package net.celestium.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@code /morph} et {@code /unmorph}.
 *
 * <p>Attention : ces commandes ne font rien par elles-memes. Elles delegaient deja, dans le mod
 * d'origine, a la commande {@code identity} du mod Identity — une dependance qui n'etait declaree
 * nulle part, ni dans {@code mods.toml} ni dans la documentation. Sans ce mod installe, elles
 * echouaient en silence.
 *
 * <p>La delegation est conservee, mais l'absence du mod donne desormais un message clair plutot
 * qu'aucune reaction.
 */
public final class MorphCommand {

	private static final String IDENTITY_COMMAND = "identity";

	private MorphCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("morph")
				.then(Commands.argument("entite", StringArgumentType.string())
						.executes(context -> morph(context.getSource(),
								StringArgumentType.getString(context, "entite")))));

		dispatcher.register(Commands.literal("unmorph")
				.executes(context -> delegate(context.getSource(), "identity unequip @s")));
	}

	private static int morph(CommandSourceStack source, String entity) throws CommandSyntaxException {
		source.getPlayerOrException();
		return delegate(source, "identity equip @s " + entity);
	}

	private static int delegate(CommandSourceStack source, String command) {
		if (source.getServer().getCommands().getDispatcher().getRoot().getChild(IDENTITY_COMMAND) == null) {
			source.sendFailure(Component.translatable("message.celestium.morph.unavailable"));
			return 0;
		}

		ServerPlayer player = source.getPlayer();
		if (player == null) {
			return 0;
		}

		// Les commandes deleguees s'executent avec les droits d'operateur, sinon un joueur ne
		// pourrait pas se transformer lui-meme.
		source.getServer().getCommands().performPrefixedCommand(
				source.getServer().createCommandSourceStack().withEntity(player).withPermission(4), command);
		return 1;
	}
}
