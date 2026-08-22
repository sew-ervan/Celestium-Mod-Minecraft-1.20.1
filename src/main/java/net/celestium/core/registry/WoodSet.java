package net.celestium.core.registry;

import net.celestium.init.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/**
 * Une essence de bois complete : rondin, bois ecorce, planches, feuilles, escalier, dalle,
 * barriere, portillon, plaque de pression et bouton.
 *
 * <p>Le mod d'origine consacrait dix classes Java quasi identiques a ces dix blocs, plus dix
 * enregistrements dans le registre des blocs et dix autres dans celui des items. Ajouter une
 * seconde essence demandait de dupliquer l'ensemble. Ici, une essence tient en une declaration.
 */
public final class WoodSet {

	/** Duretes reprises du mod d'origine, debarrassees de leurs decimales aberrantes. */
	private static final float HARDNESS = 6.0F;
	private static final float RESISTANCE = 6.0F;

	private static final int WOOD_BURN_ODDS = 5;
	private static final int WOOD_FLAME_ODDS = 15;
	private static final int LEAVES_BURN_ODDS = 30;
	private static final int LEAVES_FLAME_ODDS = 90;

	private final String name;

	public final RegistryObject<Block> log;
	public final RegistryObject<Block> wood;
	public final RegistryObject<Block> planks;
	public final RegistryObject<Block> leaves;
	public final RegistryObject<Block> stairs;
	public final RegistryObject<Block> slab;
	public final RegistryObject<Block> fence;
	public final RegistryObject<Block> fenceGate;
	public final RegistryObject<Block> pressurePlate;
	public final RegistryObject<Block> button;

	public WoodSet(String name, MapColor woodColor, MapColor leavesColor, WoodType woodType, BlockSetType setType) {
		this.name = name;

		this.log = ModBlocks.register(name + "_log", () -> new RotatedPillarBlock(pillarProperties(woodColor)));
		this.wood = ModBlocks.register(name + "_wood", () -> new RotatedPillarBlock(pillarProperties(woodColor)));
		this.planks = ModBlocks.register(name + "_planks", () -> new Block(woodProperties(woodColor)));

		this.leaves = ModBlocks.register(name + "_leaves", () -> new LeavesBlock(
				BlockBehaviour.Properties.of()
						.mapColor(leavesColor)
						.sound(SoundType.GRASS)
						.strength(0.2F)
						.randomTicks()
						.noOcclusion()
						.isValidSpawn((state, level, pos, type) -> false)
						.isSuffocating((state, level, pos) -> false)
						.isViewBlocking((state, level, pos) -> false)));

		// L'escalier a besoin de l'etat des planches ; le fournisseur differe la resolution
		// jusqu'a ce que les planches soient effectivement enregistrees.
		this.stairs = ModBlocks.register(name + "_stairs",
				() -> new StairBlock(() -> this.planks.get().defaultBlockState(), woodProperties(woodColor)));
		this.slab = ModBlocks.register(name + "_slab", () -> new SlabBlock(woodProperties(woodColor)));
		this.fence = ModBlocks.register(name + "_fence", () -> new FenceBlock(woodProperties(woodColor)));
		this.fenceGate = ModBlocks.register(name + "_fence_gate",
				() -> new FenceGateBlock(woodProperties(woodColor), woodType));
		this.pressurePlate = ModBlocks.register(name + "_pressure_plate",
				() -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
						woodProperties(woodColor).noCollission(), setType));
		this.button = ModBlocks.register(name + "_button",
				() -> new ButtonBlock(woodProperties(woodColor).noCollission(), setType, 30, true));
	}

	private static BlockBehaviour.Properties woodProperties(MapColor color) {
		return BlockBehaviour.Properties.of()
				.mapColor(color)
				.sound(SoundType.WOOD)
				.strength(HARDNESS, RESISTANCE)
				.ignitedByLava();
	}

	private static BlockBehaviour.Properties pillarProperties(MapColor color) {
		return woodProperties(color);
	}

	public String getName() {
		return this.name;
	}

	/** Tous les blocs de l'essence, dans l'ordre d'enregistrement. */
	public List<RegistryObject<Block>> all() {
		return List.of(this.log, this.wood, this.planks, this.leaves, this.stairs, this.slab,
				this.fence, this.fenceGate, this.pressurePlate, this.button);
	}

	/** Blocs qui se comportent comme du bois massif face au feu, feuilles exclues. */
	public List<RegistryObject<Block>> flammableWood() {
		return List.of(this.log, this.wood, this.planks, this.stairs, this.slab,
				this.fence, this.fenceGate, this.pressurePlate, this.button);
	}

	/**
	 * Declare l'inflammabilite aupres du bloc de feu.
	 *
	 * <p>MCreator surchargeait {@code getFlammability} dans chacune des dix classes. Forge attend
	 * en realite une declaration centralisee, faite une fois au demarrage.
	 */
	public void registerFlammability() {
		FireBlock fire = (FireBlock) Blocks.FIRE;
		this.flammableWood().forEach(block -> fire.setFlammable(block.get(), WOOD_BURN_ODDS, WOOD_FLAME_ODDS));
		fire.setFlammable(this.leaves.get(), LEAVES_BURN_ODDS, LEAVES_FLAME_ODDS);
	}
}
