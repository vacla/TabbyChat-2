package mnm.mods.tabbychat.client.gui.component;

import mnm.mods.tabbychat.util.Location;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A panel wrapper for a screen.
 */
public class ComponentScreen extends Screen {

    private final GuiPanel PANEL = new GuiPanel();

    public ComponentScreen(Text title) {
        super(title);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float tick) {
        PANEL.render(matrixStack, mouseX, mouseY, tick);
        PANEL.renderCaption(matrixStack, mouseX, mouseY);
    }

    @Override
    public void tick() {
        PANEL.tick();
    }

    @Override
    @Nonnull
    public List<? extends Element> children() {
        return PANEL.children();
    }

    @Override
    public void init(MinecraftClient mc, int width, int height) {
        PANEL.setLocation(new Location(0, 0, width, height));
        PANEL.clear();
        super.init(mc, width, height);
    }

    /**
     * Gets the main panel on this screen. Add things to this.
     * 
     * @return The main panel
     */
    protected GuiPanel getPanel() {
        return PANEL;
    }
}
