package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.WoodSet;
import net.celestium.feature.portal.CorruptedPortalFrameBlock;
import net.celestium.feature.portal.DemonPortalBlock;
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
import net.minecraftforge.client.model.generators.ConfiguredModel;
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
		simpleCube(ModBlocks.CORRUPTED_LUCKY_BLOCK.get());
		simpleCube(ModBlocks.DEMON_LUCKY_BLOCK.get());

		simpleCube(ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get());
		simpleCube(ModBlocks.CORRUPTED_CELESTIUM_ORE.get());
		simpleCube(ModBlocks.SUMMONING_ALTAR.get());
		simpleCube(ModBlocks.DEMONIUM_ORE.get());
		simpleCube(ModBlocks.DEMONIUM_BLOCK.get());

		portal();
		corruptedPortal();
		enchantingTable();

		woodSet(ModBlocks.BOIS_DU_DEMON);
	}

	/**
	 * La surface du portail reprend le modele du portail du Nether, dont l'unique texture est
	 * remplacee. Faute de texture animee dediee, celle du bloc de Celestium sert de tenant-lieu.
	 */
	private void portal() {
		ModelFile plane = models()
				.withExistingParent("demon_portal", mcLoc("block/nether_portal_ns"))
				.texture("portal", modLoc("block/celestium_block"))
				.renderType("translucent");

		getVariantBuilder(ModBlocks.DEMON_PORTAL.get())
				.partialState().with(DemonPortalBlock.AXIS, Direction.Axis.X)
				.modelForState().modelFile(plane).addModel()
				.partialState().with(DemonPortalBlock.AXIS, Direction.Axis.Z)
				.modelForState().modelFile(plane).rotationY(90).addModel();
	}

	/**
	 * Le cadre corrompu et la surface qu'il ouvre.
	 *
	 * <p>Le cadre est un socle de treize seiziemes, surmonte d'un oeil quand il est garni. Les deux
	 * volumes sont declares separement plutot que dessines dans une texture : c'est ce relief qui
	 * rend un anneau incomplet lisible d'un coup d'oeil, de loin et de dessus.
	 *
	 * <p>La surface est plate et posee au sol, comme celle de l'End : on tombe dedans plutot qu'on
	 * ne la traverse.
	 */
	private void corruptedPortal() {
		ResourceLocation frameTexture = modLoc("block/corrupted_celestium_block");
		ResourceLocation eyeTexture = modLoc("block/celestium_block");

		ModelFile empty = models().withExistingParent("corrupted_portal_frame", mcLoc("block/block"))
				.texture("particle", frameTexture)
				.texture("frame", frameTexture)
				.element().from(0, 0, 0).to(16, 13, 16)
				.allFaces((direction, face) -> face.texture("#frame")).end();

		ModelFile filled = models().withExistingParent("corrupted_portal_frame_filled", mcLoc("block/block"))
				.texture("particle", frameTexture)
				.texture("frame", frameTexture)
				.texture("eye", eyeTexture)
				.element().from(0, 0, 0).to(16, 13, 16)
				.allFaces((direction, face) -> face.texture("#frame")).end()
				.element().from(4, 13, 4).to(12, 16, 12)
				.allFaces((direction, face) -> face.texture("#eye")).end();

		getVariantBuilder(ModBlocks.CORRUPTED_PORTAL_FRAME.get()).forAllStates(state -> {
			ModelFile model = state.getValue(CorruptedPortalFrameBlock.HAS_EYE) ? filled : empty;
			int rotation = (int) state.getValue(CorruptedPortalFrameBlock.FACING).toYRot();

			return ConfiguredModel.builder()
					.modelFile(model)
					.rotationY((rotation + 180) % 360)
					.build();
		});

		simpleBlockItem(ModBlocks.CORRUPTED_PORTAL_FRAME.get(), empty);

		ModelFile surface = models().withExistingParent("corrupted_portal", mcLoc("block/block"))
				.texture("particle", modLoc("block/corrupted_portal"))
				.texture("all", modLoc("block/corrupted_portal"))
				.renderType("translucent")
				.element().from(0, 0, 0).to(16, 12, 16)
				.allFaces((direction, face) -> face.texture("#all")).end();

		simpleBlock(ModBlocks.CORRUPTED_PORTAL.get(), surface);
	}

	/**
	 * La table corrompue : un pupitre bas, de la hauteur d'une table d'enchantement.
	 *
	 * <p>Douze seiziemes, comme son modele vanilla. La hauteur n'est pas decorative : c'est elle qui
	 * dit qu'on pose quelque chose dessus plutot qu'on ne marche dessus.
	 */
	private void enchantingTable() {
		ResourceLocation texture = modLoc("block/corrupted_celestium_block");

		ModelFile table = models().withExistingParent("corrupted_enchanting_table", mcLoc("block/block"))
				.texture("particle", texture)
				.texture("all", texture)
				.renderType("cutout")
				.element().from(0, 0, 0).to(16, 12, 16)
				.allFaces((direction, face) -> face.texture("#all")).end();

		simpleBlockWithItem(ModBlocks.CORRUPTED_ENCHANTING_TABLE.get(), table);
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
