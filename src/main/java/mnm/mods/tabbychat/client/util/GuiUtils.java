package mnm.mods.tabbychat.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import mnm.mods.tabbychat.util.TexturedModal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class GuiUtils
{
    public static void drawContinuousTexturedBox(Identifier modal, int x, int y, int u, int v, int width, int height, int uw, int uh, int borderSize, int zLevel)
    {
        drawContinuousTexturedBox(modal, x, y, u, v, width, height, uw, uh, borderSize, borderSize, borderSize, borderSize, zLevel);

    }

    public static void drawContinuousTexturedBox(Identifier modal, int x, int y, int u, int v, int width, int height, int uw, int uh,
                                                 int tB, int bB, int lB, int rB, float zLevel)
    {
        MinecraftClient.getInstance().getTextureManager().bindTexture(modal);
        drawContinuousTexturedBox(x, y, u, v, width, height, uw, uh, tB, bB, lB, rB, zLevel);
    }

    public static void drawContinuousTexturedBox(int x, int y, int u, int v, int width, int height, int uw, int uh,
                                                 int tB, int bB, int lB, int rB, float zLevel)
    {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        int fillerWidth = uw - lB - rB;
        int fillerHeight = uh - tB - bB;
        int canvasWidth = width - lB - rB;
        int canvasHeight = height - tB - bB;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;

        // Draw Border
        // Top Left
        drawTexturedModalRect(x, y, u, v, lB, tB, zLevel);
        // Top Right
        drawTexturedModalRect(x + lB + canvasWidth, y, u + lB + fillerWidth, v, rB, tB, zLevel);
        // Bottom Left
        drawTexturedModalRect(x, y + tB + canvasHeight, u, v + tB + fillerHeight, lB, bB, zLevel);
        // Bottom Right
        drawTexturedModalRect(x + lB + canvasWidth, y + tB + canvasHeight, u + lB + fillerWidth, v + tB + fillerHeight, rB, bB, zLevel);

        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++)
        {
            // Top Border
            drawTexturedModalRect(x + lB + (i * fillerWidth), y, u + lB, v, (i == xPasses ? remainderWidth : fillerWidth), tB, zLevel);
            // Bottom Border
            drawTexturedModalRect(x + lB + (i * fillerWidth), y + tB + canvasHeight, u + lB, v + tB + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bB, zLevel);

            // Throw in some filler for good measure
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
                drawTexturedModalRect(x + lB + (i * fillerWidth), y + tB + (j * fillerHeight), u + lB, v + tB, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight), zLevel);
        }

        // Side Borders
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
        {
            // Left Border
            drawTexturedModalRect(x, y + tB + (j * fillerHeight), u, v + tB, lB, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
            // Right Border
            drawTexturedModalRect(x + lB + canvasWidth, y + tB + (j * fillerHeight), u + lB + fillerWidth, v + tB, rB, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
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
