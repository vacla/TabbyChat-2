package mnm.mods.tabbychat.client.extra.filters;

import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.api.filters.Filter;
import mnm.mods.tabbychat.api.filters.FilterEvent;
import mnm.mods.tabbychat.api.filters.FilterSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.RegEx;

public class UserFilter implements Filter {

    private String name = "New Filter";
    private FilterSettings settings = new FilterSettings();
    private String pattern = ".*";

    private transient Pattern expression;

    public void setPattern(@RegEx String pattern) throws PatternSyntaxException {
        testPatternUnsafe(pattern);
        this.pattern = pattern;
        this.expression = null;
    }

    void testPattern(String pattern) throws UserPatternException {
        try {
            testPatternUnsafe(pattern);
        } catch (PatternSyntaxException e) {
            throw new UserPatternException(e);
        }
    }

    private void testPatternUnsafe(String pattern) throws PatternSyntaxException {
        Pattern.compile(resolvePattern(pattern));
    }

    @SuppressWarnings("MagicConstant")
    @Override
    public Pattern getPattern() {
        if (expression == null) {
            this.expression = Pattern.compile(resolvePattern(pattern), settings.getFlags());
        }
        return expression;
    }

    private String resolvePattern(String pattern) {
        String resolved = resolveVariables(pattern);
        if (!settings.isRegex()) {
            resolved = Pattern.quote(resolved);
        }
        return resolved;
    }

    public String getRawPattern() {
        return this.pattern;
    }

    @RegEx
    private static String resolveVariables(String pattern) {
        Matcher matcher = Pattern.compile("\\$\\{([\\w\\d]+)}").matcher(pattern);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String var = FilterAddon.getVariable(key);
            matcher.appendReplacement(buffer, var);
        }
        matcher.appendTail(buffer);

        return pattern;
    }

    @Override
    public void action(FilterEvent event) {

        if (settings.isRemove()) {
            // remove
            event.channels.clear();
        }
        // add channels
        for (String name : settings.getChannels()) {
            // replace group tokens in channel name
            Matcher matcher = Pattern.compile("\\$(\\d+)").matcher(name);
            while (matcher.find()) {
                // find groups
                int group = Integer.parseInt(matcher.group(1));
                if (group > 0 && event.matcher.groupCount() >= group) {
                    String groupText = event.matcher.group(group);
                    if (groupText != null) {
                        name = name.replace(matcher.group(), groupText);
                        continue;
                    }
                }
                name = null;
                break;
            }
            // skip this because there were missing or out of bounds groups.
            if (name == null)
                continue;

            TabbyChatClient.getInstance().getChatManager().parseChannel(name).ifPresent(event.channels::add);

        }
        // play sound
        if (settings.isSoundNotification()) {
            settings.getSoundName()
                    .map(Identifier::new)
                    .map(Registry.SOUND_EVENT::getOrEmpty)
                    .map(sndEvent -> PositionedSoundInstance.master(sndEvent.get(), 1.0F))
                    .ifPresent(MinecraftClient.getInstance().getSoundManager()::play);

        }
    }

    @Override
    public String prepareText(Text string) {
        return settings.isRaw() ? string.getString() : Filter.super.prepareText(string);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public FilterSettings getSettings() {
        return settings;
    }

    class UserPatternException extends Exception {
        private UserPatternException(PatternSyntaxException e) {
            super(e);
        }
    }
}
