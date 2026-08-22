package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.core.material.CelestiumTier;
import net.celestium.feature.backpack.BackpackItem;
import net.celestium.feature.backpack.BackpackTier;
import net.celestium.feature.celestium.CelestiumArmorItem;
import net.celestium.feature.celestium.CelestiumIngotItem;
import net.celestium.feature.celestium.CelestiumSwordItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registre des items.
 *
 * <p>Les identifiants sont en anglais et en snake_case, comme le veut la convention Minecraft ;
 * les noms affiches en jeu sont en francais et vivent dans les fichiers de langue. Les identifiants
 * generes par MCreator melangeaient les deux et comportaient des fautes ({@code fragementceleste},
 * {@code piocheen_celestium}), figees dans les sauvegardes et les recettes.
 */
public class ModItems {

	public static final DeferredRegister<Item> ITEMS =
			DeferredRegister.create(ForgeRegistries.ITEMS, CelestiumMod.MOD_ID);

	// --- Matieres premieres ---

	public static final RegistryObject<Item> CELESTIUM_FRAGMENT = ITEMS.register("celestium_fragment",
			() -> new Item(new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CELESTIUM_INGOT = ITEMS.register("celestium_ingot",
			CelestiumIngotItem::new);

	public static final RegistryObject<Item> CELESTIUM_STICK = ITEMS.register("celestium_stick",
			() -> new Item(new Item.Properties()));

	// --- Outils ---
	// Les valeurs d'attaque et de vitesse suivent les ecarts vanilla entre types d'outils ;
	// ce qui distingue le Celestium des autres materiaux est porte par CelestiumTier.

	public static final RegistryObject<Item> CELESTIUM_SWORD = ITEMS.register("celestium_sword",
			CelestiumSwordItem::new);

	public static final RegistryObject<Item> CELESTIUM_PICKAXE = ITEMS.register("celestium_pickaxe",
			() -> new PickaxeItem(CelestiumTier.CELESTIUM, 1, -2.8F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CELESTIUM_AXE = ITEMS.register("celestium_axe",
			() -> new AxeItem(CelestiumTier.CELESTIUM, 5.0F, -3.0F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CELESTIUM_SHOVEL = ITEMS.register("celestium_shovel",
			() -> new ShovelItem(CelestiumTier.CELESTIUM, 1.5F, -3.0F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CELESTIUM_HOE = ITEMS.register("celestium_hoe",
			() -> new HoeItem(CelestiumTier.CELESTIUM, -4, 0.0F, new Item.Properties().fireResistant()));

	// --- Armure ---

	public static final RegistryObject<Item> CELESTIUM_HELMET = ITEMS.register("celestium_helmet",
			() -> new CelestiumArmorItem(ArmorItem.Type.HELMET));

	public static final RegistryObject<Item> CELESTIUM_CHESTPLATE = ITEMS.register("celestium_chestplate",
			() -> new CelestiumArmorItem(ArmorItem.Type.CHESTPLATE));

	public static final RegistryObject<Item> CELESTIUM_LEGGINGS = ITEMS.register("celestium_leggings",
			() -> new CelestiumArmorItem(ArmorItem.Type.LEGGINGS));

	public static final RegistryObject<Item> CELESTIUM_BOOTS = ITEMS.register("celestium_boots",
			() -> new CelestiumArmorItem(ArmorItem.Type.BOOTS));

	// --- Sacs celestes ---

	public static final RegistryObject<Item> BACKPACK_SMALL = ITEMS.register("backpack_small",
			() -> new BackpackItem(BackpackTier.SMALL));

	public static final RegistryObject<Item> BACKPACK_MEDIUM = ITEMS.register("backpack_medium",
			() -> new BackpackItem(BackpackTier.MEDIUM));

	public static final RegistryObject<Item> BACKPACK = ITEMS.register("backpack",
			() -> new BackpackItem(BackpackTier.LARGE));

	// --- Oeufs d'apparition ---

	public static final RegistryObject<Item> MINI_WARDEN_SPAWN_EGG = ITEMS.register("mini_warden_spawn_egg",
			() -> new ForgeSpawnEggItem(ModEntities.MINI_WARDEN, 0x0F6C68, 0x39D6E0, new Item.Properties()));

	public static final RegistryObject<Item> DEMON_SWORDSMAN_SPAWN_EGG = ITEMS.register("demon_swordsman_spawn_egg",
			() -> new ForgeSpawnEggItem(ModEntities.DEMON_SWORDSMAN, 0x2B0A0A, 0xB01818, new Item.Properties()));

	private ModItems() {
	}
}
