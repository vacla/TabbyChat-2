package mnm.mods.tabbychat;

import com.mojang.brigadier.CommandDispatcher;
import io.netty.channel.Channel;
import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.command.TCTellCommand;
import mnm.mods.tabbychat.net.SNetworkVersion;
import mnm.mods.tabbychat.net.SSendChannelMessage;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

//@Mod(TabbyChat.MODID)
public class TabbyChat implements ClientModInitializer
{

    public static final String MODID = "tabbychat";

    public static final Logger logger = LogManager.getLogger(MODID);

    public static final File dataFolder = new File(new File(MinecraftClient.getInstance().runDirectory, "config"),MODID);

    public static final String PROTOCOL_VERSION = "1";

    //private static Channel channel = initNetwork();
    //private static Identifier versionChannel = initVersionNetwork();

    @Override
    public void onInitializeClient() {
        new TabbyChatClient(dataFolder);
    }

    /*@SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        versionChannel.sendTo(new SNetworkVersion(PROTOCOL_VERSION), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }*/

    /*private static Channel initNetwork() {
        logger.info(TCMarkers.NETWORK, "Initializing network");

        // put the version in the name so clients without that version will ignore any packets
        Identifier id = new Identifier(MODID, "channel-v" + PROTOCOL_VERSION);
        Channel channel = newChannel(id, PROTOCOL_VERSION);

        channel.messageBuilder(SSendChannelMessage.class, 0)
                .encoder(SSendChannelMessage::encode)
                .decoder(SSendChannelMessage::new)
                .consumer(SSendChannelMessage::handle)
                .add();

        return channel;
    }

    private static SimpleChannel initVersionNetwork() {
        Identifier id = new Identifier(MODID, "version");
        SimpleChannel channel = newChannel(id, "1");

        channel.messageBuilder(SNetworkVersion.class, 0)
                .encoder(SNetworkVersion::encode)
                .decoder(SNetworkVersion::new)
                .consumer(SNetworkVersion::handle)
                .add();

        return channel;
    }

    private static Channel newChannel(Identifier key, String version) {
        return NetworkRegistry.ChannelBuilder
                .named(key)
                .networkProtocolVersion(() -> version)
                // The network is optional, allow everyone
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
    }

    public static void sendTo(ServerPlayerEntity player, String channel, Text text) {
        TabbyChat.channel.sendTo(new SSendChannelMessage(channel, text), player.networkHandler.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }*/

}
