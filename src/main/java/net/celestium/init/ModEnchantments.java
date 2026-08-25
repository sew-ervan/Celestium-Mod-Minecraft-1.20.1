package net.celestium.init;

import net.celestium.CelestiumMod;
import com.mojang.serialization.Codec;
import net.celestium.feature.enchant.CollapseEnchantment;
import net.celestium.feature.enchant.ExcavationEnchantment;
import net.celestium.feature.enchant.PiercingShotEnchantment;
import net.celestium.feature.enchant.SeekerEnchantment;
import net.celestium.feature.enchant.VolleyEnchantment;
import net.celestium.feature.enchant.MagnetismEnchantment;
import net.celestium.feature.enchant.MidasCurseEnchantment;
import net.celestium.feature.enchant.MidasModifier;
import net.celestium.feature.enchant.TamerEnchantment;
import net.celestium.feature.enchant.ThunderstrikeEnchantment;
import net.celestium.feature.enchant.SmeltingEnchantment;
import net.celestium.feature.enchant.SmeltingModifier;
import net.celestium.feature.enchant.HarvestEnchantment;
import net.celestium.feature.enchant.VeinMinerEnchantment;
import net.celestium.feature.enchant.TimberEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Enchantements du mod.
 *
 * <p>Aucun d'eux n'apparait sur une table d'enchantement ordinaire, ni dans un coffre, ni chez
 * un villageois : ils se declarent indecouvrables et non echangeables. La seule facon de les
 * obtenir est la table corrompue, ce qui etait la demande — et ce qui leur donne leur prix.
 */
public final class ModEnchantments {

	public static final DeferredRegister<Enchantment> ENCHANTMENTS =
			DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, CelestiumMod.MOD_ID);

	/** Abattage : la hache emporte l'arbre entier. */
	public static final RegistryObject<Enchantment> TIMBER =
			ENCHANTMENTS.register("timber", TimberEnchantment::new);

	/** Excavation : la pioche et la pelle creusent par carres, de trois de cote a neuf. */
	public static final RegistryObject<Enchantment> EXCAVATION =
			ENCHANTMENTS.register("excavation", ExcavationEnchantment::new);

	/** Filon : la pioche emporte tout le gisement. */
	public static final RegistryObject<Enchantment> VEIN_MINER =
			ENCHANTMENTS.register("vein_miner", VeinMinerEnchantment::new);

	/** Moisson : la houe recolte un carre de cultures mures et les replante. */
	public static final RegistryObject<Enchantment> HARVEST =
			ENCHANTMENTS.register("harvest", HarvestEnchantment::new);

	/** Fonte : ce qu'on extrait sort deja fondu. */
	public static final RegistryObject<Enchantment> SMELTING =
			ENCHANTMENTS.register("smelting", SmeltingEnchantment::new);

	/** Aimant : ce qui tombe va droit dans les poches. */
	public static final RegistryObject<Enchantment> MAGNETISM =
			ENCHANTMENTS.register("magnetism", MagnetismEnchantment::new);

	/** Eclair fulgurant : frapper appelle parfois la foudre sur ce qu'on frappe. */
	public static final RegistryObject<Enchantment> THUNDERSTRIKE =
			ENCHANTMENTS.register("thunderstrike", ThunderstrikeEnchantment::new);

	/** Malediction de Midas : tout ce qui vaut mieux que le fer se change en or. */
	public static final RegistryObject<Enchantment> MIDAS_CURSE =
			ENCHANTMENTS.register("midas_curse", MidasCurseEnchantment::new);

	/** Dompteur : ce qui vous prend pour cible se ravise parfois. */
	public static final RegistryObject<Enchantment> TAMER =
			ENCHANTMENTS.register("tamer", TamerEnchantment::new);

	// --- Les quatre enchantements d'arc ---

	/** Salve celeste : une bande, plusieurs fleches. */
	public static final RegistryObject<Enchantment> VOLLEY =
			ENCHANTMENTS.register("volley", VolleyEnchantment::new);

	/** Transpercement : la fleche traverse et poursuit. */
	public static final RegistryObject<Enchantment> PIERCING_SHOT =
			ENCHANTMENTS.register("piercing_shot", PiercingShotEnchantment::new);

	/** Traqueur : la fleche part corrigee vers ce qu'on visait. */
	public static final RegistryObject<Enchantment> SEEKER =
			ENCHANTMENTS.register("seeker", SeekerEnchantment::new);

	/** Effondrement : la fleche plantee tire a elle ce qui l'entoure. */
	public static final RegistryObject<Enchantment> COLLAPSE =
			ENCHANTMENTS.register("collapse", CollapseEnchantment::new);

	/**
	 * Le modificateur de butin qui met la Fonte en oeuvre.
	 *
	 * <p>Un enchantement qui change ce que rend un bloc ne peut pas se contenter d'exister : il lui
	 * faut ce serialiseur, sans quoi le fichier qui le declare ne serait pas lu.
	 */
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
			DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CelestiumMod.MOD_ID);

	public static final RegistryObject<Codec<? extends IGlobalLootModifier>> SMELTING_MODIFIER =
			LOOT_MODIFIERS.register("smelting", () -> SmeltingModifier.CODEC);

	public static final RegistryObject<Codec<? extends IGlobalLootModifier>> MIDAS_MODIFIER =
			LOOT_MODIFIERS.register("midas", () -> MidasModifier.CODEC);

	private ModEnchantments() {
	}
}
