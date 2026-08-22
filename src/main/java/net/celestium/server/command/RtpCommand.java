package net.celestium.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.celestium.server.data.ModCapabilities;
import net.celestium.server.data.PlayerData;
import net.celestium.server.data.PlayerDataSyncPacket;
import net.celestium.server.data.ServerData;
import net.celestium.server.teleport.WarmupTeleport;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import java.time.LocalDate;

/**
 * {@code /rtp} : teleportation aleatoire, une fois par jour et par joueur.
 *
 * <p>Le mod d'origine confiait le deplacement a la commande vanilla {@code spreadplayers}, dont il
 * construisait la ligne par concatenation de chaines — puis affichait cette ligne au joueur, un
 * reste de debogage jamais retire. Il stockait la date sous forme de texte {@code "yyyy-MM-dd"},
 * reformate a chaque comparaison. Ici le tirage se fait directement, et la date est un numero de
 * jour.
 */
public final class RtpCommand {

	private static final int WARMUP_TICKS = 100;

	/** Nombre d'essais avant d'abandonner la recherche d'un point d'arrivee sur la terre ferme. */
	private static final int MAX_ATTEMPTS = 32;

	private RtpCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("rtp").executes(context -> randomTeleport(context.getSource())));
	}

	private static int randomTeleport(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();

		if (player.level().dimension() != Level.OVERWORLD) {
			source.sendFailure(Component.translatable("message.celestium.rtp.wrong_dimension"));
			return 0;
		}

		PlayerData data = ModCapabilities.of(player);
		long today = LocalDate.now().toEpochDay();
		if (data.hasUsedRtpOn(today)) {
			source.sendFailure(Component.translatable("message.celestium.rtp.already_used"));
			return 0;
		}

		ServerData serverData = ServerData.get(source.getServer());
		ServerLevel level = source.getServer().getLevel(Level.OVERWORLD);
		if (level == null) {
			return 0;
		}

		BlockPos centre = serverData.getSpawn()
				.map(GlobalPos::pos)
				.orElseGet(level::getSharedSpawnPos);

		source.sendSuccess(() -> Component.translatable("message.celestium.teleport.warmup",
				WARMUP_TICKS / 20), false);

		WarmupTeleport.request(player, WARMUP_TICKS, teleported -> {
			BlockPos destination = findSurface(level, centre, serverData.getRtpRadius());
			teleported.teleportTo(level, destination.getX() + 0.5, destination.getY(), destination.getZ() + 0.5,
					teleported.getYRot(), teleported.getXRot());

			PlayerData current = ModCapabilities.of(teleported);
			current.setLastRtpDay(today);
			PlayerDataSyncPacket.sendTo(teleported, current);

			teleported.displayClientMessage(
					Component.translatable("message.celestium.rtp.success").withStyle(ChatFormatting.GREEN), true);
		});
		return 1;
	}

	/**
	 * Tire une position au hasard dans le rayon et remonte a la surface.
	 *
	 * <p>Renvoie le dernier point teste si aucun essai ne tombe sur la terre ferme : mieux vaut une
	 * arrivee au-dessus de l'eau qu'une commande qui ne fait rien.
	 */
	private static BlockPos findSurface(ServerLevel level, BlockPos centre, int radius) {
		RandomSource random = level.getRandom();
		BlockPos last = centre;

		for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
			int x = centre.getX() + random.nextInt(radius * 2 + 1) - radius;
			int z = centre.getZ() + random.nextInt(radius * 2 + 1) - radius;
			int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

			last = new BlockPos(x, y, z);
			if (!level.getBlockState(last.below()).getFluidState().isEmpty()) {
				continue;
			}
			return last;
		}
		return last;
	}
}
