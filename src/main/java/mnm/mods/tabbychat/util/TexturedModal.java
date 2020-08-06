package mnm.mods.tabbychat.util;

import net.minecraft.util.Identifier;

/**
 * Defines a modal with a resource location, u and v values, a width and a
 * height.
 */
public class TexturedModal {

    private final Identifier resource;
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;

    public TexturedModal(Identifier resource, int xPos, int yPos, int width, int height) {
        this.resource = resource;
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
    }

    public Identifier getResourceLocation() {
        return resource;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
