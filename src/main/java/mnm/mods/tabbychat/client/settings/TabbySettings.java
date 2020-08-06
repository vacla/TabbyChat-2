package mnm.mods.tabbychat.client.settings;

import mnm.mods.tabbychat.util.config.SettingsFile;

import java.io.File;

public class TabbySettings extends SettingsFile {

    public GeneralSettings general = new GeneralSettings();
    public AdvancedSettings advanced = new AdvancedSettings();

    public TabbySettings(File parent) {
        super(new File(parent, "tabbychat.json").toPath());
    }
}
