package net.celestium.feature.backpack;

import net.celestium.init.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Menu d'un sac celeste, valable pour les trois tailles.
 *
 * <p>Le mod d'origine enumerait chaque emplacement avec ses coordonnees en dur, dans trois classes
 * distinctes. La grille se calcule ici a partir du palier.
 */
public class BackpackMenu extends AbstractContainerMenu {

	public static final int SLOT_SIZE = 18;
	public static final int IMAGE_WIDTH = 176;

	/** Bordure haute de la texture de coffre, au-dessus de la premiere rangee. */
	public static final int TOP_BORDER = 17;

	/** Hauteur du bloc d'inventaire du joueur dans la texture de coffre. */
	public static final int PLAYER_SECTION_HEIGHT = 96;

	private static final int INVENTORY_LEFT = 8;
	private static final int GRID_TOP = 18;

	private final BackpackTier tier;
	private final InteractionHand hand;
	private final ItemStack container;

	/** Constructeur cote client : le palier arrive par le reseau. */
	public BackpackMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
		this(containerId, playerInventory, buffer.readEnum(BackpackTier.class), buffer.readEnum(InteractionHand.class));
	}

	public BackpackMenu(int containerId, Inventory playerInventory, BackpackTier tier, InteractionHand hand) {
		super(ModMenus.BACKPACK.get(), containerId);
		this.tier = tier;
		this.hand = hand;
		this.container = playerInventory.player.getItemInHand(hand);

		IItemHandler handler = this.container.getCapability(ForgeCapabilities.ITEM_HANDLER)
				.orElseGet(() -> new net.minecraftforge.items.ItemStackHandler(tier.size()));

		addBackpackSlots(handler);
		addPlayerSlots(playerInventory);
	}

	/** Hauteur totale de l'ecran, deduite du nombre de rangees. */
	public static int imageHeight(BackpackTier tier) {
		return tier.rows() * SLOT_SIZE + TOP_BORDER + PLAYER_SECTION_HEIGHT;
	}

	private void addBackpackSlots(IItemHandler handler) {
		// La grille est centree : un sac de trois emplacements ne doit pas se coller a gauche.
		int left = (IMAGE_WIDTH - this.tier.columns() * SLOT_SIZE) / 2;

		for (int index = 0; index < this.tier.size(); index++) {
			int column = index % this.tier.columns();
			int row = index / this.tier.columns();
			this.addSlot(new SlotItemHandler(handler, index,
					left + column * SLOT_SIZE, GRID_TOP + row * SLOT_SIZE));
		}
	}

	private void addPlayerSlots(Inventory playerInventory) {
		// Memes reperes que le coffre vanilla, mesures depuis le bas de l'ecran.
		int height = imageHeight(this.tier);
		int top = height - 82;

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9,
						INVENTORY_LEFT + column * SLOT_SIZE, top + row * SLOT_SIZE));
			}
		}

		int hotbarTop = height - 24;
		for (int column = 0; column < 9; column++) {
			this.addSlot(new HotbarSlot(playerInventory, column, INVENTORY_LEFT + column * SLOT_SIZE, hotbarTop));
		}
	}

	public BackpackTier getTier() {
		return this.tier;
	}

	@Override
	public boolean stillValid(Player player) {
		// Le sac doit rester en main : le poser ou le jeter ferme l'interface.
		return player.getItemInHand(this.hand) == this.container;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = this.slots.get(index);
		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getItem();
		ItemStack original = stack.copy();
		int backpackSlots = this.tier.size();

		if (index < backpackSlots) {
			if (!this.moveItemStackTo(stack, backpackSlots, this.slots.size(), true)) {
				return ItemStack.EMPTY;
			}
		} else if (!this.moveItemStackTo(stack, 0, backpackSlots, false)) {
			return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return original;
	}

	/** L'emplacement qui tient le sac ouvert ne peut pas etre vide de son sac. */
	private final class HotbarSlot extends Slot {

		private HotbarSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean mayPickup(Player player) {
			return this.getItem() != BackpackMenu.this.container;
		}
	}
}
