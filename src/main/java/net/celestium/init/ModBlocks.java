package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.WoodSet;
import net.celestium.feature.celestium.CelestiumStorageBlock;
import net.celestium.feature.altar.SummoningAltarBlock;
import net.celestium.feature.luckyblock.LuckyBlock;
import net.celestium.feature.luckyblock.LuckyTier;
import net.celestium.feature.portal.CorruptedPortalBlock;
import net.celestium.feature.portal.CorruptedPortalFrameBlock;
import net.celestium.feature.portal.DemonPortalBlock;
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
	 * Le bloc chance ordinaire : le plus genereux des trois, et le moins spectaculaire.
	 *
	 * <p>Les trois blocs partagent leur mecanique et ne different que par leur table d'evenements,
	 * decrite dans {@link LuckyTier}. Ils forment une echelle ou l'on echange la frequence des
	 * bonnes surprises contre leur intensite.
	 */
	public static final RegistryObject<Block> LUCKY_BLOCK = register("lucky_block",
			() -> new LuckyBlock(LuckyTier.ORDINARY, BlockBehaviour.Properties.of()
					.mapColor(MapColor.GOLD)
					.sound(SoundType.COPPER)
					.strength(1.7F, 10.0F)
					.lightLevel(state -> 1)));

	/**
	 * Le bloc chance corrompu : une chance sur deux environ, mais de meilleures recompenses.
	 *
	 * <p>Les trois blocs chance forment une echelle ou l'on echange la frequence contre la valeur.
	 * Celui-ci en occupe le milieu.
	 */
	public static final RegistryObject<Block> CORRUPTED_LUCKY_BLOCK = register("corrupted_lucky_block",
			() -> new LuckyBlock(LuckyTier.CORRUPTED, BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_RED)
					.sound(SoundType.COPPER)
					.strength(2.0F, 12.0F)
					.lightLevel(state -> 2)));

	/**
	 * Le bloc chance du demon : il ne donne presque jamais rien de bon.
	 *
	 * <p>Environ un tirage sur six. En echange, ce qu'il donne ne se trouve nulle part ailleurs en
	 * pareille quantite. C'est le pari le plus mauvais du mod, et le plus paye.
	 */
	public static final RegistryObject<Block> DEMON_LUCKY_BLOCK = register("demon_lucky_block",
			() -> new LuckyBlock(LuckyTier.DEMON, BlockBehaviour.Properties.of()
					.mapColor(MapColor.NETHER)
					.sound(SoundType.COPPER)
					.strength(2.5F, 15.0F)
					.lightLevel(state -> 4)));

	/**
	 * Celestium corrompu : le cadre du portail vers les terres du demon.
	 *
	 * <p>Il ne se mine pas, il se fabrique — un bloc de Celestium que les fragments arraches au
	 * demon epeiste viennent souiller. C'est ce qui verrouille l'acces a la dimension derriere le
	 * boss.
	 */
	public static final RegistryObject<Block> CORRUPTED_CELESTIUM_BLOCK = register("corrupted_celestium_block",
			() -> new Block(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_RED)
					.sound(SoundType.AMETHYST)
					.strength(50.0F, 1200.0F)
					.requiresCorrectToolForDrops()
					.lightLevel(state -> 3)));

	/** Autel d'invocation : offrir un lingot de Demonium y rappelle le demon epeiste. */
	public static final RegistryObject<Block> SUMMONING_ALTAR = register("summoning_altar",
			() -> new SummoningAltarBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_RED)
					.sound(SoundType.STONE)
					.strength(6.0F, 1200.0F)
					.requiresCorrectToolForDrops()
					.lightLevel(state -> 7)));

	/**
	 * Minerai de Celestium corrompu, present uniquement dans les terres corrompues.
	 *
	 * <p>C'est le verrou de toute la progression : sans lui pas de cadre pour les Terres du demon,
	 * et il ne s'extrait nulle part ailleurs.
	 */
	public static final RegistryObject<Block> CORRUPTED_CELESTIUM_ORE = register("corrupted_celestium_ore",
			() -> new DropExperienceBlock(
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.COLOR_BROWN)
							.sound(SoundType.DEEPSLATE)
							.strength(4.5F, 9.0F)
							.requiresCorrectToolForDrops(),
					UniformInt.of(3, 6)));

	public static final RegistryObject<Block> DEMONIUM_ORE = register("demonium_ore",
			() -> new DropExperienceBlock(
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.NETHER)
							.sound(SoundType.NETHER_ORE)
							.strength(4.0F, 8.0F)
							.requiresCorrectToolForDrops(),
					UniformInt.of(2, 6)));

	public static final RegistryObject<Block> DEMONIUM_BLOCK = register("demonium_block",
			() -> new Block(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_RED)
					.sound(SoundType.NETHERITE_BLOCK)
					.strength(50.0F, 1200.0F)
					.requiresCorrectToolForDrops()));

	/**
	 * Le cadre menant aux terres corrompues, sur le modele de celui de l'End.
	 *
	 * <p>Il se pose et se fabrique, contrairement a son modele qu'on ne trouve qu'en forteresse :
	 * ce mod n'a pas de structure ou le cacher, et une chasse au donjon n'ajouterait qu'une corvee
	 * a un acces deja verrouille par douze yeux.
	 */
	public static final RegistryObject<Block> CORRUPTED_PORTAL_FRAME = register("corrupted_portal_frame",
			() -> new CorruptedPortalFrameBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_BROWN)
					.sound(SoundType.DEEPSLATE)
					// Dur mais recuperable, a la difference de son modele : il se fabrique, donc un
					// anneau mal place doit pouvoir se defaire sans perdre douze blocs.
					.strength(30.0F, 1200.0F)
					.requiresCorrectToolForDrops()
					.lightLevel(state -> 1)
					.pushReaction(PushReaction.BLOCK)));

	/** La surface d'un portail corrompu. Sans item : elle apparait quand l'anneau est garni. */
	public static final RegistryObject<Block> CORRUPTED_PORTAL = BLOCKS.register("corrupted_portal",
			() -> new CorruptedPortalBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_BROWN)
					.sound(SoundType.GLASS)
					.noCollission()
					.lightLevel(state -> 13)
					.strength(-1.0F, 3600000.0F)
					.noLootTable()
					.pushReaction(PushReaction.BLOCK)));

	/**
	 * La surface d'un portail celeste. Elle n'a pas d'item : on ne la pose pas, on allume un cadre.
	 * Indestructible et sans butin, comme un portail du Nether.
	 */
	public static final RegistryObject<Block> DEMON_PORTAL = BLOCKS.register("demon_portal",
			() -> new DemonPortalBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_PURPLE)
					.sound(SoundType.GLASS)
					.noCollission()
					.lightLevel(state -> 11)
					.strength(-1.0F, 3600000.0F)
					.noLootTable()
					.pushReaction(PushReaction.BLOCK)));

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
