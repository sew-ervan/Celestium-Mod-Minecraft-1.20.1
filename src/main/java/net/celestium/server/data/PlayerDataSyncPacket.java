package net.celestium.server.data;

import net.celestium.client.ClientPacketHandler;
import net.celestium.core.network.ModNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * Transmet les donnees d'un joueur a son client.
 *
 * <p>Le mod d'origine renvoyait ce paquet a chaque ecriture de variable : une teleportation en
 * declenchait quatre d'affilee. Ici la synchronisation est explicite et se fait aux moments qui
 * comptent — connexion, reapparition, changement de dimension, modification effective.
 */
public record PlayerDataSyncPacket(CompoundTag data) {

	public static void encode(PlayerDataSyncPacket packet, FriendlyByteBuf buffer) {
		buffer.writeNbt(packet.data());
	}

	public static PlayerDataSyncPacket decode(FriendlyByteBuf buffer) {
		return new PlayerDataSyncPacket(buffer.readNbt());
	}

	public static void handle(PlayerDataSyncPacket packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
				() -> () -> ClientPacketHandler.applyPlayerData(packet.data())));
		ctx.setPacketHandled(true);
	}

	/** Envoie l'etat courant des donnees a un joueur precis. */
	public static void sendTo(ServerPlayer player, PlayerData data) {
		ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PlayerDataSyncPacket(data.save()));
	}
}
