package net.celestium.init;

import net.celestium.CelestiumMod;
import com.mojang.serialization.Codec;
import net.celestium.feature.enchant.ExcavationEnchantment;
import net.celestium.feature.enchant.MagnetismEnchantment;
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
 * <p>Aucun des deux n'apparait sur une table d'enchantement ordinaire, ni dans un coffre, ni chez
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

	private ModEnchantments() {
	}
}
