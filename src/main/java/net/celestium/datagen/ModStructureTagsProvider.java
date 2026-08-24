package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.ModTags;
import net.celestium.worldgen.ModStructures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * Tags de structures.
 *
 * <p>Sans tag, une structure reste adressable par son identifiant complet mais n'apparait dans
 * aucun regroupement : {@code /locate structure #celestium:structures} les trouve toutes d'un coup.
 *
 * <p>A savoir : {@code /locate structure} ne cherche que dans la dimension courante. Le village
 * demoniaque ne se trouve donc que depuis les Terres du demon, et le cimetiere que depuis
 * l'Overworld.
 */
public class ModStructureTagsProvider extends TagsProvider<Structure> {

	public ModStructureTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
			ExistingFileHelper existingFileHelper) {
		super(output, Registries.STRUCTURE, lookupProvider, CelestiumMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(ModTags.Structures.ALL)
				.add(ModStructures.CEMETERY)
				.add(ModStructures.DEMON_VILLAGE)
			.add(ModStructures.CORRUPTED_SANCTUM);
	}
}
