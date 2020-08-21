package mnm.mods.tabbychat.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mnm.mods.tabbychat.client.AbstractChannel;
import mnm.mods.tabbychat.client.ChatManager;
import mnm.mods.tabbychat.client.ChatMessage;
import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.client.gui.component.GuiComponent;
import mnm.mods.tabbychat.mixin.MixinChatMessages;
import mnm.mods.tabbychat.util.ChatTextUtils;
import mnm.mods.tabbychat.util.Color;
import mnm.mods.tabbychat.util.Dim;
import mnm.mods.tabbychat.util.ILocation;
import mnm.mods.tabbychat.util.LocalVisibility;
import mnm.mods.tabbychat.util.TexturedModal;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ChatArea extends GuiComponent {

    private static final TexturedModal MODAL = new TexturedModal(ChatBox.GUI_LOCATION, 0, 14, 254, 205);

    private AbstractChannel channel;
    private int scrollPos = 0;

    public ChatArea() {
        this.setMinimumSize(new Dim(300, 160));
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scroll) {
        // One tick = 120
        if (getLocation().contains(x, y) && scroll != 0) {
            if (scroll > 1) {
                scroll = 1;
            }
            if (scroll < -1) {
                scroll = -1;
            }
            if (Screen.hasShiftDown()) {
                scroll *= 7;
            }
            scroll((int) scroll);
            return true;
        }
        return false;
    }

    @Override
    public void onClosed() {
        resetScroll();
        super.onClosed();
    }

    @Override
    public ILocation getLocation() {
        List<ChatMessage> visible = getVisibleChat();
        int height = visible.size() * mc.textRenderer.fontHeight;
        LocalVisibility vis = TabbyChatClient.getInstance().getSettings().advanced.visibility.get();

        if (mc.inGameHud.getChatHud().isChatFocused() || vis == LocalVisibility.ALWAYS) {
            return super.getLocation();
        } else if (height != 0) {
            int y = super.getLocation().getHeight() - height;
            return super.getLocation().copy().move(0, y - 2).setHeight(height + 2);
        }
        return super.getLocation();
    }

    @Override
    public boolean isVisible() {

        List<ChatMessage> visible = getVisibleChat();
        int height = visible.size() * mc.textRenderer.fontHeight;
        LocalVisibility vis = TabbyChatClient.getInstance().getSettings().advanced.visibility.get();

        return mc.options.chatVisibility != ChatVisibility.HIDDEN
                && (mc.inGameHud.getChatHud().isChatFocused() || vis == LocalVisibility.ALWAYS || height != 0);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float parTicks) {

        List<ChatMessage> visible = getVisibleChat();
        RenderSystem.enableBlend();
        float opac = (float) mc.options.chatOpacity;
        RenderSystem.color4f(1, 1, 1, opac);

        drawModalCorners(MODAL);

        setZOffset(100);
        // TODO abstracted padding
        int xPos = getLocation().getXPos() + 3;
        int yPos = getLocation().getYHeight();
        for (ChatMessage line : visible) {
            yPos -= mc.textRenderer.fontHeight;
            drawChatLine(matrixStack, line, xPos, yPos);
        }
        setZOffset(0);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    private void drawChatLine(MatrixStack matrixStack, ChatMessage line, int xPos, int yPos) {
        String text = ChatTextUtils.getMessageWithOptionalTimestamp(line).getString();
        mc.textRenderer.drawWithShadow(matrixStack, text, xPos, yPos, Color.WHITE.getHex() + (getLineOpacity(line) << 24));
    }

    public void setChannel(AbstractChannel channel) {
        this.channel = channel;
//        this.markDirty();
    }

    @Deprecated
    public void markDirty() {
        ChatManager.instance().markDirty(channel);
    }

    List<ChatMessage> getChat() {
        return ChatManager.instance().getVisible(channel, super.getLocation().getWidth() - 6);
    }

    private List<ChatMessage> getVisibleChat() {
        List<ChatMessage> lines = getChat();

        List<ChatMessage> messages = new ArrayList<>();
        int length = 0;

        int pos = getScrollPos();
        float unfoc = TabbyChatClient.getInstance().getSettings().advanced.unfocHeight.get();
        float div = mc.inGameHud.getChatHud().isChatFocused() ? 1 : unfoc;
        while (pos < lines.size() && length < super.getLocation().getHeight() * div - 10) {
            ChatMessage line = lines.get(pos);

            if (mc.inGameHud.getChatHud().isChatFocused()) {
                messages.add(line);
            } else if (getLineOpacity(line) > 3) {
                messages.add(line);
            } else {
                break;
            }

            pos++;
            length += mc.textRenderer.fontHeight;
        }

        return messages;
    }

    private int getLineOpacity(ChatMessage line) {
        LocalVisibility vis = TabbyChatClient.getInstance().getSettings().advanced.visibility.get();
        if (vis == LocalVisibility.ALWAYS)
            return 4;
        if (vis == LocalVisibility.HIDDEN && !mc.inGameHud.getChatHud().isChatFocused())
            return 0;
        int opacity = (int) (mc.options.chatOpacity * 255);

        double age = mc.inGameHud.getTicks() - line.getCounter();
        if (!mc.inGameHud.getChatHud().isChatFocused()) {
            double opacPerc = age / TabbyChatClient.getInstance().getSettings().advanced.fadeTime.get();
            opacPerc = 1.0D - opacPerc;
            opacPerc *= 10.0D;

            opacPerc = Math.max(0, opacPerc);
            opacPerc = Math.min(1, opacPerc);

            opacPerc *= opacPerc;
            opacity = (int) (opacity * opacPerc);
        }
        return opacity;
    }

    public void scroll(int scr) {
        setScrollPos(getScrollPos() + scr);
    }

    public void setScrollPos(int scroll) {
        List<ChatMessage> list = getChat();
        scroll = Math.min(scroll, list.size() - mc.inGameHud.getChatHud().getVisibleLineCount());
        scroll = Math.max(scroll, 0);

        this.scrollPos = scroll;
    }

    public int getScrollPos() {
        return scrollPos;
    }

    public void resetScroll() {
        setScrollPos(0);
    }

    @Nullable
    public Style getText(int clickX, int clickY) {
        if (mc.inGameHud.getChatHud().isChatFocused()) {
            double scale = mc.inGameHud.getChatHud().getChatScale();
            clickX = MathHelper.floor(clickX / scale);
            clickY = MathHelper.floor(clickY / scale);

            ILocation actual = getLocation();
            // check that cursor is in bounds.
            if (actual.contains(clickX, clickY)) {
                double size = mc.textRenderer.fontHeight * scale;
                double bottom = (actual.getYPos() + actual.getHeight());

                // The line to get
                int linePos = MathHelper.floor((clickY - bottom) / -size) + scrollPos;

                // Iterate through the chat component, stopping when the desired
                // x is reached.
                List<ChatMessage> list = this.getChat();
                if (linePos >= 0 && linePos < list.size()) {
                    ChatMessage chatline = list.get(linePos);
                    AtomicReference<Float> x = new AtomicReference<>((float) actual.getXPos() + 3);

                    int finalClickX = clickX;
                    ChatTextUtils.getMessageWithOptionalTimestamp(chatline).visit((style, text) -> {
                        // clean it up
                        String clean = MixinChatMessages.invokeGetRenderedChatMessage(text);
                        // get it's width, then scale it.
                        x.updateAndGet(v -> (float) (v + this.mc.textRenderer.getWidth(clean) * scale));

                        if (x.get() > finalClickX) {
                            return Optional.of(new LiteralText(text).setStyle(style));
                        }
                        return Optional.empty();
                    }, Style.EMPTY);
                }
            }
        }
        return null;
    }
}
