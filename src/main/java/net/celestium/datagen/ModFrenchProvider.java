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

		// --- Dimension demoniaque ---
		add("biome.celestium.demon_wastes", "Terres du démon");
		add("message.celestium.portal.entered", "Tu franchis le voile. Les terres du démon te happent.");
		add("message.celestium.portal.returned", "Tu regagnes le monde des vivants.");

		// --- Progrès ---
		add("advancements.premier_celestium.title", "Premier Celestium");
		add("advancements.premier_celestium.descr", "Tu as obtenu ton premier fragment de Celestium.");

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

		// --- Demonium ---
		addItem(ModItems.DEMONIUM_FRAGMENT, "Fragment de Demonium");
		addItem(ModItems.DEMONIUM_INGOT, "Lingot de Demonium");
		addItem(ModItems.DEMONIUM_STICK, "Bâton de Demonium");

		addItem(ModItems.DEMONIUM_SWORD, "Épée en Demonium");
		addItem(ModItems.DEMONIUM_PICKAXE, "Pioche en Demonium");
		addItem(ModItems.DEMONIUM_AXE, "Hache en Demonium");
		addItem(ModItems.DEMONIUM_SHOVEL, "Pelle en Demonium");
		addItem(ModItems.DEMONIUM_HOE, "Houe en Demonium");

		addItem(ModItems.DEMONIUM_HELMET, "Casque en Demonium");
		addItem(ModItems.DEMONIUM_CHESTPLATE, "Plastron en Demonium");
		addItem(ModItems.DEMONIUM_LEGGINGS, "Jambières en Demonium");
		addItem(ModItems.DEMONIUM_BOOTS, "Bottes en Demonium");

		addBlock(ModBlocks.DEMONIUM_ORE, "Minerai de Demonium");
		addBlock(ModBlocks.DEMONIUM_BLOCK, "Bloc de Demonium");
		addBlock(ModBlocks.CORRUPTED_CELESTIUM_BLOCK, "Celestium corrompu");

		// --- Celestium corrompu ---
		addItem(ModItems.CORRUPTED_CELESTIUM_FRAGMENT, "Fragment de Celestium corrompu");
		addItem(ModItems.CORRUPTED_CELESTIUM_INGOT, "Lingot de Celestium corrompu");

		addItem(ModItems.CORRUPTED_CELESTIUM_SWORD, "Épée en Celestium corrompu");
		addItem(ModItems.CORRUPTED_CELESTIUM_PICKAXE, "Pioche en Celestium corrompu");
		addItem(ModItems.CORRUPTED_CELESTIUM_AXE, "Hache en Celestium corrompu");
		addItem(ModItems.CORRUPTED_CELESTIUM_SHOVEL, "Pelle en Celestium corrompu");
		addItem(ModItems.CORRUPTED_CELESTIUM_HOE, "Houe en Celestium corrompu");
		addItem(ModItems.CORRUPTED_CELESTIUM_HELMET, "Casque en Celestium corrompu");
		addItem(ModItems.CORRUPTED_CELESTIUM_CHESTPLATE, "Plastron en Celestium corrompu");
		addItem(ModItems.CORRUPTED_CELESTIUM_LEGGINGS, "Jambières en Celestium corrompu");
		addItem(ModItems.CORRUPTED_CELESTIUM_BOOTS, "Bottes en Celestium corrompu");

		add("message.celestium.corruption.unprotected", "La corruption te ronge. Il te faut une armure.");
		add("message.celestium.mining.refused",
				"Cette pierre ne cède qu'au Celestium corrompu ou au Demonium.");
		add("death.attack.corruption", "%1$s a été rongé par la corruption");
		add("death.attack.corruption.player", "%1$s a été rongé par la corruption en affrontant %2$s");

		// --- Creatures des Terres du demon ---
		add("entity.celestium.parasite", "Parasite");
		add("entity.celestium.corrupted_villager", "Villageois corrompu");
		addItem(ModItems.PARASITE_SPAWN_EGG, "Œuf de parasite");
		addItem(ModItems.CORRUPTED_VILLAGER_SPAWN_EGG, "Œuf de villageois corrompu");

		addBlock(ModBlocks.SUMMONING_ALTAR, "Autel d'invocation");
		add("message.celestium.altar.summoned", "L'autel s'embrase. Quelque chose répond.");

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

		// --- Camps ---
		add("faction.celestium.demon", "Démon");
		add("faction.celestium.neutre", "Neutre");
		add("faction.celestium.celeste", "Céleste");

		// --- Messages de commandes ---
		add("message.celestium.teleport.warmup", "Ne bouge pas pendant %s secondes.");
		add("message.celestium.teleport.moved", "Tu as bougé. Réessaye.");

		add("message.celestium.home.set", "Home enregistré en %s, %s, %s.");
		add("message.celestium.home.none", "Tu n'as pas encore défini de home. Utilise /sethome.");
		add("message.celestium.home.arrived", "Te voilà chez toi.");
		add("message.celestium.home.cleared", "Home supprimé.");

		add("message.celestium.spawn.none", "Aucun point d'apparition n'a été défini sur ce serveur.");
		add("message.celestium.spawn.arrived", "Bienvenue au spawn.");

		add("message.celestium.rtp.wrong_dimension", "Le /rtp ne fonctionne que dans l'Overworld.");
		add("message.celestium.rtp.already_used", "Tu as déjà utilisé le /rtp aujourd'hui.");
		add("message.celestium.rtp.success", "Téléportation réussie.");

		add("message.celestium.announce.header", "Nouvelle annonce de la part du staff");

		add("message.celestium.morph.unavailable",
				"Le /morph nécessite le mod Identity, qui n'est pas installé sur ce serveur.");

		add("message.celestium.admin.spawn_set", "Point d'apparition défini en %s, %s, %s.");
		add("message.celestium.admin.rtp_radius_set", "Rayon du /rtp réglé sur %s blocs.");
		add("message.celestium.admin.faction_set", "%s appartient désormais au camp %s.");
		add("message.celestium.admin.home_reset", "Le home de %s a été supprimé.");
		add("message.celestium.admin.unknown_faction", "Le camp « %s » n'existe pas.");

		// --- Creatures ---
		add("entity.celestium.mini_warden", "Gardien miniature");
		add("entity.celestium.demon_swordsman", "Démon épéiste");
		add("entity.celestium.celestial_bolt", "Éclair céleste");
		addItem(ModItems.MINI_WARDEN_SPAWN_EGG, "Œuf de gardien miniature");
		addItem(ModItems.DEMON_SWORDSMAN_SPAWN_EGG, "Œuf de démon épéiste");

		// --- Sacs celestes ---
		addItem(ModItems.BACKPACK, "Sac céleste");
		addItem(ModItems.BACKPACK_MEDIUM, "Sac céleste moyen");
		addItem(ModItems.BACKPACK_SMALL, "Petit sac céleste");

		// --- Sorts ---
		add("spell.celestium.celestial_strike", "Frappe céleste");
		add("spell.celestium.celestial_bolt", "Éclair céleste");
		add("message.celestium.spell.no_target", "Aucune cible en vue.");
		add("message.celestium.spell.no_mana", "Ton énergie céleste est épuisée.");
		add("message.celestium.spell.cooling_down", "Ce sort se recharge encore %s secondes.");
		add("message.celestium.spell.wrong_faction", "Ce sort est réservé au camp %s.");
	}
}
