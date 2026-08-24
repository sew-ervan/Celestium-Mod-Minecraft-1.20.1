package net.celestium.feature.enchant;

import net.celestium.CelestiumMod;
import net.celestium.init.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Ce qui arrive en plus quand un outil enchante casse un bloc.
 *
 * <p>Les deux enchantements du mod partagent leur mecanique : ils determinent une liste de blocs
 * supplementaires et les cassent comme si le joueur l'avait fait lui-meme, avec le butin, l'usure
 * et l'experience qui vont avec.
 *
 * <p>Un verrou empeche la recursion. Casser un bloc en declenche l'evenement, donc un abattage qui
 * casserait ses rondins par la voie normale relancerait un abattage sur chacun d'eux, et ainsi de
 * suite jusqu'a la pile d'appels. Le verrou est propre au fil d'execution : le serveur en a un
 * seul pour le monde, mais un test peut en avoir plusieurs.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class ChainBreaking {

	/**
	 * Nombre maximal de blocs emportes par un abattage.
	 *
	 * <p>Un arbre geant de jungle en compte moins de deux cents ; au-dela, c'est qu'on a affaire a
	 * une construction en rondins, et la limite evite d'en raser un quartier d'un coup de hache.
	 */
	private static final int TIMBER_LIMIT = 256;

	/** Empeche un bloc casse par l'enchantement d'en declencher un autre. */
	private static final ThreadLocal<Boolean> BUSY = ThreadLocal.withInitial(() -> false);

	private ChainBreaking() {
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if (event.isCanceled() || BUSY.get()) {
			return;
		}
		if (!(event.getPlayer() instanceof ServerPlayer player) || player.isCreative()) {
			return;
		}
		if (!(event.getLevel() instanceof Level level)) {
			return;
		}

		ItemStack tool = player.getItemBySlot(EquipmentSlot.MAINHAND);
		BlockPos origin = event.getPos();
		BlockState state = event.getState();

		Set<BlockPos> extra = extraBlocks(level, player, tool, origin, state);
		if (extra.isEmpty()) {
			return;
		}

		BUSY.set(true);
		try {
			breakAll(level, player, tool, extra);
		} finally {
			BUSY.set(false);
		}
	}

	/** Les blocs a emporter en plus de celui qu'on vient de casser. */
	private static Set<BlockPos> extraBlocks(Level level, Player player, ItemStack tool,
			BlockPos origin, BlockState state) {

		if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.TIMBER.get(), tool) > 0
				&& state.is(BlockTags.LOGS)) {
			return trunk(level, origin, state);
		}

		int excavation = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.EXCAVATION.get(), tool);
		if (excavation > 0 && tool.isCorrectToolForDrops(state)) {
			return square(level, player, tool, origin, excavation);
		}

		return Set.of();
	}

	/**
	 * Le tronc entier, de proche en proche.
	 *
	 * <p>Le parcours accepte les diagonales : les arbres du jeu de base ont des troncs qui se
	 * decalent d'un bloc — les acacias, les chenes noueux — et un parcours strictement orthogonal
	 * s'arreterait au premier coude.
	 *
	 * <p>Seuls les rondins de la meme essence sont emportes. Sans ce filtre, un chene pousse contre
	 * un bouleau ferait tomber les deux.
	 */
	private static Set<BlockPos> trunk(Level level, BlockPos origin, BlockState state) {
		Set<BlockPos> found = new HashSet<>();
		Deque<BlockPos> pending = new ArrayDeque<>();
		pending.add(origin);

		while (!pending.isEmpty() && found.size() < TIMBER_LIMIT) {
			BlockPos current = pending.removeFirst();

			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					for (int dz = -1; dz <= 1; dz++) {
						BlockPos next = current.offset(dx, dy, dz);

						if (next.equals(origin) || found.contains(next)) {
							continue;
						}
						if (!level.getBlockState(next).is(state.getBlock())) {
							continue;
						}

						found.add(next);
						pending.add(next);
					}
				}
			}
		}

		return found;
	}

	/**
	 * Le carre creuse autour du bloc vise.
	 *
	 * <p>Le plan du carre suit la face regardee : creuser un mur donne un carre vertical, creuser
	 * le sol un carre horizontal. C'est ce qu'on attend en creusant, et cela evite de percer le
	 * plafond en voulant elargir un couloir.
	 *
	 * <p>Ne sont emportes que les blocs que l'outil sait deja casser. Un carre qui emporterait la
	 * terre autour d'un filon ferait de l'excavation une pelle universelle.
	 */
	private static Set<BlockPos> square(Level level, Player player, ItemStack tool, BlockPos origin,
			int enchantLevel) {

		int radius = ExcavationEnchantment.radiusFor(enchantLevel);
		Direction facing = Direction.orderedByNearest(player)[0];

		Set<BlockPos> found = new HashSet<>();

		for (int a = -radius; a <= radius; a++) {
			for (int b = -radius; b <= radius; b++) {
				BlockPos target = switch (facing.getAxis()) {
					case Y -> origin.offset(a, 0, b);
					case X -> origin.offset(0, a, b);
					case Z -> origin.offset(a, b, 0);
				};

				if (target.equals(origin)) {
					continue;
				}

				BlockState state = level.getBlockState(target);
				if (state.isAir() || state.getDestroySpeed(level, target) < 0.0F) {
					continue;
				}
				if (!tool.isCorrectToolForDrops(state)) {
					continue;
				}

				found.add(target);
			}
		}

		return found;
	}

	/**
	 * Casse la liste, en s'arretant si l'outil rend l'ame.
	 *
	 * <p>Chaque bloc coute un point d'usure, comme s'il avait ete casse a la main. Un enchantement
	 * qui creuserait quatre-vingt-un blocs pour le prix d'un rendrait l'usure des outils sans objet.
	 */
	private static void breakAll(Level level, ServerPlayer player, ItemStack tool, Set<BlockPos> targets) {
		for (BlockPos target : targets) {
			if (tool.isEmpty() || (tool.isDamageableItem()
					&& tool.getDamageValue() >= tool.getMaxDamage() - 1)) {
				return;
			}

			// destroyBlock cote serveur s'occupe du butin, de l'experience et de l'usure.
			player.gameMode.destroyBlock(target);
		}
	}
}
