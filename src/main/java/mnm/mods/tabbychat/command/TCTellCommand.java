package mnm.mods.tabbychat.command;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mnm.mods.tabbychat.TabbyChat;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class TCTellCommand {

    private static final String TARGETS = "targets";
    private static final String CHANNEL = "channel";
    private static final String MESSAGE = "message";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tctell")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument(TARGETS, EntityArgumentType.players())
                        .then(argument(CHANNEL, StringArgumentType.string())
                                .then(argument(MESSAGE, TextArgumentType.text())
                                        .executes(TCTellCommand::execute)))));
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, TARGETS);
        /*String channel = "#" + StringArgumentType.getString(context, CHANNEL);
        Text message = TextArgumentType.getTextArgument(context, MESSAGE);

        for (ServerPlayerEntity player : players) {
            TabbyChat.send(player, channel, message);
        }*/

        return players.size();
    }

}
