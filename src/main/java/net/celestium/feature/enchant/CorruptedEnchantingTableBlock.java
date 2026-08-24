package net.celestium.feature.enchant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * La table d'enchantement corrompue.
 *
 * <p>Elle ne tire rien au sort. On lui presente un outil, elle enumere ce qu'il peut recevoir, et
 * chaque proposition affiche en clair son enchantement, son palier et son prix. L'alphabet illisible
 * de la table du jeu de base n'a de sens que pour cacher un tirage ; il n'y en a pas ici.
 *
 * <p>C'est le seul endroit du jeu ou les quatre enchantements du mod existent.
 */
public class CorruptedEnchantingTableBlock extends Block {

	private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

	private static final Component TITLE = Component.translatable("container.celestium.corrupted_enchanting");

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

		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (player instanceof ServerPlayer serverPlayer) {
			NetworkHooks.openScreen(serverPlayer, menuProvider(level, pos));
		}
		return InteractionResult.CONSUME;
	}

	@Nullable
	private static MenuProvider menuProvider(Level level, BlockPos pos) {
		return new SimpleMenuProvider(
				(containerId, inventory, player) -> newMenu(containerId, inventory, level, pos),
				TITLE);
	}

	private static AbstractContainerMenu newMenu(int containerId, Inventory inventory, Level level,
			BlockPos pos) {
		return new CorruptedEnchantingMenu(containerId, inventory,
				ContainerLevelAccess.create(level, pos));
	}

	/** Le halo qui salue un enchantement accorde. */
	public static void celebrate(Level level, BlockPos pos) {
		level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 0.7F);

		if (level instanceof ServerLevel server) {
			server.sendParticles(ParticleTypes.ENCHANT,
					pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
					40, 0.4, 0.4, 0.4, 0.6);
		}
	}
}
