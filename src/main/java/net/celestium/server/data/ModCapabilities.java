package net.celestium.server.data;

import net.celestium.CelestiumMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Enregistrement et cycle de vie des donnees joueur.
 *
 * <p>Les evenements de mort et de changement de dimension recopient explicitement les donnees :
 * sans cela, un joueur perd son home a chaque reapparition.
 */
public final class ModCapabilities {

	public static final Capability<PlayerData> PLAYER_DATA =
			CapabilityManager.get(new CapabilityToken<>() {
			});

	private ModCapabilities() {
	}

	/** Recupere les donnees d'un joueur, ou des donnees neuves si la capacite est absente. */
	public static PlayerData of(Player player) {
		return player.getCapability(PLAYER_DATA).orElseGet(PlayerData::new);
	}

	@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static final class ModBusEvents {

		private ModBusEvents() {
		}

		@SubscribeEvent
		public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
			event.register(PlayerData.class);
		}
	}

	@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
	public static final class ForgeBusEvents {

		private ForgeBusEvents() {
		}

		@SubscribeEvent
		public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer)) {
				event.addCapability(CelestiumMod.id("player_data"), new PlayerDataProvider());
			}
		}

		@SubscribeEvent
		public static void onPlayerClone(PlayerEvent.Clone event) {
			// A la mort, la capacite de l'ancien corps est invalidee : il faut la rouvrir pour
			// en lire le contenu avant de le recopier.
			event.getOriginal().reviveCaps();
			PlayerData original = of(event.getOriginal());
			of(event.getEntity()).copyFrom(original);
			event.getOriginal().invalidateCaps();
		}

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			sync(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			sync(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
			sync(event.getEntity());
		}

		private static void sync(Player player) {
			if (player instanceof ServerPlayer serverPlayer) {
				PlayerDataSyncPacket.sendTo(serverPlayer, of(serverPlayer));
			}
		}
	}
}
