package net.celestium.feature.enchant;

import net.celestium.init.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * La table d'enchantement corrompue.
 *
 * <p>Elle ne propose rien : on lui presente un outil et elle lui donne ce qu'il peut recevoir. Une
 * hache y gagne l'abattage, une pioche ou une pelle y gagnent un niveau d'excavation. C'est le seul
 * endroit du jeu ou ces deux enchantements existent.
 *
 * <p>Pas de menu, pas de lapis, pas de bibliotheques autour. Le choix est assume : avec deux
 * enchantements et un seul chemin pour chacun, un tirage a trois propositions n'aurait rien a
 * proposer, et une interface entiere serait beaucoup d'appareillage pour un clic droit. Le prix
 * reste celui d'une table ordinaire — des niveaux d'experience, d'autant plus qu'on monte.
 */
public class CorruptedEnchantingTableBlock extends Block {

	private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

	/** Cout en niveaux du premier palier ; chaque palier suivant coute ce prix multiplie. */
	private static final int LEVEL_COST = 8;

	public CorruptedEnchantingTableBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {

		ItemStack tool = player.getItemInHand(hand);
		Enchantment enchantment = enchantmentFor(tool);

		if (enchantment == null) {
			return refuse(level, player, "message.celestium.enchant.wrong_tool");
		}

		int current = EnchantmentHelper.getItemEnchantmentLevel(enchantment, tool);
		int next = current + 1;

		if (next > enchantment.getMaxLevel()) {
			return refuse(level, player, "message.celestium.enchant.already_maxed");
		}

		int cost = LEVEL_COST * next;
		if (!player.getAbilities().instabuild && player.experienceLevel < cost) {
			return refuse(level, player, "message.celestium.enchant.too_poor");
		}

		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (!player.getAbilities().instabuild) {
			player.giveExperienceLevels(-cost);
		}

		// Poser le niveau plutot que l'ajouter : EnchantmentHelper conserve la table existante et
		// remplace la seule entree concernee, ce qui preserve les autres enchantements de l'outil.
		var enchantments = EnchantmentHelper.getEnchantments(tool);
		enchantments.put(enchantment, next);
		EnchantmentHelper.setEnchantments(enchantments, tool);

		celebrate(level, pos);
		player.displayClientMessage(Component.translatable("message.celestium.enchant.granted",
				Component.translatable(enchantment.getDescriptionId()), next), true);

		return InteractionResult.CONSUME;
	}

	/** L'enchantement que cet outil peut recevoir, ou {@code null} s'il n'en attend aucun. */
	@Nullable
	private static Enchantment enchantmentFor(ItemStack tool) {
		if (tool.getItem() instanceof AxeItem) {
			return ModEnchantments.TIMBER.get();
		}
		if (tool.getItem() instanceof PickaxeItem || tool.getItem() instanceof ShovelItem) {
			return ModEnchantments.EXCAVATION.get();
		}
		return null;
	}

	private static InteractionResult refuse(Level level, Player player, String message) {
		if (!level.isClientSide()) {
			player.displayClientMessage(Component.translatable(message), true);
			level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
					SoundSource.BLOCKS, 0.5F, 0.5F);
		}
		return InteractionResult.sidedSuccess(level.isClientSide());
	}

	private static void celebrate(Level level, BlockPos pos) {
		level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 0.7F);

		if (level instanceof ServerLevel server) {
			server.sendParticles(ParticleTypes.ENCHANT,
					pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
					40, 0.4, 0.4, 0.4, 0.6);
		}
	}
}
