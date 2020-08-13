package mnm.mods.tabbychat.client.gui.component;

import com.mojang.blaze3d.platform.GlStateManager;
import mnm.mods.tabbychat.util.Color;
import mnm.mods.tabbychat.util.Dim;
import mnm.mods.tabbychat.util.ILocation;
import mnm.mods.tabbychat.util.Location;
import mnm.mods.tabbychat.util.TexturedModal;
import mnm.mods.tabbychat.util.Vec2i;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringEscapeUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The base class for all gui components.
 *
 * @author Matthew
 */
public abstract class GuiComponent extends AbstractButtonWidget
{

    protected MinecraftClient mc = MinecraftClient.getInstance();

    private Color secondaryColor;
    private Color primaryColor;
    private GuiPanel parent;
    private ILocation location = new Location();
    private Dim minimumSize = new Dim(0, 0);
    private Text caption;

    public GuiComponent() {
        super(0, 0, 200, 20, new LiteralText(""));
    }

    /**
     * Draws this component on screen.
     *
     * @param mouseX   The mouse x
     * @param mouseY   The mouse y
     * @param parTicks
     */
    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float parTicks) {
    }

    public void renderCaption(MatrixStack matrixStack, int x, int y) {
        getCaption()
                .map(Text::getString)
                .filter(((Predicate<String>) String::isEmpty).negate())
                .filter(t -> this.getLocation().contains(x, y))
                .ifPresent(text -> this.renderCaption(matrixStack, text, x, y));
    }

    protected void renderCaption(MatrixStack matrixStack, String caption, int x, int y) {
        caption = StringEscapeUtils.unescapeJava(caption);
        String[] list = caption.split("\n\r?");

        int w = 0;
        // find the largest width
        for (String s : list) {
            w = Math.max(w, (int) (mc.textRenderer.getWidth(s)));
        }
        y -= mc.textRenderer.fontHeight * list.length;

        Vec2i point = getLocation().getPoint();
        int sw = mc.getWindow().getScaledWidth();
        int w2 = w;
        int x2 = x;
        while (x2 - 8 + point.x + w2 + 20 > sw) {
            x--;
            w2--;
        }
        x += getLocation().getXPos();
        y += getLocation().getYPos();
        // put it on top
        GlStateManager.pushMatrix();
        fill(matrixStack, x - 2, y - 2, x + w + 2, y + mc.textRenderer.fontHeight * list.length + 1, 0xcc333333);
        renderBorders(matrixStack, x - 2, y - 2, x + w + 2, y + mc.textRenderer.fontHeight * list.length + 1,
                0xccaaaaaa);
        for (String s : list) {
            mc.textRenderer.drawWithShadow(matrixStack, s, x, y, this.getPrimaryColorProperty().getHex());
            y += mc.textRenderer.fontHeight;
        }
        GlStateManager.popMatrix();
    }

    protected void renderBorders(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int color) {
        this.drawVerticalLine(matrixStack, x1 - 1, y1 - 1, y2 + 1, color); // left
        this.drawHorizontalLine(matrixStack, x1 - 1, x2, y1 - 1, color); // top
        this.drawVerticalLine(matrixStack, x2, y1 - 1, y2 + 1, color); // right
        this.drawHorizontalLine(matrixStack, x1, x2 - 1, y2, color); // bottom
    }

    /**
     * Draws borders around the provided points. Uses a brightened background
     * color with 0xaa transparency.
     *
     * @param x1 left point
     * @param y1 upper point
     * @param x2 right point
     * @param y2 lower point
     */
    protected void renderBorders(MatrixStack matrixStack, int x1, int y1, int x2, int y2) {
        Color color = getSecondaryColorProperty();
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        double amt = .75;
        r += lum(r, amt);
        g += lum(g, amt);
        b += lum(b, amt);
        color = Color.of(r, g, b, 0xaa);
        renderBorders(matrixStack, x1, y1, x2, y2, color.getHex());
    }

    private static int lum(int o, double amt) {
        return (int) ((255 - o) * amt);
    }

    /**
     * Updates the component. Called when it is called on the {@link Screen}.
     */
    public void tick() {
    }

    /**
     * Called when the screen is closed.
     */
    public void onClosed() {

    }

    @Override
    protected boolean clicked(double x, double y) {
        return isMouseOver(x, y);
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        return this.isEnabled() && this.isVisible() && getLocation().contains(x, y);
    }

    /**
     * Gets the current location of this component.
     *
     * @return The current immutable location.
     */
    public ILocation getLocation() {
        return this.location;
    }

    /**
     * Sets the location of this component. In order to maintain encapsulation,
     * it is wrapped as an immutable.
     *
     * @param location The new location
     */
    public void setLocation(ILocation location) {
        this.location = location.asImmutable();
    }

    /**
     * Gets the parent of this component. Will return {@code null} until it is
     * added to a panel by being used as the parameter to
     * {@link GuiPanel#add(GuiComponent)} or
     * {@link GuiPanel#add(GuiComponent, Object)}.
     *
     * @return The parent or null if there is none
     */
    public Optional<GuiPanel> getParent() {
        return Optional.ofNullable(this.parent);
    }


    /**
     * Sets the parent of this component. Should only be used by
     * {@link GuiPanel}.
     *
     * @param guiPanel The parent
     */
    void setParent(@Nullable GuiPanel guiPanel) {
        this.parent = guiPanel;
    }

    public void setMinimumSize(Dim size) {
        this.minimumSize = size;
    }

    public Dim getMinimumSize() {
        return minimumSize;
    }

    public Optional<Color> getPrimaryColor() {
        return Optional.ofNullable(this.primaryColor);
    }

    public void setPrimaryColor(@Nullable Color color) {
        this.primaryColor = color;
    }

    public Optional<Color> getSecondaryColor() {
        return Optional.ofNullable(this.secondaryColor);
    }

    public void setSecondaryColor(@Nullable Color color) {
        this.secondaryColor = color;
    }

    public Color getPrimaryColorProperty() {
        return getProperty(GuiComponent::getPrimaryColor, Color.WHITE);
    }

    public Color getSecondaryColorProperty() {
        return getProperty(GuiComponent::getSecondaryColor, Color.of(0));
    }

    /**
     * Gets if this is enabled. Disabled components will not handle mouse or
     * keyboard events.
     *
     * @return True if enabled, false if disabled.
     */
    public boolean isEnabled() {
        return active;
    }

    /**
     * Sets if this is enabled or not. Disabled components will not handle mouse
     * or keyboard events.
     *
     * @param enabled True for enabled, false for disabled
     */
    public void setEnabled(boolean enabled) {
        this.active = enabled;
    }

    /**
     * Gets if this component is visible. Non-visible components are not
     * rendered.
     *
     * @return The visibility state
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets this component's visibility. Non-visible components are not
     * rendered.
     *
     * @param visible The visibility state
     */
    public void setVisible(boolean visible) {
        if (!visible) {
            this.onClosed();
        }
        this.visible = visible;
    }

    public void setCaption(@Nullable Text text) {
        this.caption = text;
    }

    /**
     * Gets the caption which is shown when the mouse is hovering over this
     * component.
     *
     * @return The caption
     */
    public Optional<Text> getCaption() {
        return Optional.ofNullable(caption);
    }

    private <T> T getProperty(final Function<GuiComponent, Optional<T>> prop, T def) {
        return getProperty(prop).orElse(def);
    }

    private <T> Optional<T> getProperty(final Function<GuiComponent, Optional<T>> prop) {
        Optional<T> result = prop.apply(this);
        Optional<GuiPanel> parent = getParent();
        if (!result.isPresent()) {
            result = parent.flatMap((GuiComponent p) -> p.getProperty(prop));
        }
        return result;
    }

    protected void drawModalCorners(TexturedModal modal) {
        ILocation location = getLocation();
        int x = location.getXPos();
        int y = location.getYPos();
        int u = modal.getXPos();
        int v = modal.getYPos();
        int w = location.getWidth() + 1;
        int h = location.getHeight() + 1;
        int uw = modal.getWidth();
        int uh = modal.getHeight();

        MinecraftClient.getInstance().getTextureManager().bindTexture(modal.getResourceLocation());
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        int fillerWidth = uw - 2 - 2;
        int fillerHeight = uh - 2 - 2;
        int canvasWidth = width - 2 - 2;
        int canvasHeight = height - 2 - 2;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;

        // Draw Border
        // Top Left
        drawTexturedModalRect(x, y, u, v, 2, 2, getZOffset());
        // Top Right
        drawTexturedModalRect(x + 2 + canvasWidth, y, u + 2 + fillerWidth, v, 2, 2, getZOffset());
        // Bottom Left
        drawTexturedModalRect(x, y + 2 + canvasHeight, u, v + 2 + fillerHeight, 2, 2, getZOffset());
        // Bottom Right
        drawTexturedModalRect(x + 2 + canvasWidth, y + 2 + canvasHeight, u + 2 + fillerWidth, v + 2 + fillerHeight, 2, 2, getZOffset());

        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++)
        {
            // Top Border
            drawTexturedModalRect(x + 2 + (i * fillerWidth), y, u + 2, v, (i == xPasses ? remainderWidth : fillerWidth), 2, getZOffset());
            // Bottom Border
            drawTexturedModalRect(x + 2 + (i * fillerWidth), y + 2 + canvasHeight, u + 2, v + 2 + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), 2, getZOffset());

            // Throw in some filler for good measure
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
                drawTexturedModalRect(x + 2 + (i * fillerWidth), y + 2 + (j * fillerHeight), u + 2, v + 2, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight), getZOffset());
        }

        // Side Borders
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
        {
            // Left Border
            drawTexturedModalRect(x, y + 2 + (j * fillerHeight), u, v + 2, 2, (j == yPasses ? remainderHeight : fillerHeight), getZOffset());
            // Right Border
            drawTexturedModalRect(x + 2 + canvasWidth, y + 2 + (j * fillerHeight), u + 2 + fillerWidth, v + 2, 2, (j == yPasses ? remainderHeight : fillerHeight), getZOffset());
        }

    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float zLevel)
    {
        final float uScale = 1f / 0x100;
        final float vScale = 1f / 0x100;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = tessellator.getBuffer();
        wr.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        wr.vertex(x        , y + height, zLevel).texture( u          * uScale, ((v + height) * vScale)).next();
        wr.vertex(x + width, y + height, zLevel).texture((u + width) * uScale, ((v + height) * vScale)).next();
        wr.vertex(x + width, y         , zLevel).texture((u + width) * uScale, ( v           * vScale)).next();
        wr.vertex(x        , y         , zLevel).texture( u          * uScale, ( v           * vScale)).next();
        tessellator.draw();
    }
}
