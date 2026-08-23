package net.celestium.feature.portal;

import net.celestium.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * La surface d'un portail demoniaque.
 *
 * <p>Traversable, lumineuse, et incassable a la main : elle ne se retire qu'en brisant son cadre,
 * comme un portail du Nether. Toute entite qui la traverse est envoyee dans l'autre monde.
 */
public class DemonPortalBlock extends Block {

	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

	private static final VoxelShape SHAPE_X = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	private static final VoxelShape SHAPE_Z = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

	public DemonPortalBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(AXIS) == Direction.Axis.Z ? SHAPE_Z : SHAPE_X;
	}

	/**
	 * Le portail disparait des que son cadre est rompu.
	 *
	 * <p>Sans cela, casser un seul bloc du cadre laisserait une surface flottante indestructible.
	 */
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour,
			LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {

		Direction.Axis portalAxis = state.getValue(AXIS);
		boolean alongPortal = direction.getAxis() == Direction.Axis.Y
				|| direction.getAxis() == portalAxis;

		if (alongPortal && !neighbour.is(this) && !neighbour.is(ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get())) {
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.isClientSide() || !entity.canChangeDimensions()) {
			return;
		}
		DemonPortalTravel.onEntityInPortal(entity);
	}

	/** Le portail ne se pose pas a la main : il apparait en allumant un cadre. */
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return switch (rotation) {
			case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> state.setValue(AXIS,
					state.getValue(AXIS) == Direction.Axis.Z ? Direction.Axis.X : Direction.Axis.Z);
			default -> state;
		};
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state;
	}

	/** Empeche un piston de deplacer la surface hors de son cadre. */
	@Override
	public net.minecraft.world.level.material.PushReaction getPistonPushReaction(BlockState state) {
		return net.minecraft.world.level.material.PushReaction.BLOCK;
	}

	/** Signale au joueur qu'il vient de changer de monde, une fois arrive. */
	public static void announceArrival(ServerPlayer player, ServerLevel destination) {
		player.displayClientMessage(
				net.minecraft.network.chat.Component.translatable(
						destination.dimension() == net.celestium.worldgen.ModDimensions.DEMON_LEVEL
								? "message.celestium.portal.entered"
								: "message.celestium.portal.returned"),
				true);
	}
}
