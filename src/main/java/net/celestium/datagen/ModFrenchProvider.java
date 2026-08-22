package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.WoodSet;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

/**
 * Traductions francaises, langue de reference du mod.
 *
 * <p>Le fichier de langue du mod d'origine dupliquait chaque cle sous un ancien identifiant de mod
 * ({@code celestia_univers_}), et laissait les dix blocs de bois en anglais brut.
 */
public class ModFrenchProvider extends LanguageProvider {

	public ModFrenchProvider(PackOutput output) {
		super(output, CelestiumMod.MOD_ID, "fr_fr");
	}

	@Override
	protected void addTranslations() {
		add("itemGroup.celestium.celestial_univers", "Celestial Univers");

		addItem(ModItems.CELESTIUM_FRAGMENT, "Fragment céleste");
		addItem(ModItems.CELESTIUM_INGOT, "Lingot de Celestium");
		addItem(ModItems.CELESTIUM_STICK, "Bâton de Celestium");

		addItem(ModItems.CELESTIUM_SWORD, "Épée en Celestium");
		addItem(ModItems.CELESTIUM_PICKAXE, "Pioche en Celestium");
		addItem(ModItems.CELESTIUM_AXE, "Hache en Celestium");
		addItem(ModItems.CELESTIUM_SHOVEL, "Pelle en Celestium");
		addItem(ModItems.CELESTIUM_HOE, "Houe en Celestium");

		addItem(ModItems.CELESTIUM_HELMET, "Casque en Celestium");
		addItem(ModItems.CELESTIUM_CHESTPLATE, "Plastron en Celestium");
		addItem(ModItems.CELESTIUM_LEGGINGS, "Jambières en Celestium");
		addItem(ModItems.CELESTIUM_BOOTS, "Bottes en Celestium");

		addBlock(ModBlocks.CELESTIUM_ORE, "Minerai de Celestium");
		addBlock(ModBlocks.CELESTIUM_BLOCK, "Bloc de Celestium");
		addBlock(ModBlocks.LUCKY_BLOCK, "Bloc chance");

		WoodSet demon = ModBlocks.BOIS_DU_DEMON;
		addBlock(demon.log, "Rondin de bois du démon");
		addBlock(demon.wood, "Bois du démon");
		addBlock(demon.planks, "Planches en bois du démon");
		addBlock(demon.leaves, "Feuilles du démon");
		addBlock(demon.stairs, "Escalier en bois du démon");
		addBlock(demon.slab, "Dalle en bois du démon");
		addBlock(demon.fence, "Barrière en bois du démon");
		addBlock(demon.fenceGate, "Portillon en bois du démon");
		addBlock(demon.pressurePlate, "Plaque de pression en bois du démon");
		addBlock(demon.button, "Bouton en bois du démon");
	}
}
