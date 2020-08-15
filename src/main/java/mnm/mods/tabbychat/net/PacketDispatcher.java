package mnm.mods.tabbychat.net;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class PacketDispatcher {
    BiConsumer<Identifier, PacketByteBuf> packetSink;

    PacketDispatcher(final BiConsumer<Identifier, PacketByteBuf> packetSink) {
        this.packetSink = packetSink;
    }

    private PacketDispatcher() {

    }

    public void sendPacket(Identifier resourceLocation, PacketByteBuf buffer) {
        packetSink.accept(resourceLocation, buffer);
    }

    static class NetworkManagerDispatcher extends PacketDispatcher {
        private final ClientConnection manager;
        private final int packetIndex;
        private final BiFunction<Pair<PacketByteBuf, Integer>, Identifier, Packet<?>> customPacketSupplier;

        NetworkManagerDispatcher(ClientConnection manager, int packetIndex, BiFunction<Pair<PacketByteBuf, Integer>, Identifier, Packet<?>> customPacketSupplier) {
            super();
            this.packetSink = this::dispatchPacket;
            this.manager = manager;
            this.packetIndex = packetIndex;
            this.customPacketSupplier = customPacketSupplier;
        }

        private void dispatchPacket(final Identifier resourceLocation, final PacketByteBuf buffer) {
            final Packet<?> packet = this.customPacketSupplier.apply(Pair.of(buffer, packetIndex), resourceLocation);
            this.manager.send(packet);
        }
    }
}