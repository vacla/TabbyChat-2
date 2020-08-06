package mnm.mods.tabbychat.util.text;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

import java.util.Optional;

public class FancyFontRenderer extends DrawableHelper
{

    private final TextRenderer fontRenderer;

    public FancyFontRenderer(TextRenderer fr) {
        this.fontRenderer = fr;
    }

    public void drawChat(MatrixStack matrixStack, MutableText chat, float x, float y) {
        this.drawChat(matrixStack, chat, x, y, true);
    }

    public void drawChat(MatrixStack matrixStack, MutableText chat, float x, float y, boolean shadow) {
        drawChat(matrixStack, chat, x, y, -1, shadow);
    }

    public void drawChat(MatrixStack matrixStack, MutableText chat, float x, float y, int color) {
        this.drawChat(matrixStack, chat, x, y, color, true);
    }

    public void drawChat(MatrixStack matrixStack, MutableText chat, float x, float y, int color, boolean shadow) {

        final float[] x1 = {x};
        float finalY = y;
        chat.visit((style, text) -> {
            MutableText message = new LiteralText(text).setStyle(style);
            if (message instanceof FancyTextComponent) {
                FancyTextComponent fcc = (FancyTextComponent) message;
                for (String s : text.split("\r?\n")) {
                    int length = fontRenderer.getWidth(s);
                    fill(matrixStack, (int) x1[0], (int) finalY, (int) x1[0] + length, (int) finalY - fontRenderer.fontHeight, fcc.getFancyStyle().getHighlight().getHex());
                    drawHorizontalLine(matrixStack, (int) x1[0], (int) x1[0] + length, (int) finalY + fontRenderer.fontHeight - 1, fcc.getFancyStyle().getUnderline().getHex());
                }
            }
            x1[0] += fontRenderer.getWidth(text);
            return Optional.empty();
        }, Style.EMPTY);
        for (String s : chat.getString().split("\r?\n")) {
            if (shadow) {
                fontRenderer.drawWithShadow(matrixStack, s, x, y, color);
            }else {
                fontRenderer.draw(matrixStack, s, x, y, color);
            }
            y += fontRenderer.fontHeight;
        }
    }

}
