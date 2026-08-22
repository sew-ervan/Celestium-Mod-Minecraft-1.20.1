package net.celestium.server.teleport;

import net.celestium.core.util.ServerScheduler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Teleportation differee : le joueur doit rester immobile un court instant.
 *
 * <p>Mecanique commune a {@code /home}, {@code /spawn} et {@code /rtp}. Le mod d'origine la
 * reecrivait dans chaque procedure, et surtout stockait la position d'attente dans les variables
 * persistantes du joueur ({@code TempPlayer1}, {@code TempPlayer2}, {@code TempPlayer3}) : une
 * donnee de cinq secondes ecrite dans la sauvegarde, synchronisee au client a chaque champ.
 *
 * <p>La comparaison de position se faisait par egalite exacte de {@code double}. Un joueur qui
 * glissait d'un millieme de bloc voyait sa teleportation refusee. Un seuil de tolerance remplace
 * ce test.
 */
public final class WarmupTeleport {

	/** Distance en blocs qu'un joueur peut parcourir sans annuler sa teleportation. */
	private static final double ALLOWED_DRIFT = 0.5;

	private static final Map<UUID, Vec3> PENDING = new HashMap<>();

	private WarmupTeleport() {
	}

	/**
	 * Demande une teleportation apres un temps d'attente.
	 *
	 * @param player     le joueur concerne
	 * @param delayTicks duree d'immobilite exigee
	 * @param onSuccess  action executee si le joueur n'a pas bouge
	 */
	public static void request(ServerPlayer player, int delayTicks, Consumer<ServerPlayer> onSuccess) {
		UUID id = player.getUUID();
		PENDING.put(id, player.position());

		ServerScheduler.schedule(delayTicks, () -> {
			Vec3 origin = PENDING.remove(id);
			if (origin == null || player.isRemoved()) {
				return;
			}
			if (player.position().distanceToSqr(origin) > ALLOWED_DRIFT * ALLOWED_DRIFT) {
				player.displayClientMessage(Component.translatable("message.celestium.teleport.moved")
						.withStyle(ChatFormatting.RED), false);
				return;
			}
			onSuccess.accept(player);
		});
	}

	/** Annule l'attente en cours d'un joueur, s'il y en a une. */
	public static void cancel(ServerPlayer player) {
		PENDING.remove(player.getUUID());
	}
}
