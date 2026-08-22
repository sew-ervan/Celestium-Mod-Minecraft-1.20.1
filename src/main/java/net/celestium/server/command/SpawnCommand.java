package net.celestium.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.celestium.server.data.ServerData;
import net.celestium.server.teleport.Teleporter;
import net.celestium.server.teleport.WarmupTeleport;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/** {@code /spawn} : ramene le joueur au point d'apparition defini par les administrateurs. */
public final class SpawnCommand {

	private static final int WARMUP_TICKS = 100;

	private SpawnCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("spawn").executes(context -> goToSpawn(context.getSource())));
	}

	private static int goToSpawn(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		Optional<GlobalPos> spawn = ServerData.get(source.getServer()).getSpawn();

		if (spawn.isEmpty()) {
			source.sendFailure(Component.translatable("message.celestium.spawn.none"));
			return 0;
		}

		source.sendSuccess(() -> Component.translatable("message.celestium.teleport.warmup",
				WARMUP_TICKS / 20), false);

		WarmupTeleport.request(player, WARMUP_TICKS, teleported -> {
			if (Teleporter.teleport(teleported, spawn.get())) {
				teleported.displayClientMessage(
						Component.translatable("message.celestium.spawn.arrived").withStyle(ChatFormatting.GREEN), true);
			}
		});
		return 1;
	}
}
