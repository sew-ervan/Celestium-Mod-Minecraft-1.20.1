package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.core.material.ModArmorMaterials;
import net.celestium.core.material.ModTiers;
import net.celestium.feature.backpack.BackpackItem;
import net.celestium.feature.backpack.BackpackTier;
import net.celestium.feature.celestium.CelestialDustItem;
import net.celestium.feature.celestium.CelestiumIngotItem;
import net.celestium.feature.celestium.CelestiumSwordItem;
import net.celestium.feature.celestium.ModArmorItem;
import net.celestium.feature.cloak.InvisibilityCloakItem;
import net.celestium.feature.mount.TandemSaddleItem;
import net.celestium.feature.portal.CorruptedEyeItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
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
 *
 * <p>Les deux materiaux suivent la meme progression — fragment, lingot, bloc, outils, armure — mais
 * ne se valent pas : voir {@link ModTiers} et {@link ModArmorMaterials}.
 */
public class ModItems {

	public static final DeferredRegister<Item> ITEMS =
			DeferredRegister.create(ForgeRegistries.ITEMS, CelestiumMod.MOD_ID);

	// --- Celestium : matieres premieres ---

	public static final RegistryObject<Item> CELESTIUM_FRAGMENT = ITEMS.register("celestium_fragment",
			() -> new Item(new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CELESTIUM_INGOT = ITEMS.register("celestium_ingot",
			CelestiumIngotItem::new);

	public static final RegistryObject<Item> CELESTIUM_STICK = ITEMS.register("celestium_stick",
			() -> new Item(new Item.Properties()));

	/**
	 * La poussiere celeste : un effet au hasard, pour une duree au hasard.
	 *
	 * <p>Rien d'instantane dans ses deux listes — voir {@link CelestialDustItem}.
	 */
	public static final RegistryObject<Item> CELESTIAL_DUST = ITEMS.register("celestial_dust",
			CelestialDustItem::new);

	// --- Celestium : outils ---
	// Les valeurs d'attaque et de vitesse suivent les ecarts vanilla entre types d'outils ;
	// ce qui distingue un materiau d'un autre est porte par ModTiers.

	public static final RegistryObject<Item> CELESTIUM_SWORD = ITEMS.register("celestium_sword",
			CelestiumSwordItem::new);

	public static final RegistryObject<Item> CELESTIUM_PICKAXE = ITEMS.register("celestium_pickaxe",
			() -> new PickaxeItem(ModTiers.CELESTIUM, 1, -2.8F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CELESTIUM_AXE = ITEMS.register("celestium_axe",
			() -> new AxeItem(ModTiers.CELESTIUM, 5.0F, -3.0F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CELESTIUM_SHOVEL = ITEMS.register("celestium_shovel",
			() -> new ShovelItem(ModTiers.CELESTIUM, 1.5F, -3.0F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CELESTIUM_HOE = ITEMS.register("celestium_hoe",
			() -> new HoeItem(ModTiers.CELESTIUM, -4, 0.0F, new Item.Properties().fireResistant()));

	// --- Celestium : armure ---

	public static final RegistryObject<Item> CELESTIUM_HELMET = ITEMS.register("celestium_helmet",
			() -> new ModArmorItem(ModArmorMaterials.CELESTIUM, ArmorItem.Type.HELMET));

	public static final RegistryObject<Item> CELESTIUM_CHESTPLATE = ITEMS.register("celestium_chestplate",
			() -> new ModArmorItem(ModArmorMaterials.CELESTIUM, ArmorItem.Type.CHESTPLATE));

	public static final RegistryObject<Item> CELESTIUM_LEGGINGS = ITEMS.register("celestium_leggings",
			() -> new ModArmorItem(ModArmorMaterials.CELESTIUM, ArmorItem.Type.LEGGINGS));

	public static final RegistryObject<Item> CELESTIUM_BOOTS = ITEMS.register("celestium_boots",
			() -> new ModArmorItem(ModArmorMaterials.CELESTIUM, ArmorItem.Type.BOOTS));

	// --- Celestium corrompu : de quoi ouvrir le portail et survivre derriere ---

	public static final RegistryObject<Item> CORRUPTED_CELESTIUM_FRAGMENT =
			ITEMS.register("corrupted_celestium_fragment",
					() -> new Item(new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CORRUPTED_CELESTIUM_INGOT =
			ITEMS.register("corrupted_celestium_ingot", () -> new Item(new Item.Properties().fireResistant()));

	// --- Celestium corrompu : outils ---
	//
	// Les seuls outils, avec ceux du demon, qui mordent la pierre des Terres du demon. Ils se
	// montent sur un baton ordinaire : cette panoplie doit rester fabricable sans rien rapporter
	// de la dimension qu'elle sert a ouvrir.

	public static final RegistryObject<Item> CORRUPTED_CELESTIUM_PICKAXE =
			ITEMS.register("corrupted_celestium_pickaxe",
					() -> new PickaxeItem(ModTiers.CORRUPTED_CELESTIUM, 1, -2.8F,
							new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CORRUPTED_CELESTIUM_SWORD =
			ITEMS.register("corrupted_celestium_sword",
					() -> new SwordItem(ModTiers.CORRUPTED_CELESTIUM, 3, -2.4F,
							new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CORRUPTED_CELESTIUM_AXE =
			ITEMS.register("corrupted_celestium_axe",
					() -> new AxeItem(ModTiers.CORRUPTED_CELESTIUM, 5.5F, -3.1F,
							new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CORRUPTED_CELESTIUM_SHOVEL =
			ITEMS.register("corrupted_celestium_shovel",
					() -> new ShovelItem(ModTiers.CORRUPTED_CELESTIUM, 1.5F, -3.0F,
							new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> CORRUPTED_CELESTIUM_HOE =
			ITEMS.register("corrupted_celestium_hoe",
					() -> new HoeItem(ModTiers.CORRUPTED_CELESTIUM, -3, 0.0F,
							new Item.Properties().fireResistant()));

	// --- Celestium corrompu : armure ---

	public static final RegistryObject<Item> CORRUPTED_CELESTIUM_HELMET =
			ITEMS.register("corrupted_celestium_helmet",
					() -> new ModArmorItem(ModArmorMaterials.CORRUPTED_CELESTIUM, ArmorItem.Type.HELMET));

	public static final RegistryObject<Item> CORRUPTED_CELESTIUM_CHESTPLATE =
			ITEMS.register("corrupted_celestium_chestplate",
					() -> new ModArmorItem(ModArmorMaterials.CORRUPTED_CELESTIUM, ArmorItem.Type.CHESTPLATE));

	public static final RegistryObject<Item> CORRUPTED_CELESTIUM_LEGGINGS =
			ITEMS.register("corrupted_celestium_leggings",
					() -> new ModArmorItem(ModArmorMaterials.CORRUPTED_CELESTIUM, ArmorItem.Type.LEGGINGS));

	public static final RegistryObject<Item> CORRUPTED_CELESTIUM_BOOTS =
			ITEMS.register("corrupted_celestium_boots",
					() -> new ModArmorItem(ModArmorMaterials.CORRUPTED_CELESTIUM, ArmorItem.Type.BOOTS));

	/**
	 * Le livre corrompu, cle de la table d'enchantement corrompue.
	 *
	 * <p>Un livre ordinaire trempe dans du Celestium corrompu — donc rapporte des terres corrompues,
	 * comme tout ce qui touche a cette matiere.
	 */
	public static final RegistryObject<Item> CORRUPTED_BOOK = ITEMS.register("corrupted_book",
			() -> new Item(new Item.Properties().fireResistant().stacksTo(16).rarity(Rarity.RARE)));

	/**
	 * L'oeil qui garnit un cadre corrompu.
	 *
	 * <p>Douze sont necessaires. C'est le prix d'entree des terres corrompues, et par ricochet
	 * celui des Terres du demon, puisqu'on ne trouve qu'ici de quoi en batir le cadre.
	 */
	public static final RegistryObject<Item> CORRUPTED_EYE = ITEMS.register("corrupted_eye",
			() -> new CorruptedEyeItem(new Item.Properties().fireResistant().rarity(Rarity.RARE)));

	// --- Le trophee du demon ---

	/**
	 * Ce que le demon epeiste laisse en mourant.
	 *
	 * <p>Un seul par demon abattu. Les villageois corrompus le reverent : c'est la seule chose
	 * contre laquelle ils cedent leurs reserves.
	 */
	public static final RegistryObject<Item> DEMON_HEART = ITEMS.register("demon_heart",
			() -> new Item(new Item.Properties().fireResistant().stacksTo(16).rarity(Rarity.EPIC)));

	// --- Demonium : matieres premieres ---

	public static final RegistryObject<Item> DEMONIUM_FRAGMENT = ITEMS.register("demonium_fragment",
			() -> new Item(new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> DEMONIUM_INGOT = ITEMS.register("demonium_ingot",
			() -> new Item(new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> DEMONIUM_STICK = ITEMS.register("demonium_stick",
			() -> new Item(new Item.Properties().fireResistant()));

	// --- Demonium : outils ---

	public static final RegistryObject<Item> DEMONIUM_SWORD = ITEMS.register("demonium_sword",
			() -> new SwordItem(ModTiers.DEMONIUM, 4, -2.4F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> DEMONIUM_PICKAXE = ITEMS.register("demonium_pickaxe",
			() -> new PickaxeItem(ModTiers.DEMONIUM, 1, -2.8F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> DEMONIUM_AXE = ITEMS.register("demonium_axe",
			() -> new AxeItem(ModTiers.DEMONIUM, 6.0F, -3.0F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> DEMONIUM_SHOVEL = ITEMS.register("demonium_shovel",
			() -> new ShovelItem(ModTiers.DEMONIUM, 1.5F, -3.0F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> DEMONIUM_HOE = ITEMS.register("demonium_hoe",
			() -> new HoeItem(ModTiers.DEMONIUM, -4, 0.0F, new Item.Properties().fireResistant()));

	// --- Demonium : armure ---

	public static final RegistryObject<Item> DEMONIUM_HELMET = ITEMS.register("demonium_helmet",
			() -> new ModArmorItem(ModArmorMaterials.DEMONIUM, ArmorItem.Type.HELMET));

	public static final RegistryObject<Item> DEMONIUM_CHESTPLATE = ITEMS.register("demonium_chestplate",
			() -> new ModArmorItem(ModArmorMaterials.DEMONIUM, ArmorItem.Type.CHESTPLATE));

	public static final RegistryObject<Item> DEMONIUM_LEGGINGS = ITEMS.register("demonium_leggings",
			() -> new ModArmorItem(ModArmorMaterials.DEMONIUM, ArmorItem.Type.LEGGINGS));

	public static final RegistryObject<Item> DEMONIUM_BOOTS = ITEMS.register("demonium_boots",
			() -> new ModArmorItem(ModArmorMaterials.DEMONIUM, ArmorItem.Type.BOOTS));

	// --- Matiere noire ---
	//
	// Elle ne brille pas, ne se voit qu'a ce qu'elle attire, et compose l'essentiel de l'univers
	// sans qu'on l'ait jamais tenue. C'est de la masse pure : ses outils durent, son armure ancre,
	// et son bloc le plus utile ne fait rien d'autre que tirer a lui ce qui tombe.

	public static final RegistryObject<Item> DARK_MATTER = ITEMS.register("dark_matter",
			() -> new Item(new Item.Properties().fireResistant().rarity(Rarity.RARE)));

	public static final RegistryObject<Item> DARK_MATTER_PICKAXE = ITEMS.register("dark_matter_pickaxe",
			() -> new PickaxeItem(ModTiers.DARK_MATTER, 1, -2.8F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> DARK_MATTER_SWORD = ITEMS.register("dark_matter_sword",
			() -> new SwordItem(ModTiers.DARK_MATTER, 3, -2.4F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> DARK_MATTER_AXE = ITEMS.register("dark_matter_axe",
			() -> new AxeItem(ModTiers.DARK_MATTER, 5.5F, -3.1F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> DARK_MATTER_SHOVEL = ITEMS.register("dark_matter_shovel",
			() -> new ShovelItem(ModTiers.DARK_MATTER, 1.5F, -3.0F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> DARK_MATTER_HOE = ITEMS.register("dark_matter_hoe",
			() -> new HoeItem(ModTiers.DARK_MATTER, -3, 0.0F, new Item.Properties().fireResistant()));

	public static final RegistryObject<Item> DARK_MATTER_HELMET = ITEMS.register("dark_matter_helmet",
			() -> new ModArmorItem(ModArmorMaterials.DARK_MATTER, ArmorItem.Type.HELMET));

	public static final RegistryObject<Item> DARK_MATTER_CHESTPLATE = ITEMS.register("dark_matter_chestplate",
			() -> new ModArmorItem(ModArmorMaterials.DARK_MATTER, ArmorItem.Type.CHESTPLATE));

	public static final RegistryObject<Item> DARK_MATTER_LEGGINGS = ITEMS.register("dark_matter_leggings",
			() -> new ModArmorItem(ModArmorMaterials.DARK_MATTER, ArmorItem.Type.LEGGINGS));

	public static final RegistryObject<Item> DARK_MATTER_BOOTS = ITEMS.register("dark_matter_boots",
			() -> new ModArmorItem(ModArmorMaterials.DARK_MATTER, ArmorItem.Type.BOOTS));

	// --- Sacs celestes ---

	public static final RegistryObject<Item> BACKPACK_SMALL = ITEMS.register("backpack_small",
			() -> new BackpackItem(BackpackTier.SMALL));

	public static final RegistryObject<Item> BACKPACK_MEDIUM = ITEMS.register("backpack_medium",
			() -> new BackpackItem(BackpackTier.MEDIUM));

	public static final RegistryObject<Item> BACKPACK = ITEMS.register("backpack",
			() -> new BackpackItem(BackpackTier.LARGE));

	/** Vingt rangees : l'interface n'en montre que six et defile pour le reste. */
	public static final RegistryObject<Item> BACKPACK_HUGE = ITEMS.register("backpack_huge",
			() -> new BackpackItem(BackpackTier.HUGE));

	// --- Equipement de voyage ---

	/**
	 * La cape d'invisibilite : on disparait, mais l'emplacement du plastron est pris.
	 *
	 * <p>Volontairement pas un {@code ArmorItem} — voir {@link InvisibilityCloakItem}.
	 */
	public static final RegistryObject<Item> INVISIBILITY_CLOAK = ITEMS.register("invisibility_cloak",
			InvisibilityCloakItem::new);

	/** La selle deux places, a poser sur une monture deja sellee. */
	public static final RegistryObject<Item> TANDEM_SADDLE = ITEMS.register("tandem_saddle",
			TandemSaddleItem::new);

	// --- Oeufs d'apparition ---

	public static final RegistryObject<Item> MINI_WARDEN_SPAWN_EGG = ITEMS.register("mini_warden_spawn_egg",
			() -> new ForgeSpawnEggItem(ModEntities.MINI_WARDEN, 0x0F6C68, 0x39D6E0, new Item.Properties()));

	public static final RegistryObject<Item> CELESTIAL_DRAGON_SPAWN_EGG =
			ITEMS.register("celestial_dragon_spawn_egg",
					() -> new ForgeSpawnEggItem(ModEntities.CELESTIAL_DRAGON, 0x1B2A5E, 0xE6ECFF,
							new Item.Properties()));

	public static final RegistryObject<Item> DEMON_SWORDSMAN_SPAWN_EGG = ITEMS.register("demon_swordsman_spawn_egg",
			() -> new ForgeSpawnEggItem(ModEntities.DEMON_SWORDSMAN, 0x2B0A0A, 0xB01818, new Item.Properties()));

	public static final RegistryObject<Item> PARASITE_SPAWN_EGG = ITEMS.register("parasite_spawn_egg",
			() -> new ForgeSpawnEggItem(ModEntities.PARASITE, 0x3B0D10, 0x8E2B22, new Item.Properties()));

	public static final RegistryObject<Item> CORRUPTED_VILLAGER_SPAWN_EGG =
			ITEMS.register("corrupted_villager_spawn_egg",
					() -> new ForgeSpawnEggItem(ModEntities.CORRUPTED_VILLAGER, 0x4A2C1A, 0x8E2B22,
							new Item.Properties()));

	private ModItems() {
	}
}
