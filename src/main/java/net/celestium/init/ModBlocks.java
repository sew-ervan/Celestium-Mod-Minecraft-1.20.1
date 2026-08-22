package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.WoodSet;
import net.celestium.feature.celestium.CelestiumStorageBlock;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/** Registre des blocs. Chaque bloc pose ici obtient automatiquement son item associe. */
public class ModBlocks {

	public static final DeferredRegister<Block> BLOCKS =
			DeferredRegister.create(ForgeRegistries.BLOCKS, CelestiumMod.MOD_ID);

	/**
	 * Le minerai lache son experience via {@link DropExperienceBlock}, comme tout minerai vanilla.
	 * Le bloc genere par MCreator n'en donnait aucune.
	 */
	public static final RegistryObject<Block> CELESTIUM_ORE = register("celestium_ore",
			() -> new DropExperienceBlock(
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.DEEPSLATE)
							.sound(SoundType.AMETHYST)
							.strength(5.0F, 10.0F)
							.requiresCorrectToolForDrops()
							.pushReaction(PushReaction.BLOCK),
					UniformInt.of(3, 7)));

	public static final RegistryObject<Block> CELESTIUM_BLOCK = register("celestium_block",
			() -> new CelestiumStorageBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_LIGHT_BLUE)
					.sound(SoundType.AMETHYST)
					.strength(65.0F, 6.0F)
					.requiresCorrectToolForDrops()
					.emissiveRendering((state, level, pos) -> true)
					.hasPostProcess((state, level, pos) -> true)));

	/**
	 * Le comportement du bloc chance tient entierement dans sa table de butin : une entree de tag
	 * en {@code expand} tire un item au hasard parmi les recompenses. MCreator y consacrait une
	 * classe de bloc, une procedure, et deux surcharges — l'une pour la casse, l'autre pour
	 * l'explosion — la que le systeme de butin couvre les deux cas seul.
	 */
	public static final RegistryObject<Block> LUCKY_BLOCK = register("lucky_block",
			() -> new Block(BlockBehaviour.Properties.of()
					.mapColor(MapColor.GOLD)
					.sound(SoundType.COPPER)
					.strength(1.7F, 10.0F)
					.lightLevel(state -> 1)));

	/**
	 * Le bois du demon : dix blocs en une declaration.
	 *
	 * <p>Ajouter une seconde essence tient desormais en une ligne, la ou le mod d'origine
	 * demandait dix classes Java et vingt enregistrements.
	 */
	public static final WoodSet BOIS_DU_DEMON = new WoodSet(
			"bois_du_demon",
			MapColor.CRIMSON_STEM,
			MapColor.COLOR_RED,
			ModWoodTypes.BOIS_DU_DEMON,
			ModWoodTypes.BOIS_DU_DEMON_SET);

	private ModBlocks() {
	}

	/**
	 * Enregistre un bloc et l'item qui le pose, sous le meme nom.
	 *
	 * <p>MCreator tenait les deux registres dans deux fichiers separes, ce qui laissait
	 * regulierement un bloc sans item : le bloc existait dans le monde mais restait introuvable
	 * en inventaire.
	 */
	public static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
		RegistryObject<T> registered = BLOCKS.register(name, block);
		ModItems.ITEMS.register(name, () -> new BlockItem(registered.get(), new Item.Properties()));
		return registered;
	}
}
