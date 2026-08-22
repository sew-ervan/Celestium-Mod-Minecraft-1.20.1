package net.celestium.feature.celestium;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bloc de Celestium compact : neuf lingots, et une base de balise qui teinte le rayon.
 */
public class CelestiumStorageBlock extends Block {

	/** Teinte celeste du rayon de balise, en composantes rouge / vert / bleu. */
	private static final float[] BEACON_TINT = new float[]{0.5254902F, 0.41960785F, 1.0F};

	public CelestiumStorageBlock(Properties properties) {
		super(properties);
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
		return BEACON_TINT;
	}
}
