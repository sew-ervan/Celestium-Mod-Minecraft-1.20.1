package net.celestium;

import com.mojang.logging.LogUtils;
import net.celestium.core.material.CelestiumTier;
import net.celestium.core.network.ModNetwork;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModCreativeTabs;
import net.celestium.init.ModEntities;
import net.celestium.init.ModSpells;
import net.celestium.init.ModItems;
import net.celestium.init.ModMenus;
import net.celestium.init.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

		// ModBlocks est touche en premier : son initialisation inscrit aussi les items de bloc
		// dans ModItems, qui doit donc etre complet avant que le bus ne collecte les entrees.
		ModBlocks.BLOCKS.register(modBus);
		ModItems.ITEMS.register(modBus);
		ModCreativeTabs.TABS.register(modBus);
		ModSounds.SOUNDS.register(modBus);
		ModEntities.ENTITIES.register(modBus);
		ModMenus.MENUS.register(modBus);

		modBus.addListener(this::onCommonSetup);
	}

	private void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			CelestiumTier.registerSorting();
			ModSpells.init();
			ModNetwork.register();
			ModBlocks.BOIS_DU_DEMON.registerFlammability();
		});
	}

	/** Raccourci pour construire un identifiant dans l'espace de noms du mod. */
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
