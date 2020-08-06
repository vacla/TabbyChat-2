package mnm.mods.tabbychat.client.gui.component;

import mnm.mods.tabbychat.util.Color;
import mnm.mods.tabbychat.util.ILocation;
import mnm.mods.tabbychat.util.Location;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * A checkbox, representing a boolean input.
 *
 * @author Matthew
 */
public class GuiCheckbox extends GuiButton implements IGuiInput<Boolean> {

    private boolean value;

    public GuiCheckbox() {
        super("");
        this.setLocation(new Location(0, 0, 9, 9));
        setSecondaryColor(Color.of(0x99ffffa0));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float parTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();

        ILocation loc = getLocation();
        GuiUtils.drawContinuousTexturedBox(WIDGETS, loc.getXPos(), loc.getYPos(), 0, 46, loc.getWidth(), loc.getHeight(), 200, 20, 2, 3, 2, 2, this.getZOffset());

        if (this.getValue()) {
            this.drawCenteredString(matrixStack, mc.textRenderer, "x", loc.getXCenter() + 1, loc.getYPos() + 1, getSecondaryColorProperty().getHex());
        }
        this.drawTextWithShadow(matrixStack, mc.textRenderer, new LiteralText(getText()), loc.getXWidth() + 2, loc.getYPos() + 2, getPrimaryColorProperty().getHex());
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        setValue(!getValue());
    }

}
