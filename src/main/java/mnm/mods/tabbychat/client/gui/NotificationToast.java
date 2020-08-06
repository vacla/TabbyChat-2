package mnm.mods.tabbychat.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class NotificationToast implements Toast
{

    private String owner;
    private String title;

    private long firstDrawTime;
    private boolean newDisplay;

    public NotificationToast(String owner, Text title) {
        this.owner = owner;
        this.title = title.getString();
    }

    @Nonnull
    public Toast.Visibility draw(MatrixStack matrixStack, @Nonnull ToastManager toastGui, long delta) {
        if (this.newDisplay) {
            this.firstDrawTime = delta;
            this.newDisplay = false;
        }
        int x = 10;
        int textWidth = toastGui.getGame().textRenderer.getWidth(title);
        final long delay = 500;
        int maxSize = textWidth - 150;
        long timeElapsed = delta - firstDrawTime - delay;
        if (timeElapsed > 0 && textWidth > maxSize) {
            x = Math.max((int) (-maxSize * (timeElapsed) / (8000L) + x), -maxSize);
        }

        toastGui.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        toastGui.drawTexture(matrixStack, 0, 0, 0, 0, 160, 32);

        toastGui.getGame().textRenderer.draw(matrixStack, Formatting.UNDERLINE + this.owner, 8.0F, 6.0F, -256);

        Window window = toastGui.getGame().getWindow();
        double height = window.getScaledHeight();
        double scale = window.getScaleFactor();

        float[] trans = new float[16];
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, trans);
        float xpos = trans[12];

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) ((xpos + 10) * scale), (int) ((height - 32) * scale), (int) (140 * scale), (int) (32 * scale));

        toastGui.getGame().textRenderer.draw(matrixStack, this.title, x, 16.0F, -1);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        return delta - this.firstDrawTime < 10000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    @Override
    @Nonnull
    public String getType() {
        return this.owner;
    }
}
