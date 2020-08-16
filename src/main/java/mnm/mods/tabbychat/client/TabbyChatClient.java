package mnm.mods.tabbychat.client;

import mnm.mods.tabbychat.TCMarkers;
import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.api.events.MessageAddedToChannelEvent;
import mnm.mods.tabbychat.client.core.GuiNewChatTC;
import mnm.mods.tabbychat.client.extra.ChatAddonAntiSpam;
import mnm.mods.tabbychat.client.extra.ChatLogging;
import mnm.mods.tabbychat.client.extra.filters.FilterAddon;
import mnm.mods.tabbychat.client.extra.spell.Spellcheck;
import mnm.mods.tabbychat.client.settings.ServerSettings;
import mnm.mods.tabbychat.client.settings.TabbySettings;
import mnm.mods.tabbychat.util.ChannelPatterns;
import mnm.mods.tabbychat.util.ChatTextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.text.Text;
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.FMLPaths;*/

import java.io.File;
import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.regex.Matcher;

public class TabbyChatClient {

    private static TabbyChatClient instance;
    private static ChatManager chatManager;
    private static Spellcheck spellcheck;

    private TabbySettings settings;
    private ServerSettings serverSettings;

    public static TabbyChatClient getInstance() {
        return instance;
    }

    public TabbyChatClient(File dataFolder) {
        instance = this;
        // Set global settings
        settings = new TabbySettings(dataFolder);
        settings.load();
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public static Spellcheck getSpellcheck() {
        return spellcheck;
    }

    public TabbySettings getSettings() {
        return settings;
    }

    public ServerSettings getServerSettings() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayNetworkHandler connection = mc.getNetworkHandler();
        if (connection == null) {
            serverSettings = null;
        } else {
            // this is probably InetSocketAddress
            SocketAddress address = connection.getConnection().getAddress();
            if (serverSettings == null || !serverSettings.getSocket().equals(address)) {

                // Set server settings
                serverSettings = new ServerSettings(TabbyChat.dataFolder.toPath(), address);
                onJoinServer();
            }
        }
        return serverSettings;
    }

    //@SubscribeEvent
    public static void onLoadingFinished() {

        TabbyChat.logger.info(TCMarkers.STARTUP, "Minecraft load complete!");

        MinecraftClient mc = MinecraftClient.getInstance();

        spellcheck = new Spellcheck(TabbyChat.dataFolder.toPath());        // Keeps the current language updated whenever it is changed.
        TabbyChat.logger.warn(spellcheck);
        ReloadableResourceManager irrm = (ReloadableResourceManager) mc.getResourceManager();
        irrm.registerListener(spellcheck);

        chatManager = ChatManager.instance();

        //this.removeChannelTags;
        new ChatAddonAntiSpam(chatManager);
        new FilterAddon(chatManager);
        new ChatLogging(MinecraftClient.getInstance().runDirectory.toPath().resolve("logs/chat"));
    }

    private void removeChannelTags(MessageAddedToChannelEvent.Pre event) {
        if (settings.advanced.hideTag.get() && event.getChannel() != DefaultChannel.INSTANCE) {
            ChannelPatterns pattern = getServerSettings().general.channelPattern.get();

            Text text = event.getText();
            Matcher matcher = pattern.getPattern().matcher(event.getText().getString());
            if (matcher.find()) {
                event.setText(ChatTextUtils.subChat(text, matcher.end()));
            }
        }
    }

    private void onJoinServer() {

        serverSettings.load();

        // load chat
        try {
            Path conf = getServerSettings().getPath().getParent();
            chatManager.loadFrom(conf);
        } catch (Exception e) {
            TabbyChat.logger.warn(TCMarkers.CHATBOX, "Unable to load chat data.", e);
        }

    }

    //@Mod.EventBusSubscriber(modid = TabbyChat.MODID, value = Dist.CLIENT)
    /*private static class StartListener {
        //@SubscribeEvent
        public static void onGuiOpen(TickEvent.ClientTickEvent event) {
            // Do the first tick, then unregister self.
            // essentially an on-thread startup complete listener
            MinecraftClient mc = MinecraftClient.getInstance();
            hookIntoChat(mc.inGameHud, new GuiNewChatTC(mc, instance));
            MinecraftForge.EVENT_BUS.unregister(StartListener.class);
        }
    }*/

    //@Mod.EventBusSubscriber(modid = TabbyChat.MODID, value = Dist.CLIENT)
    public static class NullScreenListener {
        // Listens for a null GuiScreen. Null means we're in-game
        // TODO workaround for lack of client network events. Replace when possible
        //@SubscribeEvent
        public static void onGuiOpen(Screen screen) {
            if (screen == null) {
                instance.getServerSettings();
            }
        }
    }

    /*private static void hookIntoChat(InGameHud guiIngame, ChatHud chat) {
        try {
            TabbyChat.logger.info(TCMarkers.STARTUP, "Successfully hooked into chat.");
        } catch (Throwable e) {
            TabbyChat.logger.fatal(TCMarkers.STARTUP, "Unable to hook into chat. This is bad.", e);
        }
    }*/
}
