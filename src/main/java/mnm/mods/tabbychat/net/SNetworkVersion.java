package mnm.mods.tabbychat.net;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.client.gui.NotificationToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class SNetworkVersion {

    private String version;

    public SNetworkVersion(PacketByteBuf buffer) {
        version = buffer.readString(20);
    }

    public SNetworkVersion(String version) {
        this.version = version;
    }

    public void encode(PacketByteBuf buffer) {
        buffer.writeString(version);
    }

    /*public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {

            if (!version.equals(TabbyChat.PROTOCOL_VERSION)) {
                Text title = new TranslatableText("tabbychat.network.mismatch");
                MinecraftClient.getInstance().getToastManager().add(new NotificationToast("TabbyChat", title));
            }
        });
        context.get().setPacketHandled(true);
    }*/

    public static class Context
    {
        private final NetworkManager networkManager;

        /**
         * The {@link NetworkDirection} this message has been received on.
         */
        private final NetworkDirection networkDirection;

        /**
         * The packet dispatcher for this event. Sends back to the origin.
         */
        private final PacketDispatcher packetDispatcher;
        private boolean packetHandled;

        Context(NetworkManager netHandler, NetworkDirection networkDirection, int index)
        {
            this(netHandler, networkDirection, new PacketDispatcher.NetworkManagerDispatcher(netHandler, index, networkDirection.reply()::buildPacket));
        }

        Context(NetworkManager networkManager, NetworkDirection networkDirection, PacketDispatcher dispatcher) {
            this.networkManager = networkManager;
            this.networkDirection = networkDirection;
            this.packetDispatcher = dispatcher;
        }

        public NetworkDirection getDirection() {
            return networkDirection;
        }

        public PacketDispatcher getPacketDispatcher() {
            return packetDispatcher;
        }

        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return networkManager.channel().attr(key);
        }

        public void setPacketHandled(boolean packetHandled) {
            this.packetHandled = packetHandled;
        }

        public boolean getPacketHandled()
        {
            return packetHandled;
        }

        public CompletableFuture<Void> enqueueWork(Runnable runnable) {
            ThreadTaskExecutor<?> executor = LogicalSidedProvider.WORKQUEUE.get(getDirection().getReceptionSide());
            // Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
            // Same logic as ThreadTaskExecutor#runImmediately without the join
            if (!executor.isOnExecutionThread()) {
                return executor.deferTask(runnable); // Use the internal method so thread check isn't done twice
            } else {
                runnable.run();
                return CompletableFuture.completedFuture(null);
            }
        }

        /**
         * When available, gets the sender for packets that are sent from a client to the server.
         */
        @Nullable
        public ServerPlayerEntity getSender()
        {
            INetHandler netHandler = networkManager.getNetHandler();
            if (netHandler instanceof ServerPlayNetHandler)
            {
                ServerPlayNetHandler netHandlerPlayServer = (ServerPlayNetHandler) netHandler;
                return netHandlerPlayServer.player;
            }
            return null;
        }

        public NetworkManager getNetworkManager() {
            return networkManager;
        }
    }
}
