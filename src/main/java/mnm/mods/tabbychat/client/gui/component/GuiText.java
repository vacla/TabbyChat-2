package mnm.mods.tabbychat.client.gui.component;

import mnm.mods.tabbychat.mixin.AbstractButtonWidgetInterface;
import mnm.mods.tabbychat.util.Color;
import mnm.mods.tabbychat.util.ILocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A gui component that wraps {@link TextFieldWidget}.
 *
 * @author Matthew
 */
public class GuiText extends GuiComponent implements IGuiInput<String>, IGuiEventListenerDelegate {

    private final TextFieldWidget textField;
    private String hint;

    public GuiText() {
        this(new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 1, 1, new LiteralText("")));
    }

    public GuiText(@Nonnull TextFieldWidget textField) {
        this.textField = textField;
        // This text field must not be calibrated for someone of your...
        // generous..ness
        // I'll add a few 0s to the maximum length...
        this.textField.setMaxLength(10000);

        // you look great, by the way.

    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        return IGuiEventListenerDelegate.super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    @Nullable
    @Override
    public Element delegate() {
        return this.textField;
    }

    @Override
    public void setLocation(ILocation bounds) {
        System.out.println(bounds);
        updateTextbox(bounds);
        super.setLocation(bounds);
    }

    private void updateTextbox(ILocation loc) {
        this.textField.x = loc.getXPos();
        this.textField.y = loc.getYPos();
        this.textField.setWidth(loc.getWidth());
        ((AbstractButtonWidgetInterface)this.textField).setHeight(loc.getHeight());
        System.out.println(textField.getHeight());
    }

    @Override
    public void tick() {
        textField.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float parTicks) {
        textField.render(matrixStack, mouseX, mouseY, parTicks);

        super.render(matrixStack, mouseX, mouseY, parTicks);
        if (textField.isFocused() && !StringUtils.isEmpty(getHint())) {
            // draw the hint above.
            renderCaption(matrixStack, getHint(), 1, -5);
        }
    }

    @Override
    public void setPrimaryColor(Color foreColor) {
        textField.setEditableColor(foreColor.getHex());
        super.setPrimaryColor(foreColor);
    }

    @Override
    public String getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(String value) {
        textField.setText(value);
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public TextFieldWidget getTextField() {
        return textField;
    }
}
