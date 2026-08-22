package net.celestium.server.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Reglages du serveur : point d'apparition defini par les administrateurs et rayon du {@code /rtp}.
 *
 * <p>Remplace les {@code MapVariables} generees par MCreator, ou le point d'apparition tenait en
 * trois {@code double} plus un booleen, sans dimension associee.
 */
public class ServerData extends SavedData {

	private static final String DATA_NAME = "celestium_server";
	private static final String KEY_SPAWN_DIMENSION = "SpawnDimension";
	private static final String KEY_SPAWN_POS = "SpawnPos";
	private static final String KEY_RTP_RADIUS = "RtpRadius";

	private static final int DEFAULT_RTP_RADIUS = 10000;

	@Nullable
	private GlobalPos spawn;

	private int rtpRadius = DEFAULT_RTP_RADIUS;

	public static ServerData get(MinecraftServer server) {
		ServerLevel overworld = server.getLevel(Level.OVERWORLD);
		if (overworld == null) {
			throw new IllegalStateException("Le monde principal est introuvable");
		}
		return overworld.getDataStorage().computeIfAbsent(ServerData::load, ServerData::new, DATA_NAME);
	}

	public static ServerData load(CompoundTag tag) {
		ServerData data = new ServerData();
		if (tag.contains(KEY_SPAWN_DIMENSION) && tag.contains(KEY_SPAWN_POS)) {
			ResourceLocation dimensionId = ResourceLocation.tryParse(tag.getString(KEY_SPAWN_DIMENSION));
			if (dimensionId != null) {
				BlockPos pos = NbtUtils.readBlockPos(tag.getCompound(KEY_SPAWN_POS));
				data.spawn = GlobalPos.of(ResourceKey.create(Registries.DIMENSION, dimensionId), pos);
			}
		}
		data.rtpRadius = tag.contains(KEY_RTP_RADIUS) ? tag.getInt(KEY_RTP_RADIUS) : DEFAULT_RTP_RADIUS;
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		if (this.spawn != null) {
			tag.putString(KEY_SPAWN_DIMENSION, this.spawn.dimension().location().toString());
			tag.put(KEY_SPAWN_POS, NbtUtils.writeBlockPos(this.spawn.pos()));
		}
		tag.putInt(KEY_RTP_RADIUS, this.rtpRadius);
		return tag;
	}

	public Optional<GlobalPos> getSpawn() {
		return Optional.ofNullable(this.spawn);
	}

	public void setSpawn(GlobalPos spawn) {
		this.spawn = spawn;
		this.setDirty();
	}

	public int getRtpRadius() {
		return this.rtpRadius;
	}

	public void setRtpRadius(int radius) {
		this.rtpRadius = Math.max(1, radius);
		this.setDirty();
	}
}
