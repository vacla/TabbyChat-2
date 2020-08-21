package mnm.mods.tabbychat.client.gui;

import com.google.common.collect.ImmutableSet;
import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.client.AbstractChannel;
import mnm.mods.tabbychat.client.ChatManager;
import mnm.mods.tabbychat.client.DefaultChannel;
import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.api.Channel;
import mnm.mods.tabbychat.api.ChannelStatus;
import mnm.mods.tabbychat.api.events.MessageAddedToChannelEvent;
import mnm.mods.tabbychat.client.UserChannel;
import mnm.mods.tabbychat.client.settings.ServerSettings;
import mnm.mods.tabbychat.client.settings.TabbySettings;
import mnm.mods.tabbychat.client.util.ScaledDimension;
import mnm.mods.tabbychat.mixin.MixinChatScreenInterface;
import mnm.mods.tabbychat.mixin.MixinCommandSuggestor;
import mnm.mods.tabbychat.mixin.MixinScreenImpl;
import mnm.mods.tabbychat.util.ILocation;
import mnm.mods.tabbychat.util.Location;
import mnm.mods.tabbychat.util.Vec2i;
import mnm.mods.tabbychat.client.gui.component.layout.BorderLayout;
import mnm.mods.tabbychat.client.gui.component.GuiPanel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Rect2i;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class ChatBox extends GuiPanel {

    public static final Identifier GUI_LOCATION = new Identifier(TabbyChat.MODID, "textures/chatbox.png");

    private static ChatBox instance;

    private ChatArea chatArea;
    private ChatTray pnlTray;
    private TextBox txtChatInput;

    private boolean dragMode;
    private Vec2i drag;
    private Location tempbox;

    private List<AbstractChannel> channels = new ArrayList<>();
    private AbstractChannel active = DefaultChannel.INSTANCE;
    private Map<Channel, ChannelStatus> channelStatus = new HashMap<>();

    private ChatScreen chat;

    public ChatBox(TabbySettings settings) {
        super(new BorderLayout());
        instance = this;
        this.add(pnlTray = new ChatTray(), BorderLayout.Position.NORTH);
        this.add(chatArea = new ChatArea(), BorderLayout.Position.CENTER);
        this.add(txtChatInput = new TextBox(), BorderLayout.Position.SOUTH);
        this.add(new Scrollbar(chatArea), BorderLayout.Position.EAST);

        super.setLocation(settings.advanced.getChatboxLocation());

        this.channels.add(DefaultChannel.INSTANCE);
        this.pnlTray.addChannel(DefaultChannel.INSTANCE);

        this.setStatus(DefaultChannel.INSTANCE, ChannelStatus.ACTIVE);

        super.tick();

        //MinecraftForge.EVENT_BUS.addListener(this::messageScroller);
        //MinecraftForge.EVENT_BUS.addListener(this::addChatMessage);
    }

    public static ChatBox getInstance() {
        return instance;
    }

    public void update(ChatScreen chat) {
        this.chat = chat;
        if (((MixinCommandSuggestor)((MixinChatScreenInterface)chat).getCommandSuggestor()).getWindow() != null && ((MixinCommandSuggestor.MixinSuggestionWindow)((MixinCommandSuggestor)((MixinChatScreenInterface)chat).getCommandSuggestor()).getWindow()).getArea() instanceof TCRect)
        {
            ((MixinCommandSuggestor.MixinSuggestionWindow)((MixinCommandSuggestor)((MixinChatScreenInterface)chat).getCommandSuggestor()).getWindow()).setArea(new TCRect(((MixinCommandSuggestor.MixinSuggestionWindow)((MixinCommandSuggestor)((MixinChatScreenInterface)chat).getCommandSuggestor()).getWindow()).getArea()));
        }
    }

    private void messageScroller(MessageAddedToChannelEvent.Post event) {

        // compensate scrolling
        ChatArea chatbox = getChatArea();
        if (getActiveChannel() == event.getChannel() && chatbox.getScrollPos() > 0 && event.getId() == 0) {
            chatbox.scroll(1);
        }
    }

    private void addChatMessage(MessageAddedToChannelEvent.Post event) {
        AbstractChannel channel = (AbstractChannel) event.getChannel();
        addChannel(channel);
        setStatus(channel, ChannelStatus.UNREAD);
    }

    public void addChannels(Collection<AbstractChannel> active) {
        active.forEach(this::addChannel);
    }

    public Set<AbstractChannel> getChannels() {
        return ImmutableSet.copyOf(this.channels);
    }

    private void addChannel(AbstractChannel channel) {
        if (!this.channels.contains(channel)) {
            this.channels.add(channel);
            pnlTray.addChannel(channel);
            ChatManager.instance().save();
        }

    }

    public void removeChannel(AbstractChannel channel) {
        if (channels.contains(channel) && channel != DefaultChannel.INSTANCE) {
            channels.remove(channel);
            pnlTray.removeChannel(channel);
        }
        if (getActiveChannel() == channel) {
            setActiveChannel(DefaultChannel.INSTANCE);
        }
        ChatManager.instance().save();
    }

    @Nullable
    ChannelStatus getStatus(Channel chan) {
        return channelStatus.get(chan);
    }

    public void setStatus(AbstractChannel chan, @Nullable ChannelStatus status) {
        this.channelStatus.compute(chan, (key, old) -> {
            if (status == null || old == null || status.ordinal() < old.ordinal()) {
                return status;
            }
            return old;
        });
        if (status == ChannelStatus.ACTIVE) {
            chatArea.setChannel(chan);
        }
    }

    public void clearMessages() {
        this.channels.removeIf(Predicate.isEqual(DefaultChannel.INSTANCE).negate());

        this.pnlTray.clearMessages();
        setStatus(DefaultChannel.INSTANCE, ChannelStatus.ACTIVE);
    }

    public AbstractChannel getActiveChannel() {
        return active;
    }

    public void setActiveChannel(AbstractChannel channel) {
        TextBox text = this.txtChatInput;

        if (active.isPrefixHidden()
                ? text.getText().trim().isEmpty()
                : text.getText().trim().equals(active.getPrefix())) {
            // text is the prefix, so remove it.
            text.setText("");
            if (!channel.isPrefixHidden() && !channel.getPrefix().isEmpty()) {
                // target has prefix visible
                text.getTextField().getTextField().setText(channel.getPrefix() + " ");
            }
        }
        // set max text length
        boolean hidden = channel.isPrefixHidden();
        int prefLength = hidden ? channel.getPrefix().length() + 1 : 0;

        text.getTextField().getTextField().setMaxLength(ChatManager.MAX_CHAT_LENGTH - prefLength);

        // reset scroll
        // TODO per-channel scroll settings?
        if (channel != active) {
            getChatArea().resetScroll();
        }
        setStatus(active, null);
        active = channel;
        setStatus(active, ChannelStatus.ACTIVE);

    }

    /*private ServerSettings server() {
        return TabbyChatClient.getInstance().getServerSettings();
    }

    private void runActivationCommand(AbstractChannel channel) {
        String cmd = channel.getCommand();
        if (cmd.isEmpty()) {


            String pat;
            if (channel instanceof UserChannel) {
                pat = server().general.messageCommand.get();
            } else {
                pat = server().general.channelCommand.get();
            }
            if (pat.isEmpty()) {
                return;
            }
            String name = channel.getName();
            if (channel == DefaultChannel.INSTANCE) {
                name = server().general.defaultChannel.get();
            }
            // insert the channel name
            cmd = pat.replace("{}", name);

        }
        if (cmd.startsWith("/")) {
            if (cmd.length() > ChatManager.MAX_CHAT_LENGTH) {
                cmd = cmd.substring(0, ChatManager.MAX_CHAT_LENGTH);
            }
            MinecraftClient.getInstance().player.sendChatMessage(cmd);
        }
    }*/

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float parTicks) {
        handleDragging(mouseX, mouseY);

        if (mc.inGameHud.getChatHud().isChatFocused()) {
            update(chat);
            ((MixinChatScreenInterface)chat).getCommandSuggestor().render(matrixStack, mouseX, mouseY);

            Style itextcomponent = this.mc.inGameHud.getChatHud().getText(mouseX, mouseY);
            if (itextcomponent != null && itextcomponent.getHoverEvent() != null) {
                ((MixinScreenImpl)chat).invokeRenderTextHoverEffect(matrixStack, itextcomponent, mouseX, mouseY);
            }
        }
        super.render(matrixStack, mouseX, mouseY, parTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && (pnlTray.getLocation().contains(mouseX, mouseY)
                || Screen.hasAltDown() && getLocation().contains(mouseX, mouseY))) {
            dragMode = !pnlTray.isHandleHovered(mouseX, mouseY);
            drag = new Vec2i((int) mouseX, (int) mouseY);
            tempbox = getLocation().copy();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleDragging(double mx, double my) {
        if (drag != null) {

            if (!dragMode) {
                setLocation(new Location(
                        tempbox.getXPos(),
                        tempbox.getYPos() + (int) my - drag.y,
                        tempbox.getWidth() + (int) mx - drag.x,
                        tempbox.getHeight() - (int) my + drag.y));
                ChatManager.instance().markDirty(active);
            } else {
                setLocation(getLocation().copy()
                        .setXPos(tempbox.getXPos() + (int) mx - drag.x)
                        .setYPos(tempbox.getYPos() + (int) my - drag.y));
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (drag != null) {
            drag = null;
            tempbox = null;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        return this.chatArea.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
    }

    private ILocation normalizeLocation(ILocation bounds) {
        double scale = mc.options.chatScale;

        // original dims
        final int x = (int) (bounds.getXPos() * scale);
        final int y = (int) (bounds.getYPos() * scale);
        final int w = (int) (bounds.getWidth() * scale);
        final int h = (int) (bounds.getHeight() * scale);

        // the new dims
        int w1 = w;
        int h1 = h;
        int x1 = x;
        int y1 = y;

        final int SCREEN_W = mc.getWindow().getScaledWidth();
        final int SCREEN_H = mc.getWindow().getScaledHeight();

        final int HOTBAR = 25;

        // limits for sizes
        final int MIN_W = 50;
        final int MIN_H = 50;
        final int MAX_W = SCREEN_W;
        final int MAX_H = SCREEN_H - HOTBAR;


        // calculate width and height first
        // used to calculate max x and y
        w1 = Math.max(MIN_W, w1);
        w1 = Math.min(MAX_W, w1);
        // this is different because height anchor is at the top
        // so is affected at the bottom.
        if (h1 < MIN_H) {
            y1 -= MIN_H - h1;
            h1 = MIN_H;
        }
        if (h1 > MAX_H) {
            y1 += h1 - MAX_H;
            h1 = MAX_H;
        }

        // limits for position
        final int MIN_X = 0;
        final int MIN_Y = 0;
        final int MAX_X = SCREEN_W - w1;
        final int MAX_Y = SCREEN_H - h1 - HOTBAR;

        // calculate x and y coordinates
        x1 = Math.max(MIN_X, x1);
        x1 = Math.min(MAX_X, x1);
        y1 = Math.max(MIN_Y, y1);
        y1 = Math.min(MAX_Y, y1);

        // reset the location if it changed.
        if (x1 != x || y1 != y || w1 != w || h1 != h) {
            bounds = new Location(
                    MathHelper.ceil(x1 / scale),
                    MathHelper.ceil(y1 / scale),
                    MathHelper.ceil(w1 / scale),
                    MathHelper.ceil(h1 / scale));
        }

        return bounds;
    }

    @Override
    public void setLocation(ILocation location) {

        location = normalizeLocation(location);

        if (!getLocation().equals(location)) {
            super.setLocation(location);
            // save bounds
            TabbySettings sett = TabbyChatClient.getInstance().getSettings();
            sett.advanced.chatX.set(location.getXPos());
            sett.advanced.chatY.set(location.getYPos());
            sett.advanced.chatW.set(location.getWidth());
            sett.advanced.chatH.set(location.getHeight());
            sett.save();
        }
    }

    @Override
    public void onClosed() {
        super.onClosed();
        tick();
    }

    @Nullable
    @Override
    public Element getFocused() {
        return txtChatInput;
    }

    public ChatArea getChatArea() {
        return this.chatArea;
    }

    public ChatTray getTray() {
        return this.pnlTray;
    }

    public TextBox getChatInput() {
        return this.txtChatInput;
    }

    public void onScreenHeightResize(int oldWidth, int oldHeight, int newWidth, int newHeight) {

        if (oldWidth == 0 || oldHeight == 0)
            return; // first time!

        // measure the distance from the bottom, then subtract from new height

        ScaledDimension oldDim = new ScaledDimension(oldWidth, oldHeight);
        ScaledDimension newDim = new ScaledDimension(newWidth, newHeight);

        int bottom = oldDim.getScaledHeight() - getLocation().getYPos();
        int y = newDim.getScaledHeight() - bottom;
        this.setLocation(getLocation().copy().setYPos(y));
        this.tick();
    }

    private class TCRect extends Rect2i
    {

        private final Rect2i parent;

        private TCRect(Rect2i parent) {
            super(0, 0, 0, 0);
            this.parent = parent;
        }

        @Override
        public int getX() {
            return Math.max(0, Math.min(parent.getX() + getLocation().getXPos(), chat.width - parent.getWidth()));
        }

        @Override
        public int getY() {
            return getLocation().getYHeight() - parent.getHeight() - 14 * getChatInput().getWrappedLines().size();
        }

        @Override
        public int getWidth() {
            return parent.getWidth();
        }

        @Override
        public int getHeight() {
            return parent.getHeight();
        }

        @Override
        public boolean contains(int x, int y) {
            return x >= this.getX() && x <= this.getX() + this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight();
        }
    }

}
