package net.celestium.feature.portal;

import net.celestium.worldgen.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * La surface d'un portail vers les terres corrompues.
 *
 * <p>Elle emporte au contact, sans attente, comme celle de l'End : le cadre a douze yeux est deja
 * une epreuve en soi, et faire patienter en plus n'ajouterait rien.
 *
 * <p>Le bloc est plat et se pose au sol : on tombe dedans plutot qu'on ne le traverse.
 */
public class CorruptedPortalBlock extends Block {

	private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

	public CorruptedPortalBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.isClientSide() || entity.isOnPortalCooldown() || !entity.canChangeDimensions()) {
			return;
		}
		if (!(level instanceof ServerLevel origin)) {
			return;
		}
		// Le bloc n'occupe que les douze seiziemes inferieurs : il faut avoir vraiment pose le pied
		// dedans, sinon on bascule en frolant le bord.
		if (!entity.getBoundingBox().intersects(SHAPE.bounds().move(pos))) {
			return;
		}

		entity.setPortalCooldown();
		CorruptedPortalTravel.travel(entity, origin);
	}

	/** Le portail s'evanouit si son anneau est rompu. */
	@Override
	public BlockState updateShape(BlockState state, net.minecraft.core.Direction direction,
			BlockState neighbour, net.minecraft.world.level.LevelAccessor level, BlockPos pos,
			BlockPos neighbourPos) {

		if (direction.getAxis().isHorizontal() && !neighbour.is(this)
				&& !neighbour.is(net.celestium.init.ModBlocks.CORRUPTED_PORTAL_FRAME.get())) {
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
	}

	@Override
	public net.minecraft.world.level.material.PushReaction getPistonPushReaction(BlockState state) {
		return net.minecraft.world.level.material.PushReaction.BLOCK;
	}

	/** Annonce l'arrivee, dans un sens comme dans l'autre. */
	public static void announce(ServerPlayer player, ServerLevel destination) {
		player.displayClientMessage(
				net.minecraft.network.chat.Component.translatable(
						destination.dimension() == ModDimensions.CORRUPTED_LEVEL
								? "message.celestium.corrupted.entered"
								: "message.celestium.corrupted.returned"),
				true);
	}
}
