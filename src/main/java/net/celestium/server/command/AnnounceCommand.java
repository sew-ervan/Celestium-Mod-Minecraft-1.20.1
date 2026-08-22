package net.celestium.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;

/**
 * {@code /annonce [staff] <message>} : diffuse un message a tout le serveur.
 *
 * <p>Le niveau etait un {@code double} compare a 0 ou 1, sans autocompletion ni verification : une
 * valeur de 2 diffusait le silence. C'est desormais un sous-mot explicite.
 */
public final class AnnounceCommand {

	private AnnounceCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("annonce")
				.requires(source -> source.hasPermission(2))
				.then(Commands.argument("message", StringArgumentType.greedyString())
						.executes(context -> announce(context.getSource(),
								StringArgumentType.getString(context, "message"), false)))
				.then(Commands.literal("staff")
						.then(Commands.argument("message", StringArgumentType.greedyString())
								.executes(context -> announce(context.getSource(),
										StringArgumentType.getString(context, "message"), true)))));
	}

	private static int announce(CommandSourceStack source, String message, boolean fromStaff) {
		PlayerList players = source.getServer().getPlayerList();

		players.broadcastSystemMessage(Component.empty(), false);
		if (fromStaff) {
			players.broadcastSystemMessage(
					Component.translatable("message.celestium.announce.header").withStyle(ChatFormatting.BOLD), false);
		}
		players.broadcastSystemMessage(Component.literal(message), false);
		players.broadcastSystemMessage(Component.empty(), false);

		return 1;
	}
}
