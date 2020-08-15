package mnm.mods.tabbychat.client.extra.filters;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mnm.mods.tabbychat.client.ChatManager;
import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.api.events.ChatMessageEvent.ChatReceivedEvent;
import mnm.mods.tabbychat.api.filters.Filter;
import mnm.mods.tabbychat.api.filters.FilterEvent;
import mnm.mods.tabbychat.client.settings.ServerSettings;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterAddon {

    private static Map<String, Optional<Supplier<String>>> variables = Maps.newHashMap();

    private List<Filter> filters = Lists.newArrayList();

    public FilterAddon(ChatManager chat) {
        filters.add(new ChannelFilter(chat));
        filters.add(new MessageFilter(chat));

        variables.clear();

        MinecraftClient mc = MinecraftClient.getInstance();
        setVariable("player", () -> Pattern.quote(mc.getSession().getUsername()));
        setVariable("onlineplayer", () -> Joiner.on('|')
                .appendTo(new StringBuilder("(?:"), mc.getNetworkHandler().getPlayerList().stream()
                        .map(player -> Pattern.quote(player.getProfile().getName()))
                        .iterator())
                .append(')').toString()
        );
    }

    private static void setVariable(String key, Supplier<String> supplier) {
        variables.put(key, Optional.of(supplier));
    }

    static String getVariable(String key) {
        return variables.getOrDefault(key, Optional.empty())
                .map(Supplier::get)
                .orElse("");
    }

    public void onChatRecieved(ChatReceivedEvent message) {
        ServerSettings settings = TabbyChatClient.getInstance().getServerSettings();
        if (settings == null) {
            // We're possibly not in game.
            return;
        }

        for (Filter filter : Iterables.concat(filters, settings.filters)) {
            Matcher matcher = filter.getPattern().matcher(filter.prepareText(message.text));
            while (matcher.find()) {
                FilterEvent event = new FilterEvent(matcher, message.channels, message.text);
                filter.action(event);
                message.text = event.text; // Set the new chat
                message.channels = event.channels; // Add new channels.
            }
        }
    }
}
