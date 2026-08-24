package net.celestium.feature.enchant;

import net.celestium.init.ModEnchantments;
import net.celestium.init.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Le menu de la table corrompue.
 *
 * <p>Un seul emplacement, pour l'outil. La table n'a rien a tirer au sort : elle regarde ce qu'on
 * lui presente et enumere ce que cet outil peut recevoir. Le choix se fait par le bouton, dont
 * l'indice remonte au serveur par {@link #clickMenuButton} — le meme chemin que la table du jeu de
 * base, sans paquet a ecrire.
 *
 * <p>L'outil reste dans l'emplacement pendant l'enchantement et en ressort enchante. C'est plus
 * lisible que de le prendre en main : on voit ce qu'on paie.
 */
public class CorruptedEnchantingMenu extends AbstractContainerMenu {

	public static final int IMAGE_WIDTH = 176;
	public static final int IMAGE_HEIGHT = 225;

	/** Cout en niveaux du premier palier ; chaque palier suivant coute ce prix multiplie. */
	public static final int LEVEL_COST = 8;

	/** Nombre de propositions que l'ecran sait afficher. */
	public static final int MAX_OFFERS = 5;

	private static final int TOOL_SLOT_X = 16;
	private static final int TOOL_SLOT_Y = 60;

	private final Container tool = new SimpleContainer(1) {
		@Override
		public void setChanged() {
			super.setChanged();
			CorruptedEnchantingMenu.this.slotsChanged(this);
		}
	};

	private final ContainerLevelAccess access;
	private final Player player;

	public CorruptedEnchantingMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, ContainerLevelAccess.NULL);
	}

	public CorruptedEnchantingMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
		super(ModMenus.CORRUPTED_ENCHANTING.get(), containerId);
		this.access = access;
		this.player = playerInventory.player;

		this.addSlot(new Slot(this.tool, 0, TOOL_SLOT_X, TOOL_SLOT_Y) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return !offersFor(stack).isEmpty();
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9,
						8 + column * 18, IMAGE_HEIGHT - 82 + row * 18));
			}
		}
		for (int column = 0; column < 9; column++) {
			this.addSlot(new Slot(playerInventory, column, 8 + column * 18, IMAGE_HEIGHT - 24));
		}
	}

	/** L'outil actuellement presente. */
	public ItemStack tool() {
		return this.tool.getItem(0);
	}

	/**
	 * Ce que l'outil presente peut encore recevoir.
	 *
	 * <p>La liste exclut ce qui est deja au maximum : proposer un palier qu'on ne peut pas prendre
	 * n'apprend rien et invite au clic inutile.
	 */
	public static List<Enchantment> offersFor(ItemStack stack) {
		List<Enchantment> offers = new ArrayList<>();
		if (stack.isEmpty()) {
			return offers;
		}

		for (Enchantment candidate : candidatesFor(stack)) {
			int current = EnchantmentHelper.getItemEnchantmentLevel(candidate, stack);
			if (current < candidate.getMaxLevel()) {
				offers.add(candidate);
			}
		}
		return offers;
	}

	/** Les enchantements que ce type d'outil accepte, au maximum ou non. */
	private static List<Enchantment> candidatesFor(ItemStack stack) {
		if (stack.getItem() instanceof SwordItem) {
			return List.of(ModEnchantments.THUNDERSTRIKE.get());
		}
		if (stack.getItem() instanceof AxeItem) {
			return List.of(ModEnchantments.TIMBER.get(), ModEnchantments.MAGNETISM.get());
		}
		if (stack.getItem() instanceof PickaxeItem) {
			return List.of(ModEnchantments.EXCAVATION.get(), ModEnchantments.VEIN_MINER.get(),
					ModEnchantments.SMELTING.get(), ModEnchantments.MAGNETISM.get(),
					ModEnchantments.MIDAS_CURSE.get());
		}
		if (stack.getItem() instanceof ArmorItem armor && armor.getType() == ArmorItem.Type.HELMET) {
			return List.of(ModEnchantments.TAMER.get());
		}
		if (stack.getItem() instanceof ShovelItem) {
			return List.of(ModEnchantments.EXCAVATION.get(), ModEnchantments.SMELTING.get(),
					ModEnchantments.MAGNETISM.get());
		}
		if (stack.getItem() instanceof HoeItem) {
			return List.of(ModEnchantments.HARVEST.get(), ModEnchantments.MAGNETISM.get());
		}
		return List.of();
	}

	/** Prix du prochain palier de cet enchantement sur cet outil. */
	public static int costOf(ItemStack stack, Enchantment enchantment) {
		return LEVEL_COST * (EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) + 1);
	}

	@Override
	public boolean clickMenuButton(Player player, int index) {
		ItemStack stack = this.tool();
		List<Enchantment> offers = offersFor(stack);

		if (index < 0 || index >= offers.size()) {
			return false;
		}

		Enchantment chosen = offers.get(index);
		int cost = costOf(stack, chosen);

		if (!player.getAbilities().instabuild && player.experienceLevel < cost) {
			return false;
		}

		if (!player.getAbilities().instabuild) {
			player.giveExperienceLevels(-cost);
		}

		// Poser le niveau plutot que l'ajouter : la table existante est conservee et seule l'entree
		// concernee change, ce qui preserve les autres enchantements de l'outil.
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		enchantments.put(chosen, EnchantmentHelper.getItemEnchantmentLevel(chosen, stack) + 1);
		EnchantmentHelper.setEnchantments(enchantments, stack);

		this.tool.setChanged();
		this.access.execute(CorruptedEnchantingTableBlock::celebrate);

		return true;
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(this.access, player, ModMenus.tableBlock());
	}

	/** L'outil presente revient au joueur si l'interface se ferme. */
	@Override
	public void removed(Player player) {
		super.removed(player);
		this.access.execute((level, pos) -> this.clearContainer(player, this.tool));
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = this.slots.get(index);
		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getItem();
		ItemStack original = stack.copy();

		if (index == 0) {
			if (!this.moveItemStackTo(stack, 1, this.slots.size(), true)) {
				return ItemStack.EMPTY;
			}
		} else if (!this.moveItemStackTo(stack, 0, 1, false)) {
			return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return original;
	}
}
