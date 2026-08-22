package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Point d'entree de la generation de donnees.
 *
 * <p>Se lance avec {@code gradlew runData} et ecrit dans {@code src/generated/resources}, dossier
 * declare comme source de ressources dans {@code build.gradle}. Ne jamais editer un fichier de ce
 * dossier a la main : il est reecrit a chaque execution.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	private DataGenerators() {
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

		// Ressources client : modeles, blockstates, langues.
		generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, fileHelper));
		generator.addProvider(event.includeClient(), new ModItemModelProvider(output, fileHelper));
		generator.addProvider(event.includeClient(), new ModFrenchProvider(output));
		generator.addProvider(event.includeClient(), new ModEnglishProvider(output));

		// Donnees serveur : recettes, tags, tables de butin.
		generator.addProvider(event.includeServer(), new ModRecipeProvider(output));

		ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, lookup, fileHelper);
		generator.addProvider(event.includeServer(), blockTags);
		generator.addProvider(event.includeServer(),
				new ModItemTagsProvider(output, lookup, blockTags.contentsGetter(), fileHelper));

		generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(),
				List.of(new LootTableProvider.SubProviderEntry(
						ModBlockLootTables::new, LootContextParamSets.BLOCK))));
	}
}
