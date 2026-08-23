package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.WoodSet;
import net.celestium.feature.portal.CelestialPortalBlock;
import net.celestium.init.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Genere les blockstates et les modeles de blocs.
 *
 * <p>Remplace une soixantaine de fichiers JSON ecrits a la main. Les vingt-deux modeles du seul
 * bois du demon (escaliers interieurs et exterieurs, barriere en poteau et en traverse, portillon
 * ouvert, ferme, mural...) decoulent tous de quatre textures.
 */
public class ModBlockStateProvider extends BlockStateProvider {

	public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, CelestiumMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleCube(ModBlocks.CELESTIUM_ORE.get());
		simpleCube(ModBlocks.CELESTIUM_BLOCK.get());
		simpleCube(ModBlocks.LUCKY_BLOCK.get());

		simpleCube(ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get());
		simpleCube(ModBlocks.SUMMONING_ALTAR.get());
		simpleCube(ModBlocks.DEMONIUM_ORE.get());
		simpleCube(ModBlocks.DEMONIUM_BLOCK.get());

		portal();

		woodSet(ModBlocks.BOIS_DU_DEMON);
	}

	/**
	 * La surface du portail reprend le modele du portail du Nether, dont l'unique texture est
	 * remplacee. Faute de texture animee dediee, celle du bloc de Celestium sert de tenant-lieu.
	 */
	private void portal() {
		ModelFile plane = models()
				.withExistingParent("celestial_portal", mcLoc("block/nether_portal_ns"))
				.texture("portal", modLoc("block/celestium_block"))
				.renderType("translucent");

		getVariantBuilder(ModBlocks.CELESTIAL_PORTAL.get())
				.partialState().with(CelestialPortalBlock.AXIS, Direction.Axis.X)
				.modelForState().modelFile(plane).addModel()
				.partialState().with(CelestialPortalBlock.AXIS, Direction.Axis.Z)
				.modelForState().modelFile(plane).rotationY(90).addModel();
	}

	/** Bloc plein a texture unique, plus son modele d'item. */
	private void simpleCube(Block block) {
		simpleBlockWithItem(block, cubeAll(block));
	}

	/** Genere les dix blocs d'une essence a partir de ses quatre textures. */
	private void woodSet(WoodSet set) {
		String prefix = set.getName();
		ResourceLocation logSide = modLoc("block/" + prefix + "_log_side");
		ResourceLocation logTop = modLoc("block/" + prefix + "_log_top");
		ResourceLocation planks = modLoc("block/" + prefix + "_planks");
		ResourceLocation leaves = modLoc("block/" + prefix + "_leaves");

		// Le rondin montre ses cernes en bout ; le bois ecorce presente l'ecorce sur les six faces.
		axisBlock((RotatedPillarBlock) set.log.get(), logSide, logTop);
		axisBlock((RotatedPillarBlock) set.wood.get(), logSide, logSide);
		blockItemFromBlockModel(set.log.get());
		blockItemFromBlockModel(set.wood.get());

		ModelFile planksModel = models().cubeAll(name(set.planks.get()), planks);
		simpleBlockWithItem(set.planks.get(), planksModel);

		// Les feuilles doivent etre rendues en cutout : sans cela, leurs pixels transparents
		// s'affichent en noir opaque.
		ModelFile leavesModel = models().cubeAll(name(set.leaves.get()), leaves).renderType("cutout_mipped");
		simpleBlockWithItem(set.leaves.get(), leavesModel);

		stairsBlock((StairBlock) set.stairs.get(), planks);
		blockItemFromBlockModel(set.stairs.get());

		slabBlock((SlabBlock) set.slab.get(), planksModel.getLocation(), planks);
		blockItemFromBlockModel(set.slab.get());

		fenceBlock((FenceBlock) set.fence.get(), planks);
		itemModels().fenceInventory(name(set.fence.get()), planks);

		fenceGateBlock((FenceGateBlock) set.fenceGate.get(), planks);
		blockItemFromBlockModel(set.fenceGate.get());

		pressurePlateBlock((PressurePlateBlock) set.pressurePlate.get(), planks);
		blockItemFromBlockModel(set.pressurePlate.get());

		buttonBlock((ButtonBlock) set.button.get(), planks);
		itemModels().buttonInventory(name(set.button.get()), planks);
	}

	/** L'item du bloc reprend simplement le modele du bloc. */
	private void blockItemFromBlockModel(Block block) {
		String blockName = name(block);
		itemModels().withExistingParent(blockName, modLoc("block/" + blockName));
	}

	private String name(Block block) {
		ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
		if (key == null) {
			throw new IllegalStateException("Bloc non enregistre : " + block);
		}
		return key.getPath();
	}
}
