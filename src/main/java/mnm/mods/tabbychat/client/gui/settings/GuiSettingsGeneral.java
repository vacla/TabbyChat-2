package mnm.mods.tabbychat.client.gui.settings;

import static mnm.mods.tabbychat.util.Translation.*;

import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.client.settings.GeneralSettings;
import mnm.mods.tabbychat.client.settings.TabbySettings;
import mnm.mods.tabbychat.util.TimeStamps;
import mnm.mods.tabbychat.util.Color;
import mnm.mods.tabbychat.client.gui.component.layout.GuiGridLayout;
import mnm.mods.tabbychat.client.gui.component.GuiLabel;
import mnm.mods.tabbychat.client.gui.component.config.GuiSettingBoolean;
import mnm.mods.tabbychat.client.gui.component.config.GuiSettingEnum;
import mnm.mods.tabbychat.client.gui.component.config.GuiSettingNumber.GuiSettingDouble;
import mnm.mods.tabbychat.client.gui.component.config.SettingPanel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiSettingsGeneral extends SettingPanel<TabbySettings> {

    GuiSettingsGeneral() {
        setLayout(new GuiGridLayout(10, 20));
        setDisplayString(I18n.translate(SETTINGS_GENERAL));
        setSecondaryColor(Color.of(255, 0, 255, 64));
    }

    @Override
    public void initGUI() {
        GeneralSettings sett = getSettings().general;

        int pos = 1;
        add(new GuiLabel(new TranslatableText(LOG_CHAT)), new int[] { 2, pos });
        GuiSettingBoolean chkLogChat = new GuiSettingBoolean(sett.logChat);
        chkLogChat.setCaption(new TranslatableText(LOG_CHAT_DESC));
        add(chkLogChat, new int[] { 1, pos });

        add(new GuiLabel(new TranslatableText(SPLIT_LOG)), new int[] { 7, pos });
        GuiSettingBoolean chkSplitLog = new GuiSettingBoolean(sett.splitLog);
        chkSplitLog.setCaption(new TranslatableText(SPLIT_LOG_DESC));
        add(chkSplitLog, new int[] { 6, pos });

        pos += 2;
        add(new GuiLabel(new TranslatableText(TIMESTAMP)), new int[] { 2, pos });
        add(new GuiSettingBoolean(sett.timestampChat), new int[] { 1, pos });

        pos += 2;
        add(new GuiLabel(new TranslatableText(TIMESTAMP_STYLE)), new int[] { 3, pos });
        add(new GuiSettingEnum<>(sett.timestampStyle, TimeStamps.values()), new int[] { 5, pos, 4, 1 });

        pos += 2;
        add(new GuiLabel(new TranslatableText(TIMESTAMP_COLOR)), new int[] { 3, pos });
        add(new GuiSettingEnum<>(sett.timestampColor, getColors(), GuiSettingsGeneral::getColorName), new int[] { 5, pos, 4, 1 });

        pos += 2;
        add(new GuiLabel(new TranslatableText(ANTI_SPAM)), new int[] { 2, pos });
        GuiSettingBoolean chkSpam = new GuiSettingBoolean(sett.antiSpam);
        chkSpam.setCaption(new TranslatableText(ANTI_SPAM_DESC));
        add(chkSpam, new int[] { 1, pos });

        pos += 2;
        add(new GuiLabel(new TranslatableText(SPAM_PREJUDICE)), new int[] { 3, pos });
        GuiSettingDouble nud = new GuiSettingDouble(sett.antiSpamPrejudice);
        nud.getComponent().setMin(0);
        nud.getComponent().setMax(1);
        nud.getComponent().setInterval(0.05);
        nud.getComponent().setFormat(NumberFormat.getPercentInstance());
        nud.setCaption(new TranslatableText(SPAM_PREJUDICE_DESC));
        add(nud, new int[] { 6, pos, 2, 1 });

        pos += 2;
        add(new GuiLabel(new TranslatableText(UNREAD_FLASHING)), new int[] { 2, pos });
        add(new GuiSettingBoolean(sett.unreadFlashing), new int[] { 1, pos });

        pos += 2;
        add(new GuiLabel(new TranslatableText(CHECK_UPDATES)), new int[] { 2, pos });
        add(new GuiSettingBoolean(sett.checkUpdates), new int[] { 1, pos });
    }

    private static List<Formatting> getColors() {
        return Stream.of(Formatting.values())
                .filter(Formatting::isColor)
                .collect(Collectors.toList());
    }

    private static String getColorName(Formatting input) {
        return "colors." + input.getName();
    }

    @Override
    public TabbySettings getSettings() {
        return TabbyChatClient.getInstance().getSettings();
    }

}
