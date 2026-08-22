package net.celestium.server.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Rattache un {@link PlayerData} a un joueur et le fait vivre avec sa sauvegarde. */
public class PlayerDataProvider implements ICapabilitySerializable<CompoundTag> {

	private final PlayerData data = new PlayerData();
	private final LazyOptional<PlayerData> optional = LazyOptional.of(() -> this.data);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
		return ModCapabilities.PLAYER_DATA.orEmpty(capability, this.optional);
	}

	@Override
	public CompoundTag serializeNBT() {
		return this.data.save();
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.data.load(tag);
	}

	/** Libere la reference quand le joueur disparait, pour ne pas retenir l'entite en memoire. */
	public void invalidate() {
		this.optional.invalidate();
	}
}
