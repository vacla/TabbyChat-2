package mnm.mods.tabbychat.net;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.NetworkSide;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public enum LogicalSidedProvider
{
    WORKQUEUE((c) -> c.get(), (s) -> s.get()),
    INSTANCE((c) -> c.get(), (s) -> s.get()),
    CLIENTWORLD((c) -> Optional.<World>of(c.get().world), (s) -> Optional.<World>empty());
    private static Supplier<MinecraftClient> client;
    private static Supplier<MinecraftServer> server;

    LogicalSidedProvider(Function<Supplier<MinecraftClient>, ?> clientSide, Function<Supplier<MinecraftServer>, ?> serverSide)
    {
        this.clientSide = clientSide;
        this.serverSide = serverSide;
    }

    public static void setClient(Supplier<MinecraftClient> client)
    {
        LogicalSidedProvider.client = client;
    }

    public static void setServer(Supplier<MinecraftServer> server)
    {
        LogicalSidedProvider.server = server;
    }


    private final Function<Supplier<MinecraftClient>, ?> clientSide;
    private final Function<Supplier<MinecraftServer>, ?> serverSide;

    @SuppressWarnings("unchecked")
    public <T> T get(final NetworkSide side)
    {
        return (T) (side == NetworkSide.CLIENTBOUND ? clientSide.apply(client) : serverSide.apply(server));
    }
}