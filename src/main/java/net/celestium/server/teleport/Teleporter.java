package net.celestium.server.teleport;

import net.celestium.CelestiumMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Teleportation d'un joueur, y compris d'une dimension a l'autre.
 *
 * <p>Le mod d'origine reimplementait le changement de dimension a la main dans chaque procedure :
 * il envoyait au client un paquet {@code WIN_GAME} — celui du generique de fin — puis rejouait
 * lui-meme les effets actifs et les capacites du joueur. {@code ServerPlayer#teleportTo} fait tout
 * cela correctement depuis toujours.
 */
public final class Teleporter {

	private Teleporter() {
	}

	/** Deplace le joueur vers la position visee, en changeant de dimension si besoin. */
	public static boolean teleport(ServerPlayer player, GlobalPos target) {
		ServerLevel level = player.server.getLevel(target.dimension());
		if (level == null) {
			CelestiumMod.LOGGER.warn("Dimension introuvable pour la teleportation : {}", target.dimension().location());
			return false;
		}
		BlockPos pos = target.pos();
		player.teleportTo(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot(), player.getXRot());
		return true;
	}

	/** Position actuelle du joueur, dimension comprise. */
	public static GlobalPos currentPosition(ServerPlayer player) {
		return GlobalPos.of(player.level().dimension(), player.blockPosition());
	}
}
