package net.celestium;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Point d'entree du mod Celestium.
 *
 * <p>Les registres sont centralises dans {@code net.celestium.init} et branches ici sur le bus
 * de mod. Le code metier vit dans {@code net.celestium.feature} et {@code net.celestium.server} ;
 * aucun des deux ne doit dependre de {@code net.celestium.client}.
 */
@Mod(CelestiumMod.MOD_ID)
public class CelestiumMod {

	public static final String MOD_ID = "celestium";
	public static final Logger LOGGER = LogUtils.getLogger();

	public CelestiumMod() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Les registres seront branches ici au lot 2.

		LOGGER.info("Celestium initialise");
	}

	/** Raccourci pour construire un identifiant dans l'espace de noms du mod. */
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
