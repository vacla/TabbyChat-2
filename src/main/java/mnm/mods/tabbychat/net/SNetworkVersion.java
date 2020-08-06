package mnm.mods.tabbychat.net;

import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.client.gui.NotificationToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

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

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {

            if (!version.equals(TabbyChat.PROTOCOL_VERSION)) {
                Text title = new TranslatableText("tabbychat.network.mismatch");
                MinecraftClient.getInstance().getToastManager().add(new NotificationToast("TabbyChat", title));
            }
        });
        context.get().setPacketHandled(true);
    }
}
