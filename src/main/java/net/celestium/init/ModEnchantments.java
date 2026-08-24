package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.feature.enchant.ExcavationEnchantment;
import net.celestium.feature.enchant.TimberEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
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

	private ModEnchantments() {
	}
}
