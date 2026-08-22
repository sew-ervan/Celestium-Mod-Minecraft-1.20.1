package net.celestium.client;

import net.celestium.server.data.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Reception des paquets cote client.
 *
 * <p>Cette classe n'est chargee que sur le client, via {@code DistExecutor} : un serveur dedie ne
 * dispose pas de {@link Minecraft} et planterait au chargement de classe.
 */
@OnlyIn(Dist.CLIENT)
public final class ClientPacketHandler {

	private ClientPacketHandler() {
	}

	public static void applyPlayerData(@Nullable CompoundTag data) {
		if (data == null) {
			return;
		}
		Player player = Minecraft.getInstance().player;
		if (player != null) {
			ModCapabilities.of(player).load(data);
		}
	}
}
