package net.celestium.worldgen;

import net.celestium.CelestiumMod;
import net.celestium.init.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
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
	public static final ResourceKey<ConfiguredFeature<?, ?>> DEMONIUM_ORE = key("demonium_ore");

	/** Le minerai des terres corrompues, seul chemin vers les Terres du demon. */
	public static final ResourceKey<ConfiguredFeature<?, ?>> CORRUPTED_CELESTIUM_ORE =
			key("corrupted_celestium_ore");
	public static final ResourceKey<ConfiguredFeature<?, ?>> DEMON_TREE = key("demon_tree");

	/** Les trois blocs chance, semes un par un plutot qu'en filons. */
	public static final ResourceKey<ConfiguredFeature<?, ?>> LUCKY_BLOCK = key("lucky_block");
	public static final ResourceKey<ConfiguredFeature<?, ?>> CORRUPTED_LUCKY_BLOCK =
			key("corrupted_lucky_block");
	public static final ResourceKey<ConfiguredFeature<?, ?>> DEMON_LUCKY_BLOCK = key("demon_lucky_block");

	/** Nombre de blocs par filon. Etait de 3 dans le mod d'origine. */
	private static final int VEIN_SIZE = 4;

	/** Le Demonium se presente en filons plus gros, mais dans un monde bien moins accueillant. */
	private static final int DEMONIUM_VEIN_SIZE = 6;

	/** Filons du Celestium corrompu : larges, parce que la dimension fait deja payer le sejour. */
	private static final int CORRUPTED_VEIN_SIZE = 7;

	private ModConfiguredFeatures() {
	}

	public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
		context.register(CELESTIUM_ORE, new ConfiguredFeature<>(Feature.ORE,
				new OreConfiguration(targets(ModBlocks.CELESTIUM_ORE.get()), VEIN_SIZE)));

		// Le Demonium se loge dans la roche des Terres du demon, dont le relief est engendre par le
		// bruit de l'Overworld : ce sont donc les memes blocs remplacables que pour le Celestium.
		// Cibler la netherrack, comme lorsque le minerai vivait dans le Nether, l'empechait
		// purement et simplement d'apparaitre.
		context.register(DEMONIUM_ORE, new ConfiguredFeature<>(Feature.ORE,
				new OreConfiguration(targets(ModBlocks.DEMONIUM_ORE.get()), DEMONIUM_VEIN_SIZE)));

		// Le Celestium corrompu affleure dans les terres corrompues. Ses filons sont genereux : c'est
		// une etape de passage, pas une fin en soi, et la dimension se charge deja de rendre le sejour
		// couteux.
		context.register(CORRUPTED_CELESTIUM_ORE, new ConfiguredFeature<>(Feature.ORE,
				new OreConfiguration(targets(ModBlocks.CORRUPTED_CELESTIUM_ORE.get()), CORRUPTED_VEIN_SIZE)));

		// Les blocs chance se sement isolement — un filon de blocs chance en donnerait quatre d'un
		// coup, ce qui ruinerait le pari. Le mecanisme des filons convient neanmoins : c'est le
		// seul qui sache loger un bloc au milieu de la roche.
		context.register(LUCKY_BLOCK, new ConfiguredFeature<>(Feature.ORE,
				new OreConfiguration(targets(ModBlocks.LUCKY_BLOCK.get()), 1)));

		// Le bloc corrompu se cache dans le Nether, d'ou vient la matiere qui corrompt le Celestium.
		context.register(CORRUPTED_LUCKY_BLOCK, new ConfiguredFeature<>(Feature.ORE,
				new OreConfiguration(
						List.of(OreConfiguration.target(new TagMatchTest(BlockTags.BASE_STONE_NETHER),
								ModBlocks.CORRUPTED_LUCKY_BLOCK.get().defaultBlockState())),
						1)));

		context.register(DEMON_LUCKY_BLOCK, new ConfiguredFeature<>(Feature.ORE,
				new OreConfiguration(targets(ModBlocks.DEMON_LUCKY_BLOCK.get()), 1)));

		// Le seul arbre qui pousse sur les terres du demon : tronc droit et houppier compact.
		context.register(DEMON_TREE, new ConfiguredFeature<>(Feature.TREE,
				new TreeConfiguration.TreeConfigurationBuilder(
						BlockStateProvider.simple(ModBlocks.BOIS_DU_DEMON.log.get()),
						new StraightTrunkPlacer(5, 3, 2),
						BlockStateProvider.simple(ModBlocks.BOIS_DU_DEMON.leaves.get()),
						new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
						new TwoLayersFeatureSize(1, 0, 1))
						.ignoreVines()
						.build()));
	}

	/** Un minerai remplace la pierre en surface et le deepslate en profondeur. */
	private static List<OreConfiguration.TargetBlockState> targets(net.minecraft.world.level.block.Block ore) {
		return List.of(
				OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
						ore.defaultBlockState()),
				OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES),
						ore.defaultBlockState()));
	}

	private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, CelestiumMod.id(name));
	}
}
