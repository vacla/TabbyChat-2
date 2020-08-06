package mnm.mods.tabbychat.client.gui.component;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mnm.mods.tabbychat.util.Dim;
import mnm.mods.tabbychat.util.ILocation;
import mnm.mods.tabbychat.util.TexturedModal;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * A {@link net.minecraft.client.gui.widget.ButtonWidget} for the GuiComponent system.
 */
public class GuiButton extends GuiComponent {

    protected static final Identifier WIDGETS = new Identifier("textures/gui/widgets.png");
    private static final TexturedModal MODAL_NORMAL = new TexturedModal(WIDGETS, 0, 66, 200, 20);
    private static final TexturedModal MODAL_HOVER = new TexturedModal(WIDGETS, 0, 86, 200, 20);
    private static final TexturedModal MODAL_DISABLE = new TexturedModal(WIDGETS, 0, 46, 200, 20);

    private String text = "";
    private SoundEvent sound;

    /**
     * Instantiates a new button with {@code text} as the display string.
     *
     * @param text The display string
     */
    public GuiButton(String text) {
        this.setText(text);
        setSound(SoundEvents.UI_BUTTON_CLICK);
    }

    @Override
    public void playDownSound(SoundManager soundHandlerIn) {
        SoundEvent sound = getSound();
        if (sound != null) {
            soundHandlerIn.play(PositionedSoundInstance.master(sound, 1.0F));
        }
    }

    /**
     * Sets the display text for this button.
     *
     * @param text The new text
     */
    public void setText(@Nullable String text) {
        if (text == null) {
            text = "";
        }
        this.text = text;
    }

    /**
     * Gets the display text for this button.
     *
     * @return The text
     */
    public String getText() {
        return this.text;
    }

    public void setSound(@Nullable SoundEvent sound) {
        this.sound = sound;
    }

    @Nullable
    public SoundEvent getSound() {
        return sound;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float parTicks) {
        TextRenderer fontrenderer = mc.textRenderer;
        ILocation bounds = getLocation();

        GlStateManager.pushMatrix();

        mc.getTextureManager().bindTexture(WIDGETS);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        boolean hovered = bounds.contains(mouseX, mouseY);

        TexturedModal modal = this.getHoverState(hovered);
        GlStateManager.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        this.drawModalCorners(modal);

        int textColor = 0xE0E0E0;

        if (!this.isEnabled()) {
            textColor = 0xA0A0A0;
        } else if (hovered) {
            textColor = 0xFFFFA0;
        }

        int x = bounds.getXCenter();
        int y = bounds.getYCenter() - fontrenderer.fontHeight / 2;

        this.drawCenteredString(matrixStack, fontrenderer, getText(), x, y, textColor);

        GlStateManager.popMatrix();
    }

    private TexturedModal getHoverState(boolean hovered) {
        TexturedModal modal = GuiButton.MODAL_NORMAL;

        if (!this.isEnabled()) {
            modal = GuiButton.MODAL_DISABLE;
        } else if (hovered) {
            modal = GuiButton.MODAL_HOVER;
        }

        return modal;
    }

    @Nonnull
    @Override
    public Dim getMinimumSize() {
        return new Dim(mc.textRenderer.getWidth(this.getText()) + 8, 20);
    }

}
