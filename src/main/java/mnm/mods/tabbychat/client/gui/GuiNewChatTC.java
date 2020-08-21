package mnm.mods.tabbychat.client.gui;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import mnm.mods.tabbychat.TCMarkers;
import mnm.mods.tabbychat.client.AbstractChannel;
import mnm.mods.tabbychat.client.ChatManager;
import mnm.mods.tabbychat.client.DefaultChannel;
import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.api.ChannelStatus;
import mnm.mods.tabbychat.api.events.ChatMessageEvent.ChatReceivedEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiNewChatTC extends ChatHud
{

    private final MinecraftClient mc;
    private final TabbyChatClient tc;
    private final ChatBox chatbox;

    private int prevScreenWidth;
    private int prevScreenHeight;

    private static GuiNewChatTC instance;

    public GuiNewChatTC(MinecraftClient minecraft, TabbyChatClient tc) {
        super(minecraft);

        instance = this;

        this.mc = minecraft;
        this.tc = tc;

        this.chatbox = new ChatBox(tc.getSettings());
        this.prevScreenHeight = mc.getWindow().getHeight();

        new GuiChatTC(chatbox);
    }

    public static GuiNewChatTC getInstance() {
        return instance;
    }

    @Deprecated
    public ChatBox getChatBox() {
        return chatbox;
    }

    @Override
    public void reset() {
        chatbox.tick();
    }

    @Override
    public void clear(boolean sent) {
        checkThread(() -> {
            ChatManager.instance().clearMessages();
            if (sent) {
                this.getMessageHistory().clear();
            }
        });
    }

    @Override
    public void render(MatrixStack matrixStack, int i) {
        if (prevScreenHeight != mc.getWindow().getHeight() || prevScreenWidth != mc.getWindow().getWidth()) {

            chatbox.onScreenHeightResize(prevScreenWidth, prevScreenHeight, mc.getWindow().getWidth(), mc.getWindow().getHeight());

            prevScreenWidth = mc.getWindow().getWidth();
            prevScreenHeight = mc.getWindow().getHeight();
        }

        if (isChatFocused())
            return;

        double scale = mc.options.chatScale;

        RenderSystem.popMatrix(); // ignore what GuiIngame did.
        RenderSystem.pushMatrix();

        // Scale it accordingly
        RenderSystem.scaled(scale, scale, 1.0D);

        int mouseX = (int) mc.mouse.getX();
        int mouseY = (int) (-mc.mouse.getY() - 1);
        chatbox.render(matrixStack, mouseX, mouseY, 0);

        RenderSystem.popMatrix();
        RenderSystem.pushMatrix(); // push to avoid gl errors
    }

    @Override
    public void addMessage(Text ichat, int id) {
        checkThread(() -> this.addMessage((MutableText)ichat, id));
    }

    public void addMessage(MutableText ichat, int id) {
        // chat listeners
        ChatReceivedEvent chatevent = new ChatReceivedEvent(ichat, id);
        chatevent.channels.add(DefaultChannel.INSTANCE);
        //MinecraftForge.EVENT_BUS.post(chatevent);
        // chat filters
        ichat = chatevent.text;
        id = chatevent.id;
        if (ichat != null && !ichat.getString().isEmpty()) {
            if (id != 0) {
                // send removable msg to current channel
                chatevent.channels.clear();
                chatevent.channels.add(this.chatbox.getActiveChannel());
            }
            if (chatevent.channels.contains(DefaultChannel.INSTANCE) && chatevent.channels.size() > 1
                    && !tc.getServerSettings().general.useDefaultTab.get()) {
                chatevent.channels.remove(DefaultChannel.INSTANCE);
            }
            boolean msg = !chatevent.channels.contains(this.chatbox.getActiveChannel());
            final Set<String> ignored = Sets.newHashSet(this.tc.getServerSettings().general.ignoredChannels.get());
            Set<AbstractChannel> channels = chatevent.channels.stream()
                    .filter(it -> !ignored.contains(it.getName()))
                    .map(AbstractChannel.class::cast) // FIXME cast shouldn't be needed. Remove ASAP
                    .collect(Collectors.toSet());
            for (AbstractChannel channel : channels) {
                ChatManager.instance().addMessage(channel, ichat, id);
                if (msg) {
                    chatbox.setStatus(channel, ChannelStatus.UNREAD);
                }
            }
            TabbyChat.logger.info(TCMarkers.CHATBOX, "[CHAT] " + ichat.getString());
            this.chatbox.tick();
        }
    }

    private void checkThread(Runnable runnable) {
        if (!mc.isOnThread()) {
            mc.execute(runnable);
            TabbyChat.logger.warn(TCMarkers.CHATBOX, "Tried to modify chat from thread {}. To prevent a crash, it has been scheduled on the main thread.", Thread.currentThread().getName(), new Exception());
        } else {
            runnable.run();
        }
    }

    @Override
    public void resetScroll() {
        chatbox.getChatArea().resetScroll();
        super.resetScroll();
    }

    @Nonnull
    @Override
    public List<String> getMessageHistory() {
        return super.getMessageHistory();
    }

    @Override
    @Nullable
    public Style getText(double clickX, double clickY) {
        return chatbox.getChatArea().getText((int) clickX, (int) clickY);
    }

    @Override
    public int getHeight() {
        return chatbox.getChatArea().getLocation().getHeight();
    }

    @Override
    public int getWidth() {
        return chatbox.getChatArea().getLocation().getWidth();
    }

}
