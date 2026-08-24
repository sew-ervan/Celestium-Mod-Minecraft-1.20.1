package net.celestium.feature.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Un bloc du cadre menant aux terres corrompues.
 *
 * <p>Il fonctionne comme le cadre du portail de l'End : douze blocs disposes en anneau, chacun
 * attendant son oeil. Le douzieme oeil pose allume le passage.
 *
 * <p>Le socle et l'oeil sont deux formes distinctes, reunies quand l'oeil est en place. Sans cela
 * l'oeil ne se verrait pas depasser du cadre, qui est ce qui rend un cadre incomplet lisible d'un
 * coup d'oeil.
 */
public class CorruptedPortalFrameBlock extends Block {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty HAS_EYE = BlockStateProperties.EYE;

	private static final VoxelShape BASE = Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
	private static final VoxelShape EYE = Block.box(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
	private static final VoxelShape WITH_EYE = Shapes.or(BASE, EYE);

	public CorruptedPortalFrameBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(HAS_EYE, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, HAS_EYE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(HAS_EYE) ? WITH_EYE : BASE;
	}

	/**
	 * Le cadre se pose tourne vers celui qui le pose.
	 *
	 * <p>C'est l'inverse du reflexe habituel, et c'est voulu : les douze blocs d'un anneau regardent
	 * tous vers son centre, donc vers le joueur qui en fait le tour pour les poser.
	 */
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection())
				.setValue(HAS_EYE, false);
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	/** Un cadre garni ne se laisse pas pousser : le motif serait rompu sans que rien ne l'annonce. */
	@Override
	public net.minecraft.world.level.material.PushReaction getPistonPushReaction(BlockState state) {
		return net.minecraft.world.level.material.PushReaction.BLOCK;
	}

	/** Un cadre garni alimente un comparateur, comme celui de l'End. */
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, net.minecraft.world.level.Level level, BlockPos pos) {
		return state.getValue(HAS_EYE) ? 15 : 0;
	}

	/** Reunit les deux formes, pour que l'oeil pose se voie depasser du socle. */
	public static VoxelShape shapeWithEye() {
		return Shapes.join(BASE, EYE, BooleanOp.OR);
	}
}
