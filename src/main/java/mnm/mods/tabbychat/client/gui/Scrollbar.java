package mnm.mods.tabbychat.client.gui;

import mnm.mods.tabbychat.util.ILocation;
import mnm.mods.tabbychat.client.gui.component.GuiComponent;
import net.minecraft.client.util.math.MatrixStack;

public class Scrollbar extends GuiComponent {

    private ChatArea chat;

    public Scrollbar(ChatArea chat) {
        this.chat = chat;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float parTicks) {
        if (mc.inGameHud.getChatHud().isChatFocused()) {
            int scroll = chat.getScrollPos();
            int max = chat.getLocation().getHeight();
            int lines = max / mc.textRenderer.fontHeight;
            int total = chat.getChat().size();
            if (total <= lines) {
                return;
            }
            total -= lines;
            int size = Math.max(max / 2 - total, 10);
            float perc = Math.abs((float) scroll / (float) total - 1) * Math.abs((float) size / (float) max - 1);
            int pos = (int) (perc * max);

            ILocation loc = getLocation();
            fill(matrixStack, loc.getXPos(), loc.getYPos() + pos, loc.getXPos() + 1, loc.getYPos() + pos + size, -1);
            super.render(matrixStack, mouseX, mouseY, parTicks);
        }
    }

}
