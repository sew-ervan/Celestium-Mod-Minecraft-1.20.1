package net.celestium.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.celestium.server.data.ModCapabilities;
import net.celestium.server.data.PlayerData;
import net.celestium.server.data.PlayerDataSyncPacket;
import net.celestium.server.teleport.Teleporter;
import net.celestium.server.teleport.WarmupTeleport;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * {@code /sethome}, {@code /home} et {@code /delhome}.
 *
 * <p>Le mod d'origine n'exposait qu'un {@code /home <name>} dont l'argument, libre, devait valoir
 * exactement "set" ou "tp" — sans quoi la commande ne faisait rien et ne disait rien. Trois
 * commandes distinctes rendent l'intention lisible et donnent l'autocompletion.
 */
public final class HomeCommand {

	/** Duree d'immobilite exigee avant la teleportation, en ticks. */
	private static final int WARMUP_TICKS = 100;

	private HomeCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(literal("sethome").executes(context -> setHome(context.getSource())));
		dispatcher.register(literal("home").executes(context -> goHome(context.getSource())));
		dispatcher.register(literal("delhome").executes(context -> deleteHome(context.getSource())));
	}

	private static LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
		return Commands.literal(name);
	}

	private static int setHome(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		GlobalPos position = Teleporter.currentPosition(player);

		PlayerData data = ModCapabilities.of(player);
		data.setHome(position);
		PlayerDataSyncPacket.sendTo(player, data);

		source.sendSuccess(() -> Component.translatable("message.celestium.home.set",
				position.pos().getX(), position.pos().getY(), position.pos().getZ())
				.withStyle(ChatFormatting.GREEN), false);
		return 1;
	}

	private static int goHome(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		Optional<GlobalPos> home = ModCapabilities.of(player).getHome();

		if (home.isEmpty()) {
			source.sendFailure(Component.translatable("message.celestium.home.none"));
			return 0;
		}

		source.sendSuccess(() -> Component.translatable("message.celestium.teleport.warmup",
				WARMUP_TICKS / 20), false);

		WarmupTeleport.request(player, WARMUP_TICKS, teleported -> {
			if (Teleporter.teleport(teleported, home.get())) {
				teleported.displayClientMessage(
						Component.translatable("message.celestium.home.arrived").withStyle(ChatFormatting.GREEN), true);
			}
		});
		return 1;
	}

	private static int deleteHome(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		PlayerData data = ModCapabilities.of(player);
		data.setHome(null);
		PlayerDataSyncPacket.sendTo(player, data);
		source.sendSuccess(() -> Component.translatable("message.celestium.home.cleared"), false);
		return 1;
	}
}
