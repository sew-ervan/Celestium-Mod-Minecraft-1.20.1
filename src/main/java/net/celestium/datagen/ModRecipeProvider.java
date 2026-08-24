package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.ModTags;
import net.celestium.core.registry.WoodSet;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

/**
 * Genere les recettes du mod.
 *
 * <p>Les outils reprennent les formes vanilla. Le mod d'origine alignait le manche de la hache et
 * de la houe sur la colonne de gauche, une forme qu'aucun joueur ne devine.
 *
 * <p>Les identifiants sont donnes explicitement, dans l'espace de noms du mod. Les raccourcis
 * vanilla du genre {@code nineBlockStorageRecipes} nomment leurs recettes avec le seul chemin de
 * l'objet produit : l'identifiant retombe alors dans l'espace de noms {@code minecraft}, et deux
 * paliers de compactage successifs finissent par se disputer le meme nom.
 */
public class ModRecipeProvider extends RecipeProvider {

	public ModRecipeProvider(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> writer) {
		celestium(writer);
		demonium(writer);
		corruption(writer);
		woodSet(writer, ModBlocks.BOIS_DU_DEMON);
	}

	/**
	 * Le Celestium corrompu, et ce qui mene a lui.
	 *
	 * <p>Il ne se fabrique plus. On l'extrayait autrefois d'un bloc de Celestium souille au Nether,
	 * ce qui en faisait un detour d'artisanat ; il s'arrache maintenant a la roche des terres
	 * corrompues, et de nulle part ailleurs. C'est ce qui donne son role a cette dimension : on n'y
	 * va pas pour la visiter, on y va parce que le chemin vers les Terres du demon passe par elle.
	 *
	 * <p>Restent donc ici les deux clefs de son acces — le cadre et l'oeil — plus le compactage du
	 * materiau une fois rapporte.
	 */
	private void corruption(Consumer<FinishedRecipe> writer) {
		// Le cadre : de l'obsidienne, que le Celestium tient ensemble. Trois par tour de recette,
		// douze pour un anneau, soit quatre tours et trente-deux blocs d'obsidienne.
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
						ModBlocks.CORRUPTED_PORTAL_FRAME.get(), 3)
				.pattern("OOO")
				.pattern("OCO")
				.pattern("OOO")
				.define('O', Items.OBSIDIAN)
				.define('C', ModItems.CELESTIUM_INGOT.get())
				.unlockedBy("has_celestium_ingot", has(ModItems.CELESTIUM_INGOT.get()))
				.save(writer, CelestiumMod.id("corrupted_portal_frame"));

		// L'oeil : celui de l'Ender, qu'un fragment celeste detourne de sa destination. Il faut du
		// Nether pour la poudre de blaze et de l'End pour les perles — les deux mondes qui se sont
		// heurtes ici sont ceux qu'il faut avoir visites pour y entrer.
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CORRUPTED_EYE.get())
				.requires(Items.ENDER_EYE)
				.requires(ModItems.CELESTIUM_FRAGMENT.get())
				.unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
				.save(writer, CelestiumMod.id("corrupted_eye"));

		// Le livre corrompu : un livre ordinaire trempe dans la matiere des terres corrompues.
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CORRUPTED_BOOK.get())
				.requires(Items.BOOK)
				.requires(ModItems.CORRUPTED_CELESTIUM_FRAGMENT.get())
				.unlockedBy("has_corrupted_celestium_fragment",
						has(ModItems.CORRUPTED_CELESTIUM_FRAGMENT.get()))
				.save(writer, CelestiumMod.id("corrupted_book"));

		// La table d'enchantement corrompue. Elle reunit les trois matieres du mod : le Celestium
		// pur au-dessus, le Demonium en socle, et entre les deux la rangee qui les met cote a cote —
		// un lingot de chaque, le corrompu au milieu, la ou les deux se rejoignent.
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
						ModBlocks.CORRUPTED_ENCHANTING_TABLE.get())
				.pattern("CLC")
				.pattern("cka")
				.pattern("DDD")
				.define('C', ModBlocks.CELESTIUM_BLOCK.get())
				.define('L', ModItems.CORRUPTED_BOOK.get())
				.define('c', ModItems.CELESTIUM_INGOT.get())
				.define('k', ModItems.CORRUPTED_CELESTIUM_INGOT.get())
				.define('a', ModItems.DEMONIUM_INGOT.get())
				.define('D', ModBlocks.DEMONIUM_BLOCK.get())
				.unlockedBy("has_corrupted_book", has(ModItems.CORRUPTED_BOOK.get()))
				.save(writer, CelestiumMod.id("corrupted_enchanting_table"));

		// Le bloc corrompu se detaille en lingots puis en fragments, et inversement, comme tout
		// materiau du mod. C'est par cette chaine qu'on obtient le fragment qui allume le cadre.
		unpack(writer, RecipeCategory.MISC, ModItems.CORRUPTED_CELESTIUM_INGOT.get(),
				ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get(), "corrupted_celestium_ingot_from_block");
		pack(writer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get(),
				ModItems.CORRUPTED_CELESTIUM_INGOT.get(), "corrupted_celestium_block_from_ingots");

		unpack(writer, RecipeCategory.MISC, ModItems.CORRUPTED_CELESTIUM_FRAGMENT.get(),
				ModItems.CORRUPTED_CELESTIUM_INGOT.get(), "corrupted_celestium_fragment_from_ingot");
		pack(writer, RecipeCategory.MISC, ModItems.CORRUPTED_CELESTIUM_INGOT.get(),
				ModItems.CORRUPTED_CELESTIUM_FRAGMENT.get(), "corrupted_celestium_ingot_from_fragments");

		// Les outils du voyage. Ils se montent sur un baton ordinaire : cette panoplie ne doit rien
		// devoir aux Terres du demon, puisqu'elle est ce qui permet d'y mettre les pieds.
		Item corruptedIngot = ModItems.CORRUPTED_CELESTIUM_INGOT.get();
		tool(writer, ModItems.CORRUPTED_CELESTIUM_PICKAXE.get(), corruptedIngot, Items.STICK,
				"III", " S ", " S ");
		tool(writer, ModItems.CORRUPTED_CELESTIUM_SWORD.get(), corruptedIngot, Items.STICK,
				"I", "I", "S");
		tool(writer, ModItems.CORRUPTED_CELESTIUM_AXE.get(), corruptedIngot, Items.STICK,
				"II", "IS", " S");
		tool(writer, ModItems.CORRUPTED_CELESTIUM_SHOVEL.get(), corruptedIngot, Items.STICK,
				"I", "S", "S");
		tool(writer, ModItems.CORRUPTED_CELESTIUM_HOE.get(), corruptedIngot, Items.STICK,
				"II", " S", " S");

		// La tenue du voyage, fabricable des l'Overworld.
		armour(writer, ModItems.CORRUPTED_CELESTIUM_HELMET.get(),
				ModItems.CORRUPTED_CELESTIUM_INGOT.get(), "III", "I I");
		armour(writer, ModItems.CORRUPTED_CELESTIUM_CHESTPLATE.get(),
				ModItems.CORRUPTED_CELESTIUM_INGOT.get(), "I I", "III", "III");
		armour(writer, ModItems.CORRUPTED_CELESTIUM_LEGGINGS.get(),
				ModItems.CORRUPTED_CELESTIUM_INGOT.get(), "III", "I I", "I I");
		armour(writer, ModItems.CORRUPTED_CELESTIUM_BOOTS.get(),
				ModItems.CORRUPTED_CELESTIUM_INGOT.get(), "I I", "I I");

		// L'autel : du Demonium travaille sur un socle de bois du demon, autour d'un coeur corrompu.
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SUMMONING_ALTAR.get())
				.pattern(" D ")
				.pattern("DCD")
				.pattern("PPP")
				.define('D', ModItems.DEMONIUM_INGOT.get())
				.define('C', ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get())
				.define('P', ModBlocks.BOIS_DU_DEMON.planks.get())
				.unlockedBy("has_demonium_ingot", has(ModItems.DEMONIUM_INGOT.get()))
				.save(writer, CelestiumMod.id("summoning_altar"));
	}

	/**
	 * Le Demonium suit la meme progression que le Celestium : fragment, lingot, bloc, baton, puis
	 * outils et armure. Seules les matieres changent, les formes restent celles du jeu de base.
	 */
	private void demonium(Consumer<FinishedRecipe> writer) {
		Item fragment = ModItems.DEMONIUM_FRAGMENT.get();
		Item ingot = ModItems.DEMONIUM_INGOT.get();
		Item stick = ModItems.DEMONIUM_STICK.get();
		ItemLike block = ModBlocks.DEMONIUM_BLOCK.get();

		pack(writer, RecipeCategory.MISC, ingot, fragment, "demonium_ingot_from_fragments");
		unpack(writer, RecipeCategory.MISC, fragment, ingot, "demonium_fragment_from_ingot");

		pack(writer, RecipeCategory.BUILDING_BLOCKS, block, ingot, "demonium_block_from_ingots");
		unpack(writer, RecipeCategory.MISC, ingot, block, "demonium_ingot_from_block");

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, stick, 4)
				.pattern("F")
				.pattern("F")
				.define('F', fragment)
				.unlockedBy("has_demonium_fragment", has(fragment))
				.save(writer, CelestiumMod.id("demonium_stick_from_fragments"));

		tool(writer, ModItems.DEMONIUM_PICKAXE.get(), ingot, stick, "III", " S ", " S ");
		tool(writer, ModItems.DEMONIUM_SWORD.get(), ingot, stick, "I", "I", "S");
		tool(writer, ModItems.DEMONIUM_AXE.get(), ingot, stick, "II", "IS", " S");
		tool(writer, ModItems.DEMONIUM_SHOVEL.get(), ingot, stick, "I", "S", "S");
		tool(writer, ModItems.DEMONIUM_HOE.get(), ingot, stick, "II", " S", " S");

		armour(writer, ModItems.DEMONIUM_HELMET.get(), ingot, "III", "I I");
		armour(writer, ModItems.DEMONIUM_CHESTPLATE.get(), ingot, "I I", "III", "III");
		armour(writer, ModItems.DEMONIUM_LEGGINGS.get(), ingot, "III", "I I", "I I");
		armour(writer, ModItems.DEMONIUM_BOOTS.get(), ingot, "I I", "I I");
	}

	private void celestium(Consumer<FinishedRecipe> writer) {
		Item fragment = ModItems.CELESTIUM_FRAGMENT.get();
		Item ingot = ModItems.CELESTIUM_INGOT.get();
		Item stick = ModItems.CELESTIUM_STICK.get();
		ItemLike block = ModBlocks.CELESTIUM_BLOCK.get();

		// Les deux paliers de compactage, chacun avec sa recette inverse.
		pack(writer, RecipeCategory.MISC, ingot, fragment, "celestium_ingot_from_fragments");
		unpack(writer, RecipeCategory.MISC, fragment, ingot, "celestium_fragment_from_ingot");

		pack(writer, RecipeCategory.BUILDING_BLOCKS, block, ingot, "celestium_block_from_ingots");
		unpack(writer, RecipeCategory.MISC, ingot, block, "celestium_ingot_from_block");

		// Deux fragments donnent un baton, deux lingots en donnent neuf.
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, stick)
				.pattern("F")
				.pattern("F")
				.define('F', fragment)
				.unlockedBy("has_celestium_fragment", has(fragment))
				.save(writer, CelestiumMod.id("celestium_stick_from_fragments"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, stick, 9)
				.pattern("I")
				.pattern("I")
				.define('I', ingot)
				.unlockedBy("has_celestium_ingot", has(ingot))
				.save(writer, CelestiumMod.id("celestium_stick_from_ingots"));

		tool(writer, ModItems.CELESTIUM_PICKAXE.get(), ingot, stick, "III", " S ", " S ");
		tool(writer, ModItems.CELESTIUM_SWORD.get(), ingot, stick, "I", "I", "S");
		tool(writer, ModItems.CELESTIUM_AXE.get(), ingot, stick, "II", "IS", " S");
		tool(writer, ModItems.CELESTIUM_SHOVEL.get(), ingot, stick, "I", "S", "S");
		tool(writer, ModItems.CELESTIUM_HOE.get(), ingot, stick, "II", " S", " S");

		armour(writer, ModItems.CELESTIUM_HELMET.get(), ingot, "III", "I I");
		armour(writer, ModItems.CELESTIUM_CHESTPLATE.get(), ingot, "I I", "III", "III");
		armour(writer, ModItems.CELESTIUM_LEGGINGS.get(), ingot, "III", "I I", "I I");
		armour(writer, ModItems.CELESTIUM_BOOTS.get(), ingot, "I I", "I I");
	}

	/** Neuf unites du composant donnent une unite du produit. */
	private void pack(Consumer<FinishedRecipe> writer, RecipeCategory category, ItemLike result,
			ItemLike component, String id) {
		ShapedRecipeBuilder.shaped(category, result)
				.pattern("CCC")
				.pattern("CCC")
				.pattern("CCC")
				.define('C', component)
				.unlockedBy("has_component", has(component))
				.save(writer, CelestiumMod.id(id));
	}

	/** Une unite du composant redonne neuf unites du produit. */
	private void unpack(Consumer<FinishedRecipe> writer, RecipeCategory category, ItemLike result,
			ItemLike component, String id) {
		ShapelessRecipeBuilder.shapeless(category, result, 9)
				.requires(component)
				.unlockedBy("has_component", has(component))
				.save(writer, CelestiumMod.id(id));
	}

	private void tool(Consumer<FinishedRecipe> writer, ItemLike result, Item ingot, Item stick, String... pattern) {
		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result);
		for (String row : pattern) {
			builder.pattern(row);
		}
		builder.define('I', ingot)
				.define('S', stick)
				.unlockedBy("has_celestium_ingot", has(ingot))
				.save(writer);
	}

	private void armour(Consumer<FinishedRecipe> writer, ItemLike result, Item ingot, String... pattern) {
		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result);
		for (String row : pattern) {
			builder.pattern(row);
		}
		builder.define('I', ingot)
				.unlockedBy("has_celestium_ingot", has(ingot))
				.save(writer);
	}

	/** Les neuf recettes d'une essence, deduites de ses blocs. */
	private void woodSet(Consumer<FinishedRecipe> writer, WoodSet set) {
		String prefix = set.getName();
		ItemLike log = set.log.get();
		ItemLike planks = set.planks.get();
		Ingredient fromPlanks = Ingredient.of(planks);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, 4)
				.requires(ModTags.Items.BOIS_DU_DEMON_LOGS)
				.group("planks")
				.unlockedBy("has_log", has(ModTags.Items.BOIS_DU_DEMON_LOGS))
				.save(writer, CelestiumMod.id(prefix + "_planks"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, set.wood.get(), 3)
				.pattern("LL")
				.pattern("LL")
				.define('L', log)
				.group("bark")
				.unlockedBy("has_log", has(log))
				.save(writer, CelestiumMod.id(prefix + "_wood"));

		stairBuilder(set.stairs.get(), fromPlanks)
				.unlockedBy("has_planks", has(planks)).save(writer);
		slabBuilder(RecipeCategory.BUILDING_BLOCKS, set.slab.get(), fromPlanks)
				.unlockedBy("has_planks", has(planks)).save(writer);
		fenceBuilder(set.fence.get(), fromPlanks)
				.unlockedBy("has_planks", has(planks)).save(writer);
		fenceGateBuilder(set.fenceGate.get(), fromPlanks)
				.unlockedBy("has_planks", has(planks)).save(writer);
		pressurePlateBuilder(RecipeCategory.REDSTONE, set.pressurePlate.get(), fromPlanks)
				.unlockedBy("has_planks", has(planks)).save(writer);
		buttonBuilder(set.button.get(), fromPlanks)
				.unlockedBy("has_planks", has(planks)).save(writer);

		// Le bois du demon donne des batons ordinaires, comme toute essence.
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.STICK, 4)
				.requires(planks, 2)
				.group("sticks")
				.unlockedBy("has_planks", has(planks))
				.save(writer, CelestiumMod.id("stick_from_" + prefix + "_planks"));
	}
}
