package net.celestium.datagen;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.WoodSet;
import net.celestium.init.ModBlocks;
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

		// --- Advancements ---
		add("advancements.premier_celestium.title", "First Celestium");
		add("advancements.premier_celestium.descr", "You obtained your first Celestium fragment.");

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

		// --- Demonium ---
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
		addItem(ModItems.BACKPACK, "Celestial Backpack");
		addItem(ModItems.BACKPACK_MEDIUM, "Medium Celestial Backpack");
		addItem(ModItems.BACKPACK_SMALL, "Small Celestial Backpack");

		// --- Spells ---
		add("spell.celestium.celestial_strike", "Celestial Strike");
		add("spell.celestium.celestial_bolt", "Celestial Bolt");
		add("message.celestium.spell.no_target", "No target in sight.");
		add("message.celestium.spell.no_mana", "Your celestial energy is depleted.");
		add("message.celestium.spell.cooling_down", "That spell is still recharging for %s seconds.");
		add("message.celestium.spell.wrong_faction", "That spell is reserved for the %s faction.");
	}
}
