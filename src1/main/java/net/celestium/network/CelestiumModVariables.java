package net.celestium.network;

import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.client.Minecraft;

import net.celestium.CelestiumMod;

import java.util.function.Supplier;

import java.io.File;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CelestiumModVariables {
	public static double TPA_Acceptation = 0;
	public static File systemps = new File("");

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		CelestiumMod.addNetworkMessage(SavedDataSyncMessage.class, SavedDataSyncMessage::buffer, SavedDataSyncMessage::new, SavedDataSyncMessage::handler);
		CelestiumMod.addNetworkMessage(PlayerVariablesSyncMessage.class, PlayerVariablesSyncMessage::buffer, PlayerVariablesSyncMessage::new, PlayerVariablesSyncMessage::handler);
	}

	@SubscribeEvent
	public static void init(RegisterCapabilitiesEvent event) {
		event.register(PlayerVariables.class);
	}

	@Mod.EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level.isClientSide())
				((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
			if (!event.getEntity().level.isClientSide())
				((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getEntity().level.isClientSide())
				((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void clonePlayer(PlayerEvent.Clone event) {
			event.getOriginal().revive();
			PlayerVariables original = ((PlayerVariables) event.getOriginal().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()));
			PlayerVariables clone = ((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()));
			clone.TPA_Joueur = original.TPA_Joueur;
			clone.Commande_A_Decider = original.Commande_A_Decider;
			clone.TempStringPlayerPersistent = original.TempStringPlayerPersistent;
			clone.HomeX = original.HomeX;
			clone.HomeY = original.HomeY;
			clone.HomeZ = original.HomeZ;
			clone.HomeHasBeenSet = original.HomeHasBeenSet;
			clone.TempPlayer1 = original.TempPlayer1;
			clone.TempPlayer2 = original.TempPlayer2;
			clone.TempPlayer3 = original.TempPlayer3;
			clone.JourRTP = original.JourRTP;
			clone.HomeDimension = original.HomeDimension;
			clone.magie_equipe = original.magie_equipe;
			if (!event.isWasDeath()) {
			}
		}

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level.isClientSide()) {
				SavedData mapdata = MapVariables.get(event.getEntity().level);
				SavedData worlddata = WorldVariables.get(event.getEntity().level);
				if (mapdata != null)
					CelestiumMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(0, mapdata));
				if (worlddata != null)
					CelestiumMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getEntity().level.isClientSide()) {
				SavedData worlddata = WorldVariables.get(event.getEntity().level);
				if (worlddata != null)
					CelestiumMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "celestium_worldvars";

		public static WorldVariables load(CompoundTag tag) {
			WorldVariables data = new WorldVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level level && !level.isClientSide())
				CelestiumMod.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(level::dimension), new SavedDataSyncMessage(1, this));
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(e -> WorldVariables.load(e), WorldVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "celestium_mapvars";
		public double SpawnX = 0;
		public double SpawnY = 0;
		public double SpawnZ = 0;
		public boolean SpawnHasBeenSet = false;
		public double RtpRayon = 10000.0;

		public static MapVariables load(CompoundTag tag) {
			MapVariables data = new MapVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
			SpawnX = nbt.getDouble("SpawnX");
			SpawnY = nbt.getDouble("SpawnY");
			SpawnZ = nbt.getDouble("SpawnZ");
			SpawnHasBeenSet = nbt.getBoolean("SpawnHasBeenSet");
			RtpRayon = nbt.getDouble("RtpRayon");
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			nbt.putDouble("SpawnX", SpawnX);
			nbt.putDouble("SpawnY", SpawnY);
			nbt.putDouble("SpawnZ", SpawnZ);
			nbt.putBoolean("SpawnHasBeenSet", SpawnHasBeenSet);
			nbt.putDouble("RtpRayon", RtpRayon);
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level && !world.isClientSide())
				CelestiumMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SavedDataSyncMessage(0, this));
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(e -> MapVariables.load(e), MapVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class SavedDataSyncMessage {
		public int type;
		public SavedData data;

		public SavedDataSyncMessage(FriendlyByteBuf buffer) {
			this.type = buffer.readInt();
			this.data = this.type == 0 ? new MapVariables() : new WorldVariables();
			if (this.data instanceof MapVariables _mapvars)
				_mapvars.read(buffer.readNbt());
			else if (this.data instanceof WorldVariables _worldvars)
				_worldvars.read(buffer.readNbt());
		}

		public SavedDataSyncMessage(int type, SavedData data) {
			this.type = type;
			this.data = data;
		}

		public static void buffer(SavedDataSyncMessage message, FriendlyByteBuf buffer) {
			buffer.writeInt(message.type);
			buffer.writeNbt(message.data.save(new CompoundTag()));
		}

		public static void handler(SavedDataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer()) {
					if (message.type == 0)
						MapVariables.clientSide = (MapVariables) message.data;
					else
						WorldVariables.clientSide = (WorldVariables) message.data;
				}
			});
			context.setPacketHandled(true);
		}
	}

	public static final Capability<PlayerVariables> PLAYER_VARIABLES_CAPABILITY = CapabilityManager.get(new CapabilityToken<PlayerVariables>() {
	});

	@Mod.EventBusSubscriber
	private static class PlayerVariablesProvider implements ICapabilitySerializable<Tag> {
		@SubscribeEvent
		public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer))
				event.addCapability(new ResourceLocation("celestium", "player_variables"), new PlayerVariablesProvider());
		}

		private final PlayerVariables playerVariables = new PlayerVariables();
		private final LazyOptional<PlayerVariables> instance = LazyOptional.of(() -> playerVariables);

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return cap == PLAYER_VARIABLES_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}

		@Override
		public Tag serializeNBT() {
			return playerVariables.writeNBT();
		}

		@Override
		public void deserializeNBT(Tag nbt) {
			playerVariables.readNBT(nbt);
		}
	}

	public static class PlayerVariables {
		public String TPA_Joueur = "\"\"";
		public String Commande_A_Decider = "\"\"";
		public String TempStringPlayerPersistent = "quoicoubeh apanian t'a les crampt\u00E9s";
		public double HomeX = 0;
		public double HomeY = 0;
		public double HomeZ = 0;
		public boolean HomeHasBeenSet = false;
		public double TempPlayer1 = 0;
		public double TempPlayer2 = 0;
		public double TempPlayer3 = 0;
		public String JourRTP = "";
		public String HomeDimension = "";
		public double magie_equipe = 0;

		public void syncPlayerVariables(Entity entity) {
			if (entity instanceof ServerPlayer serverPlayer)
				CelestiumMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PlayerVariablesSyncMessage(this));
		}

		public Tag writeNBT() {
			CompoundTag nbt = new CompoundTag();
			nbt.putString("TPA_Joueur", TPA_Joueur);
			nbt.putString("Commande_A_Decider", Commande_A_Decider);
			nbt.putString("TempStringPlayerPersistent", TempStringPlayerPersistent);
			nbt.putDouble("HomeX", HomeX);
			nbt.putDouble("HomeY", HomeY);
			nbt.putDouble("HomeZ", HomeZ);
			nbt.putBoolean("HomeHasBeenSet", HomeHasBeenSet);
			nbt.putDouble("TempPlayer1", TempPlayer1);
			nbt.putDouble("TempPlayer2", TempPlayer2);
			nbt.putDouble("TempPlayer3", TempPlayer3);
			nbt.putString("JourRTP", JourRTP);
			nbt.putString("HomeDimension", HomeDimension);
			nbt.putDouble("magie_equipe", magie_equipe);
			return nbt;
		}

		public void readNBT(Tag Tag) {
			CompoundTag nbt = (CompoundTag) Tag;
			TPA_Joueur = nbt.getString("TPA_Joueur");
			Commande_A_Decider = nbt.getString("Commande_A_Decider");
			TempStringPlayerPersistent = nbt.getString("TempStringPlayerPersistent");
			HomeX = nbt.getDouble("HomeX");
			HomeY = nbt.getDouble("HomeY");
			HomeZ = nbt.getDouble("HomeZ");
			HomeHasBeenSet = nbt.getBoolean("HomeHasBeenSet");
			TempPlayer1 = nbt.getDouble("TempPlayer1");
			TempPlayer2 = nbt.getDouble("TempPlayer2");
			TempPlayer3 = nbt.getDouble("TempPlayer3");
			JourRTP = nbt.getString("JourRTP");
			HomeDimension = nbt.getString("HomeDimension");
			magie_equipe = nbt.getDouble("magie_equipe");
		}
	}

	public static class PlayerVariablesSyncMessage {
		public PlayerVariables data;

		public PlayerVariablesSyncMessage(FriendlyByteBuf buffer) {
			this.data = new PlayerVariables();
			this.data.readNBT(buffer.readNbt());
		}

		public PlayerVariablesSyncMessage(PlayerVariables data) {
			this.data = data;
		}

		public static void buffer(PlayerVariablesSyncMessage message, FriendlyByteBuf buffer) {
			buffer.writeNbt((CompoundTag) message.data.writeNBT());
		}

		public static void handler(PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer()) {
					PlayerVariables variables = ((PlayerVariables) Minecraft.getInstance().player.getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()));
					variables.TPA_Joueur = message.data.TPA_Joueur;
					variables.Commande_A_Decider = message.data.Commande_A_Decider;
					variables.TempStringPlayerPersistent = message.data.TempStringPlayerPersistent;
					variables.HomeX = message.data.HomeX;
					variables.HomeY = message.data.HomeY;
					variables.HomeZ = message.data.HomeZ;
					variables.HomeHasBeenSet = message.data.HomeHasBeenSet;
					variables.TempPlayer1 = message.data.TempPlayer1;
					variables.TempPlayer2 = message.data.TempPlayer2;
					variables.TempPlayer3 = message.data.TempPlayer3;
					variables.JourRTP = message.data.JourRTP;
					variables.HomeDimension = message.data.HomeDimension;
					variables.magie_equipe = message.data.magie_equipe;
				}
			});
			context.setPacketHandled(true);
		}
	}
}
