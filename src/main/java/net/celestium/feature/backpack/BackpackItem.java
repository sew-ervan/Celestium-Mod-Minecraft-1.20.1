package net.celestium.feature.backpack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

/** Sac celeste : un inventaire portatif, dont la taille depend du palier. */
public class BackpackItem extends Item {

	private final BackpackTier tier;

	public BackpackItem(BackpackTier tier) {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON));
		this.tier = tier;
	}

	public BackpackTier getTier() {
		return this.tier;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new BackpackInventory(this.tier);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);

		if (player instanceof ServerPlayer serverPlayer) {
			MenuProvider provider = new SimpleMenuProvider(
					(id, inventory, owner) -> new BackpackMenu(id, inventory, this.tier, hand),
					Component.translatable(this.getDescriptionId()));

			// Le palier voyage avec l'ouverture : c'est lui qui dicte la taille du menu et de
			// l'ecran, cote client comme serveur.
			NetworkHooks.openScreen(serverPlayer, provider,
					buffer -> {
						buffer.writeEnum(this.tier);
						buffer.writeEnum(hand);
					});
		}

		return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
	}
}
