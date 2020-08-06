package mnm.mods.tabbychat.client.extra.filters;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import mnm.mods.tabbychat.api.filters.FilterSettings;
import mnm.mods.tabbychat.client.gui.component.GuiButton;
import mnm.mods.tabbychat.client.gui.component.GuiCheckbox;
import mnm.mods.tabbychat.client.gui.component.GuiLabel;
import mnm.mods.tabbychat.client.gui.component.GuiPanel;
import mnm.mods.tabbychat.client.gui.component.GuiText;
import mnm.mods.tabbychat.client.gui.component.layout.GuiGridLayout;
import mnm.mods.tabbychat.util.Color;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.stream.Collectors;

import static mnm.mods.tabbychat.util.Translation.*;

public class GuiFilterEditor extends GuiPanel {

    private class ToggleButton extends GuiButton {
        private boolean active;

        private ToggleButton(String text) {
            super(text);
        }

        @Override
        public String getText() {
            String text = super.getText();
            Formatting color = active ? Formatting.GREEN : Formatting.RED;
            return color + text;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            active ^= true;
        }
    }

    private UserFilter filter;

    private GuiText txtName;
    private GuiCheckbox chkRemove;
    private GuiText txtDestinations;
    private GuiCheckbox chkSound;
    private GuiText txtSound;
    private GuiText txtPattern;
    private GuiLabel lblError;

    private ToggleButton btnRegexp;
    private ToggleButton btnIgnoreCase;
    private ToggleButton btnRaw;

    public GuiFilterEditor(UserFilter filter) {
        this.setLayout(new GuiGridLayout(20, 15));
        this.filter = filter;

        String pattern = filter.getRawPattern();
        FilterSettings settings = filter.getSettings();

        int pos = 0;

        this.add(new GuiLabel(new TranslatableText(FILTER_NAME)), new int[]{1, pos});
        this.add(txtName = new GuiText(), new int[]{5, pos, 10, 1});
        txtName.setValue(filter.getName());

        pos += 2;
        this.add(new GuiLabel(new TranslatableText(FILTER_DESTINATIONS)), new int[]{1, pos});
        this.add(txtDestinations = new GuiText(), new int[]{8, pos, 10, 1});
        txtDestinations.setValue(Joiner.on(", ").join(settings.getChannels()));
        txtDestinations.setCaption(new TranslatableText(FILTER_DESTIONATIONS_DESC));

        pos += 1;
        this.add(btnRegexp = new ToggleButton(".*"), new int[]{1, pos, 2, 1});
        btnRegexp.active = filter.getSettings().isRegex();
        btnRegexp.setCaption(new TranslatableText(FILTER_REGEX));
        this.add(btnIgnoreCase = new ToggleButton("Aa"), new int[]{3, pos, 2, 1});
        btnIgnoreCase.active = settings.isCaseInsensitive();
        btnIgnoreCase.setCaption(new TranslatableText(FILTER_IGNORE_CASE));
        this.add(btnRaw = new ToggleButton("&0"), new int[]{5, pos, 2, 1});
        btnRaw.active = settings.isRaw();
        btnRaw.setCaption(new TranslatableText(FILTER_RAW_INPUT));

        pos += 2;
        this.add(new GuiLabel(new TranslatableText(FILTER_HIDE)), new int[]{2, pos});
        this.add(chkRemove = new GuiCheckbox(), new int[]{1, pos});
        chkRemove.setValue(settings.isRemove());

        pos += 1;
        this.add(new GuiLabel(new TranslatableText(FILTER_AUDIO_NOTIFY)), new int[]{2, pos});
        this.add(chkSound = new GuiCheckbox(), new int[]{1, pos});
        chkSound.setValue(settings.isSoundNotification());

        pos += 1;
        this.add(txtSound = new GuiText() {
            private int pos;

            @Override
            public boolean charTyped(char c, int key) {
                final int max = 10;
                if (key == GLFW.GLFW_KEY_DOWN) {
                    pos++;
                } else if (key == GLFW.GLFW_KEY_UP) {
                    pos--;
                }
                // suggest sounds
                final String val = getValue().toLowerCase()
                        .substring(0, getTextField().getCursor());
                List<String> list = Registry.SOUND_EVENT.stream()
                        .map(Object::toString)
                        .filter(s -> s.contains(val))
                        .collect(Collectors.toList());

                pos = Math.min(pos, list.size() - max);
                pos = Math.max(pos, 0);
                if (list.size() > max) {
                    list = list.subList(pos, pos + max);
                }
                setHint(Joiner.on('\n').join(list));
                if ((key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) && !list.isEmpty()) {
                    setValue(list.get(0));
                    GuiFilterEditor.this.setFocused(null);
                }
                return super.charTyped(c, key);
            }
        }, new int[]{3, pos, 14, 1});
        txtSound.setValue(settings.getSoundName().orElse(""));
        txtSound.getTextField().setTextPredicate(txt -> Identifier.tryParse(txt) != null);

        final GuiButton play = new GuiButton("\u25b6");
        txtSound.getTextField().setChangedListener(s -> {
            Identifier res = Identifier.tryParse(s);
            play.setSound(Registry.SOUND_EVENT.get(res));
        });
        this.add(play, new int[]{18, pos, 2, 1});

        pos += 2;
        this.add(new GuiLabel(new TranslatableText(FILTER_EXPRESSION)), new int[]{1, pos});
        this.add(txtPattern = new GuiText() {
            @Override
            public boolean charTyped(char c, int key) {
                boolean r = super.charTyped(c, key);
                setPrimaryColor(Color.WHITE);
                lblError.setText(null);
                if (btnRegexp.active) {
                    // check valid regex
                    try {
                        filter.testPattern(getValue());
                    } catch (UserFilter.UserPatternException e) {
                        setPrimaryColor(Color.RED);
                        String string = e.getCause().getLocalizedMessage();
                        lblError.setText(new TranslatableText(string));
                    }
                }
                return r;
            }
        }, new int[]{8, pos, 12, 1});

        txtPattern.setValue(pattern == null ? "" : pattern);

        pos++;
        this.add(lblError = new GuiLabel(), new int[]{4, pos});

        GuiButton accept = new GuiButton(I18n.translate("gui.done")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                accept();
            }
        };
        this.add(accept, new int[]{5, 14, 4, 1});
    }

    private void accept() {
        filter.setName(txtName.getValue());
        filter.setPattern(txtPattern.getValue());
        FilterSettings sett = filter.getSettings();
        sett.getChannels().clear();
        sett.getChannels().addAll(Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(txtDestinations.getValue()));
        sett.setRemove(chkRemove.getValue());
        sett.setCaseInsensitive(btnIgnoreCase.active);
        sett.setRegex(btnRegexp.active);
        sett.setRaw(btnRaw.active);

        sett.setSoundNotification(chkSound.getValue());
        sett.setSoundName(txtSound.getValue());
    }

}
