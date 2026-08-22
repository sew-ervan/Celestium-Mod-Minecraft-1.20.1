package net.celestium.datagen;

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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

/**
 * Genere les recettes du mod.
 *
 * <p>Les outils reprennent les formes vanilla. Le mod d'origine alignait le manche de la hache et
 * de la houe sur la colonne de gauche, une forme qu'aucun joueur ne devine.
 */
public class ModRecipeProvider extends RecipeProvider {

	public ModRecipeProvider(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> writer) {
		celestium(writer);
		woodSet(writer, ModBlocks.BOIS_DU_DEMON);
	}

	private void celestium(Consumer<FinishedRecipe> writer) {
		Item fragment = ModItems.CELESTIUM_FRAGMENT.get();
		Item ingot = ModItems.CELESTIUM_INGOT.get();
		Item stick = ModItems.CELESTIUM_STICK.get();

		// Les deux paliers de compactage, avec leurs recettes inverses.
		nineBlockStorageRecipes(writer, RecipeCategory.MISC, fragment, RecipeCategory.MISC, ingot);
		nineBlockStorageRecipes(writer, RecipeCategory.MISC, ingot,
				RecipeCategory.BUILDING_BLOCKS, ModBlocks.CELESTIUM_BLOCK.get().asItem());

		// Deux fragments donnent un baton, deux lingots en donnent neuf.
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, stick)
				.pattern("F")
				.pattern("F")
				.define('F', fragment)
				.unlockedBy("has_celestium_fragment", has(fragment))
				.save(writer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, stick, 9)
				.pattern("I")
				.pattern("I")
				.define('I', ingot)
				.unlockedBy("has_celestium_ingot", has(ingot))
				.save(writer, "celestium:celestium_stick_from_ingots");

		// Outils.
		tool(writer, ModItems.CELESTIUM_PICKAXE.get(), ingot, stick, "III", " S ", " S ");
		tool(writer, ModItems.CELESTIUM_SWORD.get(), ingot, stick, "I", "I", "S");
		tool(writer, ModItems.CELESTIUM_AXE.get(), ingot, stick, "II", "IS", " S");
		tool(writer, ModItems.CELESTIUM_SHOVEL.get(), ingot, stick, "I", "S", "S");
		tool(writer, ModItems.CELESTIUM_HOE.get(), ingot, stick, "II", " S", " S");

		// Armure.
		armour(writer, ModItems.CELESTIUM_HELMET.get(), ingot, "III", "I I");
		armour(writer, ModItems.CELESTIUM_CHESTPLATE.get(), ingot, "I I", "III", "III");
		armour(writer, ModItems.CELESTIUM_LEGGINGS.get(), ingot, "III", "I I", "I I");
		armour(writer, ModItems.CELESTIUM_BOOTS.get(), ingot, "I I", "I I");
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
		ItemLike log = set.log.get();
		ItemLike planks = set.planks.get();
		Ingredient fromPlanks = Ingredient.of(planks);

		planksFromLogs(writer, planks, ModTags.Items.BOIS_DU_DEMON_LOGS, 4);
		woodFromLogs(writer, set.wood.get(), log);

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
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, net.minecraft.world.item.Items.STICK, 4)
				.requires(planks, 2)
				.unlockedBy("has_planks", has(planks))
				.save(writer, "celestium:stick_from_bois_du_demon_planks");
	}
}
