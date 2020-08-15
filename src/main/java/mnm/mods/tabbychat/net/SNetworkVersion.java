package mnm.mods.tabbychat.net;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.client.gui.NotificationToast;
import mnm.mods.tabbychat.mixin.MixinClientConnection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.thread.ThreadExecutor;

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

    public void handle(Supplier<Context> context) {
        context.get().enqueueWork(() -> {

            if (!version.equals(TabbyChat.PROTOCOL_VERSION)) {
                Text title = new TranslatableText("tabbychat.network.mismatch");
                MinecraftClient.getInstance().getToastManager().add(new NotificationToast("TabbyChat", title));
            }
        });
        context.get().setPacketHandled(true);
    }

    public static class Context
    {
        private final ClientConnection networkManager;

        /**
         * The {@link NetworkSide} this message has been received on.
         */
        private final NetworkSide networkDirection;

        /**
         * The packet dispatcher for this event. Sends back to the origin.
         */
        private final PacketDispatcher packetDispatcher;
        private boolean packetHandled;

        /*Context(ClientConnection netHandler, NetworkSide networkDirection, int index)
        {
            this(netHandler, networkDirection, new PacketDispatcher.NetworkManagerDispatcher(netHandler, index, networkDirection::buildPacket));
        }*/

        Context(ClientConnection networkManager, NetworkSide networkDirection, PacketDispatcher dispatcher) {
            this.networkManager = networkManager;
            this.networkDirection = networkDirection;
            this.packetDispatcher = dispatcher;
        }

        public NetworkSide getDirection(boolean otherSide) {
            if(otherSide)
            {
                switch (networkDirection)
                {
                    case CLIENTBOUND:
                        return NetworkSide.SERVERBOUND;
                    case SERVERBOUND:
                        return NetworkSide.CLIENTBOUND;
                }
            }
            return networkDirection;
        }

        public PacketDispatcher getPacketDispatcher() {
            return packetDispatcher;
        }

        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return ((MixinClientConnection)networkManager).getChannel().attr(key);
        }

        public void setPacketHandled(boolean packetHandled) {
            this.packetHandled = packetHandled;
        }

        public boolean getPacketHandled()
        {
            return packetHandled;
        }

        public CompletableFuture<Void> enqueueWork(Runnable runnable) {
            ThreadExecutor<?> executor = LogicalSidedProvider.WORKQUEUE.get(getDirection(true));
            // Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
            // Same logic as ThreadTaskExecutor#runImmediately without the join
            if (!executor.isOnThread()) {
                return executor.submit(runnable); // Use the internal method so thread check isn't done twice
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
            PacketListener netHandler = networkManager.getPacketListener();
            if (netHandler instanceof ServerPlayNetworkHandler)
            {
                ServerPlayNetworkHandler netHandlerPlayServer = (ServerPlayNetworkHandler) netHandler;
                return netHandlerPlayServer.player;
            }
            return null;
        }

        public ClientConnection getNetworkManager() {
            return networkManager;
        }
    }
}
