package mnm.mods.tabbychat.net;

import mnm.mods.tabbychat.TCMarkers;
import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.api.Channel;
import mnm.mods.tabbychat.client.ChatManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Supplier;

public class SSendChannelMessage {

    private String channel;
    private Text message;

    public SSendChannelMessage(PacketByteBuf buffer) {
        channel = buffer.readString(20);
        message = buffer.readText();
    }

    public SSendChannelMessage(String channel, Text message) {
        this.channel = channel;
        this.message = message;
    }

    public void encode(PacketByteBuf buffer) {
        buffer.writeString(channel);
        buffer.writeText(message);
    }

    public void handle(Supplier<SNetworkVersion.Context> context) {
        context.get().enqueueWork(() -> {
            ChatManager chat = ChatManager.instance();
            Optional<Channel> chan = chat.parseChannel(channel);
            if (!chan.isPresent()) {
                TabbyChat.logger.warn(TCMarkers.NETWORK, "Server sent bad channel name: {}", channel);
                return;
            }

            chat.addMessage(chan.get(), message);
        });
        context.get().setPacketHandled(true);
    }
}
