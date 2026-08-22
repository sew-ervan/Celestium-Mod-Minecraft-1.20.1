package net.celestium.feature.backpack;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Contenu d'un sac celeste, attache a l'objet lui-meme.
 *
 * <p>Une seule classe pour les trois tailles, la ou le mod d'origine en avait deux identiques a la
 * taille pres. Un sac ne peut pas contenir un autre sac : sans cette regle, deux sacs se rangeant
 * l'un dans l'autre dupliquent leur contenu.
 */
public class BackpackInventory implements ICapabilitySerializable<CompoundTag> {

	private final ItemStackHandler handler;
	private final LazyOptional<ItemStackHandler> optional;

	public BackpackInventory(BackpackTier tier) {
		this.handler = new ItemStackHandler(tier.size()) {
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return !(stack.getItem() instanceof BackpackItem);
			}
		};
		this.optional = LazyOptional.of(() -> this.handler);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
		return ForgeCapabilities.ITEM_HANDLER
				.orEmpty(capability, this.optional.cast());
	}

	@Override
	public CompoundTag serializeNBT() {
		return this.handler.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.handler.deserializeNBT(tag);
	}
}
