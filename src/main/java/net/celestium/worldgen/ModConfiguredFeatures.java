package net.celestium.worldgen;

import net.celestium.CelestiumMod;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

/**
 * Filons configures.
 *
 * <p>Le mod d'origine enumerait sept blocs remplacables un par un — pierre, granite, diorite,
 * andesite, deepslate, tuff et gravier — chacun avec sa propre regle de correspondance. Les tags
 * vanilla couvrent exactement ce besoin et suivent les ajouts de blocs des versions suivantes.
 */
public final class ModConfiguredFeatures {

	public static final ResourceKey<ConfiguredFeature<?, ?>> CELESTIUM_ORE = key("celestium_ore");
	public static final ResourceKey<ConfiguredFeature<?, ?>> CEMETERY = key("cemetery");

	/** Nombre de blocs par filon. */
	private static final int VEIN_SIZE = 3;

	private ModConfiguredFeatures() {
	}

	public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
		List<OreConfiguration.TargetBlockState> targets = List.of(
				OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
						ModBlocks.CELESTIUM_ORE.get().defaultBlockState()),
				OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES),
						ModBlocks.CELESTIUM_ORE.get().defaultBlockState()));

		context.register(CELESTIUM_ORE, new ConfiguredFeature<>(Feature.ORE,
				new OreConfiguration(targets, VEIN_SIZE)));

		context.register(CEMETERY, new ConfiguredFeature<>(ModFeatures.CEMETERY.get(),
				NoneFeatureConfiguration.INSTANCE));
	}

	private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, CelestiumMod.id(name));
	}
}
