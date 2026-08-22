package net.celestium.worldgen;

import com.mojang.serialization.Codec;
import net.celestium.CelestiumMod;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

/**
 * Pose la structure du cimetiere a la surface.
 *
 * <p>Le mod d'origine la generait dans les biomes ocean et riviere : son modificateur de biome ne
 * listait que des etendues d'eau. Le controle se fait desormais sur le terrain lui-meme — le sol
 * doit etre solide et sec — ce qui vaut quel que soit le biome et resiste aux lacs comme aux
 * rivieres qui traversent un biome terrestre.
 *
 * <p>Le tirage de rarete, ecrit a la main sous la forme
 * {@code random.nextInt(1000000) + 1 <= 1500}, est remonte dans le placement de la feature, ou
 * {@code RarityFilter} l'exprime directement.
 */
public class CemeteryFeature extends Feature<NoneFeatureConfiguration> {

	/** Enfoncement de la structure sous la surface, pour asseoir ses fondations. */
	private static final int SINK_DEPTH = 3;

	private static final int PLACE_FLAGS = 2;

	public CemeteryFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		RandomSource random = context.random();

		Optional<StructureTemplate> template = level.getLevel().getStructureManager()
				.get(CelestiumMod.id("vraicimetiere11"));
		if (template.isEmpty()) {
			CelestiumMod.LOGGER.warn("Structure du cimetiere introuvable : vraicimetiere11");
			return false;
		}

		int x = context.origin().getX() + random.nextInt(16);
		int z = context.origin().getZ() + random.nextInt(16);
		int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);

		BlockPos ground = new BlockPos(x, surface - 1, z);
		if (!isDryLand(level, ground)) {
			return false;
		}

		BlockPos origin = ground.below(SINK_DEPTH);
		StructurePlaceSettings settings = new StructurePlaceSettings()
				.setMirror(Mirror.values()[random.nextInt(Mirror.values().length)])
				.setRotation(Rotation.values()[random.nextInt(Rotation.values().length)])
				.setRandom(random)
				.addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR)
				.setIgnoreEntities(false);

		return template.get().placeInWorld(level, origin, origin, settings, random, PLACE_FLAGS);
	}

	/** Un cimetiere ne se pose ni dans l'eau ni sur un sol qui n'en est pas un. */
	private static boolean isDryLand(WorldGenLevel level, BlockPos ground) {
		BlockState state = level.getBlockState(ground);
		if (!state.getFluidState().isEmpty() || state.isAir()) {
			return false;
		}
		return level.getBlockState(ground.above()).getFluidState().isEmpty();
	}
}
