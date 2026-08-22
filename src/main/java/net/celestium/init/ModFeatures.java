package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.worldgen.CemeteryFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registre des features.
 *
 * <p>Cette classe etait appelee depuis le point d'entree du mod d'origine mais n'existait pas :
 * l'une des raisons pour lesquelles le projet ne compilait pas.
 */
public class ModFeatures {

	public static final DeferredRegister<Feature<?>> FEATURES =
			DeferredRegister.create(ForgeRegistries.FEATURES, CelestiumMod.MOD_ID);

	public static final RegistryObject<Feature<NoneFeatureConfiguration>> CEMETERY =
			FEATURES.register("cemetery", () -> new CemeteryFeature(NoneFeatureConfiguration.CODEC));

	private ModFeatures() {
	}
}
