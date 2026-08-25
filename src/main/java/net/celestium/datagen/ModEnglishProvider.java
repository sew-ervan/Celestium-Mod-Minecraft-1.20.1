package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.WoodSet;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModEnchantments;
import net.celestium.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

/** Traductions anglaises. Doit couvrir exactement les memes cles que {@link ModFrenchProvider}. */
public class ModEnglishProvider extends LanguageProvider {

	public ModEnglishProvider(PackOutput output) {
		super(output, CelestiumMod.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		add("itemGroup.celestium.celestial_univers", "Celestial Univers");

		// --- Demon dimension ---
		add("biome.celestium.demon_wastes", "Demon Wastes");
		add("message.celestium.portal.entered", "You cross the veil. The demon wastes claim you.");
		add("message.celestium.portal.returned", "You return to the world of the living.");

		advancements();

		addItem(ModItems.CELESTIUM_FRAGMENT, "Celestial Fragment");
		addItem(ModItems.CELESTIUM_INGOT, "Celestium Ingot");
		addItem(ModItems.CELESTIUM_STICK, "Celestium Stick");

		addItem(ModItems.CELESTIUM_SWORD, "Celestium Sword");
		addItem(ModItems.CELESTIUM_PICKAXE, "Celestium Pickaxe");
		addItem(ModItems.CELESTIUM_AXE, "Celestium Axe");
		addItem(ModItems.CELESTIUM_SHOVEL, "Celestium Shovel");
		addItem(ModItems.CELESTIUM_HOE, "Celestium Hoe");

		addItem(ModItems.CELESTIUM_HELMET, "Celestium Helmet");
		addItem(ModItems.CELESTIUM_CHESTPLATE, "Celestium Chestplate");
		addItem(ModItems.CELESTIUM_LEGGINGS, "Celestium Leggings");
		addItem(ModItems.CELESTIUM_BOOTS, "Celestium Boots");

		addBlock(ModBlocks.CELESTIUM_ORE, "Celestium Ore");
		addBlock(ModBlocks.CELESTIUM_BLOCK, "Block of Celestium");
		addBlock(ModBlocks.LUCKY_BLOCK, "Lucky Block");
		addBlock(ModBlocks.CORRUPTED_LUCKY_BLOCK, "Corrupted Lucky Block");
		addBlock(ModBlocks.DEMON_LUCKY_BLOCK, "Demon Lucky Block");

		// --- Demonium ---
		addItem(ModItems.DEMON_HEART, "Demon Heart");

		addItem(ModItems.DEMONIUM_FRAGMENT, "Demonium Scrap");
		addItem(ModItems.DEMONIUM_INGOT, "Demonium Ingot");
		addItem(ModItems.DEMONIUM_STICK, "Demonium Rod");

		addItem(ModItems.DEMONIUM_SWORD, "Demonium Sword");
		addItem(ModItems.DEMONIUM_PICKAXE, "Demonium Pickaxe");
		addItem(ModItems.DEMONIUM_AXE, "Demonium Axe");
		addItem(ModItems.DEMONIUM_SHOVEL, "Demonium Shovel");
		addItem(ModItems.DEMONIUM_HOE, "Demonium Hoe");

		addItem(ModItems.DEMONIUM_HELMET, "Demonium Helmet");
		addItem(ModItems.DEMONIUM_CHESTPLATE, "Demonium Chestplate");
		addItem(ModItems.DEMONIUM_LEGGINGS, "Demonium Leggings");
		addItem(ModItems.DEMONIUM_BOOTS, "Demonium Boots");

		addBlock(ModBlocks.DEMONIUM_ORE, "Demonium Ore");
		addBlock(ModBlocks.DEMONIUM_BLOCK, "Block of Demonium");
		addBlock(ModBlocks.CORRUPTED_CELESTIUM_BLOCK, "Corrupted Celestium");

		// --- Corrupted Celestium ---
		addBlock(ModBlocks.CORRUPTED_CELESTIUM_ORE, "Corrupted Celestium Ore");
		addBlock(ModBlocks.CORRUPTED_PORTAL_FRAME, "Corrupted Frame");
		addBlock(ModBlocks.CORRUPTED_PORTAL, "Corrupted Portal");
		addItem(ModItems.CORRUPTED_EYE, "Corrupted Eye");
		addItem(ModItems.CORRUPTED_BOOK, "Corrupted Book");
		addBlock(ModBlocks.CORRUPTED_ENCHANTING_TABLE, "Corrupted Enchanting Table");

		addEnchantment(ModEnchantments.TIMBER, "Timber");
		addEnchantment(ModEnchantments.EXCAVATION, "Excavation");

		addEnchantment(ModEnchantments.VEIN_MINER, "Vein Miner");
		addEnchantment(ModEnchantments.HARVEST, "Harvest");
		addEnchantment(ModEnchantments.SMELTING, "Smelting");
		addEnchantment(ModEnchantments.MAGNETISM, "Magnetism");
		addEnchantment(ModEnchantments.THUNDERSTRIKE, "Thunderstrike");
		addEnchantment(ModEnchantments.MIDAS_CURSE, "Curse of Midas");
		addEnchantment(ModEnchantments.TAMER, "Tamer");

		// --- The four bow enchantments ---
		addEnchantment(ModEnchantments.VOLLEY, "Celestial Volley");
		addEnchantment(ModEnchantments.PIERCING_SHOT, "Piercing Shot");
		addEnchantment(ModEnchantments.SEEKER, "Seeker");
		addEnchantment(ModEnchantments.COLLAPSE, "Collapse");

		// --- Dark matter ---
		addItem(ModItems.DARK_MATTER, "Dark Matter");
		addItem(ModItems.DARK_MATTER_SWORD, "Dark Matter Sword");
		addItem(ModItems.DARK_MATTER_PICKAXE, "Dark Matter Pickaxe");
		addItem(ModItems.DARK_MATTER_AXE, "Dark Matter Axe");
		addItem(ModItems.DARK_MATTER_SHOVEL, "Dark Matter Shovel");
		addItem(ModItems.DARK_MATTER_HOE, "Dark Matter Hoe");
		addItem(ModItems.DARK_MATTER_HELMET, "Dark Matter Helmet");
		addItem(ModItems.DARK_MATTER_CHESTPLATE, "Dark Matter Chestplate");
		addItem(ModItems.DARK_MATTER_LEGGINGS, "Dark Matter Leggings");
		addItem(ModItems.DARK_MATTER_BOOTS, "Dark Matter Boots");
		addBlock(ModBlocks.DARK_MATTER_ORE, "Dark Matter Ore");
		addBlock(ModBlocks.DARK_MATTER_BLOCK, "Block of Dark Matter");
		addBlock(ModBlocks.GRAVITY_WELL, "Gravity Well");

		addItem(ModItems.CELESTIAL_DUST, "Celestial Dust");
		add("tooltip.celestium.celestial_dust", "A random effect, for a random duration.");
		add("message.celestium.dust.boon", "%1$s, for %2$s seconds.");
		add("message.celestium.dust.bane", "%1$s... for %2$s seconds.");

		add("container.celestium.corrupted_enchanting", "Corrupted Enchanting Table");
		add("gui.celestium.compendium", "Celestium Compendium");
		add("gui.celestium.compendium.no_recipe",
			"Not craftable. Found in the world, or taken from what lives there.");
		add("message.celestium.enchant.cost", "%1$s levels");
		add("message.celestium.enchant.empty", "Place a tool to see what it can receive.");
		add("message.celestium.enchant.already_maxed", "This tool can go no further.");

		add("message.celestium.corrupted.entered", "Two worlds collide here. Don't linger.");
		add("message.celestium.corrupted.returned", "The world settles back into shape.");
		add("message.celestium.corrupted.boon", "The rift grants you something.");
		add("message.celestium.corrupted.bane", "The rift takes something from you.");
		add("message.celestium.corrupted.wanderers", "Something crossed over.");
		add("message.celestium.corrupted.spreading", "The stone is changing around you...");

		addItem(ModItems.CORRUPTED_CELESTIUM_FRAGMENT, "Corrupted Celestium Fragment");
		addItem(ModItems.CORRUPTED_CELESTIUM_INGOT, "Corrupted Celestium Ingot");

		addItem(ModItems.CORRUPTED_CELESTIUM_SWORD, "Corrupted Celestium Sword");
		addItem(ModItems.CORRUPTED_CELESTIUM_PICKAXE, "Corrupted Celestium Pickaxe");
		addItem(ModItems.CORRUPTED_CELESTIUM_AXE, "Corrupted Celestium Axe");
		addItem(ModItems.CORRUPTED_CELESTIUM_SHOVEL, "Corrupted Celestium Shovel");
		addItem(ModItems.CORRUPTED_CELESTIUM_HOE, "Corrupted Celestium Hoe");
		addItem(ModItems.CORRUPTED_CELESTIUM_HELMET, "Corrupted Celestium Helmet");
		addItem(ModItems.CORRUPTED_CELESTIUM_CHESTPLATE, "Corrupted Celestium Chestplate");
		addItem(ModItems.CORRUPTED_CELESTIUM_LEGGINGS, "Corrupted Celestium Leggings");
		addItem(ModItems.CORRUPTED_CELESTIUM_BOOTS, "Corrupted Celestium Boots");

		add("message.celestium.corruption.unprotected", "The corruption gnaws at you. You need armour.");
		add("message.celestium.mining.refused",
				"This stone yields only to corrupted Celestium or Demonium.");
		add("death.attack.corruption", "%1$s was consumed by the corruption");
		add("death.attack.corruption.player", "%1$s was consumed by the corruption while fighting %2$s");

		add("message.celestium.lucky.gift", "A gift.");
		add("message.celestium.lucky.burst", "It's overflowing!");
		add("message.celestium.lucky.experience", "A flood of knowledge.");
		add("message.celestium.lucky.blessing", "Something watches over you.");
		add("message.celestium.lucky.vein", "The ground grew richer.");
		add("message.celestium.lucky.beacon", "A light rises.");
		add("message.celestium.lucky.outfit", "Arm yourself.");
		add("message.celestium.lucky.relic", "A demon's heart, freely given.");

		add("message.celestium.lucky.horde", "You are not alone.");
		add("message.celestium.lucky.creepers", "Something hisses behind you...");
		add("message.celestium.lucky.invasion", "They come in numbers!");
		add("message.celestium.lucky.parasites", "The swarm engulfs you!");
		add("message.celestium.lucky.demon", "He has found you.");
		add("message.celestium.lucky.tnt", "Run.");
		add("message.celestium.lucky.detonation", "Too late.");
		add("message.celestium.lucky.lightning", "The sky falls on you.");
		add("message.celestium.lucky.curse", "Something gnaws at you.");
		add("message.celestium.lucky.arrows", "Look up.");
		add("message.celestium.lucky.anvils", "Don't stand there!");
		add("message.celestium.lucky.cage", "Trapped.");
		add("message.celestium.lucky.lava", "It's getting hot.");
		add("message.celestium.lucky.pitfall", "The ground opens!");

		// --- Demon Wastes creatures ---
		add("entity.celestium.celestial_dragon", "Celestial Dragon");
		addItem(ModItems.CELESTIAL_DRAGON_SPAWN_EGG, "Celestial Dragon Spawn Egg");
		add("entity.celestium.parasite", "Parasite");
		add("entity.celestium.corrupted_villager", "Corrupted Villager");
		addItem(ModItems.PARASITE_SPAWN_EGG, "Parasite Spawn Egg");
		addItem(ModItems.CORRUPTED_VILLAGER_SPAWN_EGG, "Corrupted Villager Spawn Egg");

		addBlock(ModBlocks.SUMMONING_ALTAR, "Summoning Altar");
		add("message.celestium.altar.summoned", "The altar blazes. Something answers.");

		WoodSet demon = ModBlocks.BOIS_DU_DEMON;
		addBlock(demon.log, "Demon Wood Log");
		addBlock(demon.wood, "Demon Wood");
		addBlock(demon.planks, "Demon Wood Planks");
		addBlock(demon.leaves, "Demon Leaves");
		addBlock(demon.stairs, "Demon Wood Stairs");
		addBlock(demon.slab, "Demon Wood Slab");
		addBlock(demon.fence, "Demon Wood Fence");
		addBlock(demon.fenceGate, "Demon Wood Fence Gate");
		addBlock(demon.pressurePlate, "Demon Wood Pressure Plate");
		addBlock(demon.button, "Demon Wood Button");

		// --- Factions ---
		add("faction.celestium.demon", "Demon");
		add("faction.celestium.neutre", "Neutral");
		add("faction.celestium.celeste", "Celestial");

		// --- Command messages ---
		add("message.celestium.teleport.warmup", "Do not move for %s seconds.");
		add("message.celestium.teleport.moved", "You moved. Try again.");

		add("message.celestium.home.set", "Home saved at %s, %s, %s.");
		add("message.celestium.home.none", "You have not set a home yet. Use /sethome.");
		add("message.celestium.home.arrived", "Welcome home.");
		add("message.celestium.home.cleared", "Home removed.");

		add("message.celestium.spawn.none", "No spawn point has been set on this server.");
		add("message.celestium.spawn.arrived", "Welcome to spawn.");

		add("message.celestium.rtp.wrong_dimension", "/rtp only works in the Overworld.");
		add("message.celestium.rtp.already_used", "You have already used /rtp today.");
		add("message.celestium.rtp.success", "Teleport successful.");

		add("message.celestium.announce.header", "New announcement from the staff");

		add("message.celestium.morph.unavailable",
				"/morph requires the Identity mod, which is not installed on this server.");

		add("message.celestium.admin.spawn_set", "Spawn point set at %s, %s, %s.");
		add("message.celestium.admin.rtp_radius_set", "/rtp radius set to %s blocks.");
		add("message.celestium.admin.faction_set", "%s now belongs to the %s faction.");
		add("message.celestium.admin.home_reset", "Home of %s has been removed.");
		add("message.celestium.admin.unknown_faction", "There is no faction named %s.");

		// --- Creatures ---
		add("entity.celestium.mini_warden", "Mini Warden");
		add("entity.celestium.demon_swordsman", "Demon Swordsman");
		add("entity.celestium.celestial_bolt", "Celestial Bolt");
		addItem(ModItems.MINI_WARDEN_SPAWN_EGG, "Mini Warden Spawn Egg");
		addItem(ModItems.DEMON_SWORDSMAN_SPAWN_EGG, "Demon Swordsman Spawn Egg");

		// --- Celestial backpacks ---
		addItem(ModItems.BACKPACK, "Large Celestial Backpack");
		addItem(ModItems.BACKPACK_HUGE, "Huge Celestial Backpack");
		addItem(ModItems.BACKPACK_MEDIUM, "Medium Celestial Backpack");
		addItem(ModItems.BACKPACK_SMALL, "Small Celestial Backpack");

		// --- Travel gear ---
		addItem(ModItems.INVISIBILITY_CLOAK, "Cloak of Invisibility");
		addItem(ModItems.TANDEM_SADDLE, "Tandem Saddle");
		addItem(ModItems.CELESTIAL_BOW, "Celestial Bow");

		// --- Spells ---
		add("spell.celestium.celestial_strike", "Celestial Strike");
		add("spell.celestium.celestial_bolt", "Celestial Bolt");
		add("message.celestium.spell.no_target", "No target in sight.");
		add("message.celestium.spell.no_mana", "Your celestial energy is depleted.");
		add("message.celestium.spell.cooling_down", "That spell is still recharging for %s seconds.");
		add("message.celestium.spell.wrong_faction", "That spell is reserved for the %s faction.");
	}

	/**
	 * Les vingt-deux progres du mod.
	 *
	 * <p>Regroupes ici plutot que dispersés dans la liste des items : ils forment un texte
	 * suivi, et les relire d'affilée est le seul moyen de vérifier que l'arbre raconte bien
	 * une progression.
	 */
	private void advancements() {

		advancement("root", "Celestial Univers", "Mine your first Celestium fragment.");
		advancement("celestium_ingot", "Raw Material", "Smelt fragments into an ingot.");
		advancement("celestium_tools", "Equipped", "Forge a Celestium tool.");
		advancement("celestium_armour", "Clad in Stars", "Wear the Celestium chestplate.");
		advancement("backpack", "Pockets Full", "Craft a celestial backpack.");
		advancement("huge_backpack", "Removal Man", "Craft the huge celestial backpack and its twenty rows.");
		advancement("lucky_block", "Push Your Luck", "Get hold of a lucky block.");

		advancement("corrupted_eye", "An Eye Opens", "Craft a corrupted eye. You will need twelve.");
		advancement("corrupted_frame", "Threshold Stone", "Obtain a corrupted frame.");
		advancement("enter_corrupted", "Where Two Worlds Collide", "Cross into the corrupted lands.");
		advancement("corrupted_ore", "Tainted Matter", "Mine corrupted Celestium.");
		advancement("corrupted_tools", "Something to Dig With", "Forge a corrupted Celestium tool.");
		advancement("corrupted_armour", "Travelling Clothes", "Wear the corrupted Celestium chestplate.");
		advancement("corrupted_book", "Grimoire", "Steep a book in corruption.");
		advancement("enchanting_table", "The Other Table", "Set up the corrupted enchanting table.");

		advancement("demon_frame", "The Demon's Frame", "Obtain a block of corrupted Celestium.");
		advancement("enter_demon", "The Demon Wastes", "Cross the demon portal.");
		advancement("demonium", "The Metal Below", "Mine Demonium.");
		advancement("demonium_armour", "Demon's Armour", "Wear the Demonium chestplate.");
		advancement("demon_wood", "Wood That Won't Burn Alone", "Fell a tree of the Demon Wastes.");
		advancement("summoning_altar", "Call Him Back", "Set up a summoning altar.");
		advancement("demon_heart", "Heart Torn Out", "Slay the demon swordsman and take his heart.");

		advancement("dark_matter", "What the Sky Is Made Of", "Mine dark matter.");
		advancement("gravity_well", "All Things Come", "Set up a gravity well.");
		advancement("dark_matter_armour", "Anchored", "Wear the dark matter chestplate.");

		advancement("celestial_dragon", "What Slept on the Gold",
				"Slay a celestial dragon and take its hoard.");
		advancement("invisibility_cloak", "Nobody Here",
				"Craft the cloak of invisibility.");
		advancement("tandem_saddle", "Hop On Behind",
				"Craft a tandem saddle.");

		// --- What the Overworld holds ---
		advancement("celestium_block", "Stockpile", "Pack ingots into a block of Celestium.");
		advancement("celestial_dust", "Stardust", "Craft celestial dust.");
		advancement("dust_trip", "The Sky, a Little Too Close", "Drink a flask of celestial dust.");
		advancement("cemetery", "Turned Earth", "Find the cemetery.");

		// --- Lucky blocks ---
		advancement("corrupted_lucky_block", "Luck Turns", "Get hold of a corrupted lucky block.");
		advancement("demon_lucky_block", "Tempting Him", "Get hold of a demon lucky block.");

		// --- The bow ---
		advancement("celestial_bow", "Draw the String", "Forge the celestial bow.");
		advancement("volley", "Three at Once", "Get Celestial Volley.");
		advancement("piercing_shot", "In a Row", "Get Piercing Shot.");
		advancement("seeker", "A Steadier Hand", "Get Seeker.");
		advancement("collapse", "All Things to the Arrow", "Get Collapse.");
		advancement("complete_bow", "The Finished Bow", "Gather all four bow enchantments on one bow.");

		// --- Structures ---
		advancement("sanctum", "Under the Stone", "Find a corrupted sanctum.");
		advancement("celestial_hoard", "All That Gold", "Find a celestial dragon's hoard.");
		advancement("demon_village", "They Live Here", "Find a village of the Demon Wastes.");
		advancement("parasite", "Cleanup", "Slay a parasite.");

		// --- What the corrupted table grants ---
		advancement("corrupted_enchant", "The Table Speaks", "Enchant an item at the corrupted table.");
		advancement("timber", "The Whole Tree", "Get Timber.");
		advancement("vein_miner", "To the End of the Vein", "Get Vein Miner.");
		advancement("excavation", "Dig Wide", "Get Excavation.");
		advancement("harvest", "In One Sweep", "Get Harvest.");
		advancement("smelting", "Already Molten", "Get Smelting.");
		advancement("magnetism", "Nothing Left Behind", "Get Magnetism.");
		advancement("thunderstrike", "The Sky Joins In", "Get Thunderstrike.");
		advancement("tamer", "Settle Down", "Get Tamer.");
		advancement("midas_curse", "Everything He Touches", "Put the Curse of Midas on your pickaxe.");

		// --- The unicorn and the familiars ---
		addItem(ModItems.UNICORN_HORN, "Unicorn Horn");
		addItem(ModItems.UNICORN_HORN_SWORD, "Unicorn Horn Sword");
		addItem(ModItems.UNICORN_HORN_HAT, "Unicorn Horn Hat");
		addItem(ModItems.UNICORN_FOAL_EGG, "Celestial Foal Egg");
		addItem(ModItems.UNICORN_SPAWN_EGG, "Unicorn Spawn Egg");
		addItem(ModItems.FENNEC_SPAWN_EGG, "Fennec Spawn Egg");
		addItem(ModItems.MINI_GUARDIAN_SPAWN_EGG, "Little Guardian Spawn Egg");
		addItem(ModItems.MINI_DEMON_SPAWN_EGG, "Little Demon Spawn Egg");
		add("entity.celestium.unicorn", "Unicorn");
		add("entity.celestium.fennec", "Fennec");
		add("entity.celestium.mini_guardian", "Little Guardian");
		add("entity.celestium.mini_demon", "Little Demon");

		// --- The unicorn and the familiars ---
		advancement("unicorn", "So It Was Real", "Slay a unicorn.");
		advancement("unicorn_horn", "Two Chances in a Hundred", "Recover a unicorn's horn.");
		advancement("horn_sword", "Mounted as a Blade", "Forge the unicorn horn sword.");
		advancement("horn_hat", "On Your Head", "Wear the unicorn horn hat.");
		advancement("unicorn_foal", "What Is Left of Her", "Recover a celestial foal egg.");
		advancement("fennec", "A Reason to Cross the Desert", "Tame a fennec.");
		advancement("mini_guardian", "The Only One You Can Approach", "Tame a little guardian.");
		advancement("mini_demon", "Brought Back from Down There", "Tame a little demon.");
		advancement("every_familiar", "Good Company",
				"Tame the fennec, the little guardian and the little demon.");
	}

	/** Un progrès : son titre et sa description, sous les deux clés attendues. */
	private void advancement(String name, String title, String description) {
		add("advancements.celestium." + name + ".title", title);
		add("advancements.celestium." + name + ".descr", description);
	}
}
