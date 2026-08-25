package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.WoodSet;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModEnchantments;
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

		advancements();

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
		addBlock(ModBlocks.CORRUPTED_LUCKY_BLOCK, "Bloc chance corrompu");
		addBlock(ModBlocks.DEMON_LUCKY_BLOCK, "Bloc chance du démon");

		// --- Demonium ---
		addItem(ModItems.DEMON_HEART, "Cœur du démon");

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
		addBlock(ModBlocks.CORRUPTED_CELESTIUM_ORE, "Minerai de Celestium corrompu");
		addBlock(ModBlocks.CORRUPTED_PORTAL_FRAME, "Cadre corrompu");
		addBlock(ModBlocks.CORRUPTED_PORTAL, "Portail corrompu");
		addItem(ModItems.CORRUPTED_EYE, "Œil corrompu");
		addItem(ModItems.CORRUPTED_BOOK, "Livre corrompu");
		addBlock(ModBlocks.CORRUPTED_ENCHANTING_TABLE, "Table d'enchantement corrompue");

		addEnchantment(ModEnchantments.TIMBER, "Abattage");
		addEnchantment(ModEnchantments.EXCAVATION, "Excavation");

		addEnchantment(ModEnchantments.VEIN_MINER, "Filon");
		addEnchantment(ModEnchantments.HARVEST, "Moisson");
		addEnchantment(ModEnchantments.SMELTING, "Fonte");
		addEnchantment(ModEnchantments.MAGNETISM, "Aimant");
		addEnchantment(ModEnchantments.THUNDERSTRIKE, "Éclair fulgurant");
		addEnchantment(ModEnchantments.MIDAS_CURSE, "Malédiction de Midas");
		addEnchantment(ModEnchantments.TAMER, "Dompteur");

		// --- Les quatre enchantements d'arc ---
		addEnchantment(ModEnchantments.VOLLEY, "Salve céleste");
		addEnchantment(ModEnchantments.PIERCING_SHOT, "Transpercement");
		addEnchantment(ModEnchantments.SEEKER, "Traqueur");
		addEnchantment(ModEnchantments.COLLAPSE, "Effondrement");

		// --- Matiere noire ---
		addItem(ModItems.DARK_MATTER, "Matière noire");
		addItem(ModItems.DARK_MATTER_SWORD, "Épée en matière noire");
		addItem(ModItems.DARK_MATTER_PICKAXE, "Pioche en matière noire");
		addItem(ModItems.DARK_MATTER_AXE, "Hache en matière noire");
		addItem(ModItems.DARK_MATTER_SHOVEL, "Pelle en matière noire");
		addItem(ModItems.DARK_MATTER_HOE, "Houe en matière noire");
		addItem(ModItems.DARK_MATTER_HELMET, "Casque en matière noire");
		addItem(ModItems.DARK_MATTER_CHESTPLATE, "Plastron en matière noire");
		addItem(ModItems.DARK_MATTER_LEGGINGS, "Jambières en matière noire");
		addItem(ModItems.DARK_MATTER_BOOTS, "Bottes en matière noire");
		addBlock(ModBlocks.DARK_MATTER_ORE, "Minerai de matière noire");
		addBlock(ModBlocks.DARK_MATTER_BLOCK, "Bloc de matière noire");
		addBlock(ModBlocks.GRAVITY_WELL, "Puits de gravité");

		addItem(ModItems.CELESTIAL_DUST, "Poussière céleste");
		add("tooltip.celestium.celestial_dust", "Un effet au hasard, pour une durée au hasard.");
		add("message.celestium.dust.boon", "%1$s, pendant %2$s secondes.");
		add("message.celestium.dust.bane", "%1$s... pendant %2$s secondes.");

		add("container.celestium.corrupted_enchanting", "Table d'enchantement corrompue");
		add("gui.celestium.compendium", "Compendium du Celestium");
		add("gui.celestium.compendium.no_recipe",
			"Ne se fabrique pas. Se trouve dans le monde ou se gagne sur ce qu'on y rencontre.");
		add("message.celestium.enchant.cost", "%1$s niveaux");
		add("message.celestium.enchant.empty", "Pose un outil pour voir ce qu'il peut recevoir.");
		add("message.celestium.enchant.already_maxed", "Cet outil ne peut pas aller plus loin.");

		add("message.celestium.corrupted.entered",
				"Les deux mondes se heurtent ici. Ne t'attarde pas.");
		add("message.celestium.corrupted.returned", "Le monde reprend sa forme.");
		add("message.celestium.corrupted.boon", "La faille t'accorde quelque chose.");
		add("message.celestium.corrupted.bane", "La faille te prend quelque chose.");
		add("message.celestium.corrupted.wanderers", "Quelque chose a traversé.");
		add("message.celestium.corrupted.spreading", "La roche change autour de toi...");

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

		// Ce que le bloc chance annonce. Le texte est le seul indice de ce qui arrive : il doit se
		// lire en une seconde, avant que l'evenement ne parle de lui-meme.
		add("message.celestium.lucky.gift", "Un présent.");
		add("message.celestium.lucky.burst", "Ça déborde !");
		add("message.celestium.lucky.experience", "Un torrent de savoir.");
		add("message.celestium.lucky.blessing", "Quelque chose veille sur toi.");
		add("message.celestium.lucky.vein", "Le sol s'est enrichi.");
		add("message.celestium.lucky.beacon", "Une lumière s'élève.");
		add("message.celestium.lucky.outfit", "De quoi t'armer.");
		add("message.celestium.lucky.relic", "Le cœur d'un démon, offert.");

		add("message.celestium.lucky.horde", "Tu n'es plus seul.");
		add("message.celestium.lucky.creepers", "Ça siffle derrière toi...");
		add("message.celestium.lucky.invasion", "Ils arrivent en nombre !");
		add("message.celestium.lucky.parasites", "La nuée te submerge !");
		add("message.celestium.lucky.demon", "Il t'a trouvé.");
		add("message.celestium.lucky.tnt", "Cours.");
		add("message.celestium.lucky.detonation", "Trop tard.");
		add("message.celestium.lucky.lightning", "Le ciel te tombe dessus.");
		add("message.celestium.lucky.curse", "Quelque chose te ronge.");
		add("message.celestium.lucky.arrows", "Lève les yeux.");
		add("message.celestium.lucky.anvils", "Ne reste pas là !");
		add("message.celestium.lucky.cage", "Enfermé.");
		add("message.celestium.lucky.lava", "Ça chauffe.");
		add("message.celestium.lucky.pitfall", "Le sol s'ouvre !");

		// --- Creatures des Terres du demon ---
		add("entity.celestium.celestial_dragon", "Dragon céleste");
		addItem(ModItems.CELESTIAL_DRAGON_SPAWN_EGG, "Œuf de dragon céleste");
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
		addItem(ModItems.BACKPACK, "Grand sac céleste");
		addItem(ModItems.BACKPACK_HUGE, "Sac céleste énorme");
		addItem(ModItems.BACKPACK_MEDIUM, "Sac céleste moyen");
		addItem(ModItems.BACKPACK_SMALL, "Petit sac céleste");

		// --- Equipement de voyage ---
		addItem(ModItems.INVISIBILITY_CLOAK, "Cape d'invisibilité");
		addItem(ModItems.TANDEM_SADDLE, "Selle deux places");
		addItem(ModItems.CELESTIAL_BOW, "Arc céleste");

		// --- Sorts ---
		add("spell.celestium.celestial_strike", "Frappe céleste");
		add("spell.celestium.celestial_bolt", "Éclair céleste");
		add("message.celestium.spell.no_target", "Aucune cible en vue.");
		add("message.celestium.spell.no_mana", "Ton énergie céleste est épuisée.");
		add("message.celestium.spell.cooling_down", "Ce sort se recharge encore %s secondes.");
		add("message.celestium.spell.wrong_faction", "Ce sort est réservé au camp %s.");
	}

	/**
	 * Les vingt-deux progres du mod.
	 *
	 * <p>Regroupes ici plutot que dispersés dans la liste des items : ils forment un texte
	 * suivi, et les relire d'affilée est le seul moyen de vérifier que l'arbre raconte bien
	 * une progression.
	 */
	private void advancements() {

		advancement("root", "Univers Céleste", "Extraire un premier fragment de Celestium.");
		advancement("celestium_ingot", "Matière première", "Fondre des fragments en un lingot.");
		advancement("celestium_tools", "Outillé", "Forger un outil en Celestium.");
		advancement("celestium_armour", "Vêtu d'étoiles", "Porter le plastron en Celestium.");
		advancement("backpack", "Les poches pleines", "Fabriquer un sac céleste.");
		advancement("huge_backpack", "Déménageur", "Fabriquer le sac céleste énorme, et ses vingt rangées.");
		advancement("lucky_block", "Tenter sa chance", "Mettre la main sur un bloc chance.");

		advancement("corrupted_eye", "Un œil qui s'ouvre", "Fabriquer un œil corrompu. Il en faudra douze.");
		advancement("corrupted_frame", "Pierre de seuil", "Obtenir un cadre corrompu.");
		advancement("enter_corrupted", "Là où deux mondes se heurtent", "Franchir le portail des terres corrompues.");
		advancement("corrupted_ore", "Matière souillée", "Extraire du Celestium corrompu.");
		advancement("corrupted_tools", "De quoi creuser là-bas", "Forger un outil en Celestium corrompu.");
		advancement("corrupted_armour", "Tenue de voyage", "Porter le plastron en Celestium corrompu.");
		advancement("corrupted_book", "Grimoire", "Tremper un livre dans la corruption.");
		advancement("enchanting_table", "L'autre table", "Dresser la table d'enchantement corrompue.");

		advancement("demon_frame", "Le cadre du démon", "Obtenir un bloc de Celestium corrompu.");
		advancement("enter_demon", "Les Terres du démon", "Franchir le portail du démon.");
		advancement("demonium", "Le métal d'en bas", "Extraire du Demonium.");
		advancement("demonium_armour", "Armure du démon", "Porter le plastron en Demonium.");
		advancement("demon_wood", "Bois qui ne brûle pas seul", "Abattre un arbre des Terres du démon.");
		advancement("summoning_altar", "Rappeler le démon", "Dresser un autel d'invocation.");
		advancement("demon_heart", "Cœur arraché", "Abattre le démon épéiste et prendre son cœur.");

		advancement("dark_matter", "Ce dont le ciel est fait", "Extraire de la matière noire.");
		advancement("gravity_well", "Tout vient à soi", "Dresser un puits de gravité.");
		advancement("dark_matter_armour", "Ancré", "Porter le plastron en matière noire.");

		advancement("celestial_dragon", "Ce qui dormait sur l'or",
				"Abattre un dragon céleste et lui prendre son tas.");
		advancement("invisibility_cloak", "Personne ici",
				"Fabriquer la cape d'invisibilité.");
		advancement("tandem_saddle", "Monte derrière",
				"Fabriquer une selle deux places.");

		// --- Ce que l'Overworld reserve ---
		advancement("celestium_block", "Réserve", "Compacter des lingots en bloc de Celestium.");
		advancement("celestial_dust", "Poussière d'étoile", "Fabriquer de la poussière céleste.");
		advancement("dust_trip", "Voir le ciel de trop près", "Boire une fiole de poussière céleste.");
		advancement("cemetery", "Terre remuée", "Trouver le cimetière.");

		// --- Les blocs chance ---
		advancement("corrupted_lucky_block", "La chance tourne", "Mettre la main sur un bloc chance corrompu.");
		advancement("demon_lucky_block", "Tenter le diable", "Mettre la main sur un bloc chance du démon.");

		// --- L'arc ---
		advancement("celestial_bow", "Tendre la corde", "Forger l'arc céleste.");
		advancement("volley", "Trois d'un coup", "Obtenir la Salve céleste.");
		advancement("piercing_shot", "À la file", "Obtenir le Transpercement.");
		advancement("seeker", "La main corrigée", "Obtenir le Traqueur.");
		advancement("collapse", "Tout vient à la flèche", "Obtenir l'Effondrement.");
		advancement("complete_bow", "L'arc achevé", "Réunir les quatre enchantements d'arc sur le même arc.");

		// --- Les structures ---
		advancement("sanctum", "Sous la pierre", "Trouver un sanctuaire corrompu.");
		advancement("celestial_hoard", "Tout cet or", "Trouver le tas d'un dragon céleste.");
		advancement("demon_village", "Ils vivent ici", "Trouver un village des Terres du démon.");
		advancement("parasite", "Nettoyage", "Abattre un parasite.");

		// --- Ce que la table corrompue accorde ---
		advancement("corrupted_enchant", "La table parle", "Enchanter un objet à la table corrompue.");
		advancement("timber", "L'arbre entier", "Obtenir l'Abattage.");
		advancement("vein_miner", "Jusqu'au bout du filon", "Obtenir le Filon.");
		advancement("excavation", "Creuser large", "Obtenir l'Excavation.");
		advancement("harvest", "D'un seul geste", "Obtenir la Moisson.");
		advancement("smelting", "Déjà fondu", "Obtenir la Fonte.");
		advancement("magnetism", "Rien ne traîne", "Obtenir l'Aimant.");
		advancement("thunderstrike", "Le ciel s'en mêle", "Obtenir l'Éclair fulgurant.");
		advancement("tamer", "On se calme", "Obtenir le Dompteur.");
		advancement("midas_curse", "Tout ce qu'il touche", "Poser la Malédiction de Midas sur sa pioche.");

		// --- La licorne et les familiers ---
		addItem(ModItems.UNICORN_HORN, "Corne de licorne");
		addItem(ModItems.UNICORN_HORN_SWORD, "Épée en corne de licorne");
		addItem(ModItems.UNICORN_HORN_HAT, "Chapeau en corne de licorne");
		addItem(ModItems.UNICORN_FOAL_EGG, "Œuf de poulain céleste");
		addItem(ModItems.UNICORN_SPAWN_EGG, "Œuf de licorne");
		addItem(ModItems.FENNEC_SPAWN_EGG, "Œuf de fennec");
		addItem(ModItems.MINI_GUARDIAN_SPAWN_EGG, "Œuf de petit gardien");
		addItem(ModItems.MINI_DEMON_SPAWN_EGG, "Œuf de petit démon");
		add("entity.celestium.unicorn", "Licorne");
		add("entity.celestium.fennec", "Fennec");
		add("entity.celestium.mini_guardian", "Petit gardien");
		add("entity.celestium.mini_demon", "Petit démon");

		// --- La licorne et les familiers ---
		advancement("unicorn", "Elle existait donc", "Abattre une licorne.");
		advancement("unicorn_horn", "Deux chances sur cent", "Récupérer la corne d'une licorne.");
		advancement("horn_sword", "Montée en lame", "Forger l'épée en corne de licorne.");
		advancement("horn_hat", "Sur la tête", "Coiffer le chapeau en corne de licorne.");
		advancement("unicorn_foal", "Ce qui reste d'elle", "Récupérer un œuf de poulain céleste.");
		advancement("fennec", "Une raison de traverser le désert", "Apprivoiser un fennec.");
		advancement("mini_guardian", "Le seul qu'on puisse approcher", "Apprivoiser un petit gardien.");
		advancement("mini_demon", "Ramené de là-bas", "Apprivoiser un petit démon.");
		advancement("every_familiar", "Bonne compagnie",
				"Apprivoiser le fennec, le petit gardien et le petit démon.");
	}

	/** Un progrès : son titre et sa description, sous les deux clés attendues. */
	private void advancement(String name, String title, String description) {
		add("advancements.celestium." + name + ".title", title);
		add("advancements.celestium." + name + ".descr", description);
	}
}
