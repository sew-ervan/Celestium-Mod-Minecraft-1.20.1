package net.celestium.client;

import net.celestium.CelestiumMod;
import net.celestium.client.model.SimpleGeoModel;
import net.celestium.client.model.UnicornModel;
import net.celestium.client.renderer.CelestialDragonRenderer;
import net.celestium.client.renderer.CorruptedVillagerRenderer;
import net.celestium.client.renderer.FennecRenderer;
import net.celestium.client.renderer.UnicornRenderer;
import net.celestium.client.renderer.SimpleGeoRenderer;
import net.celestium.feature.mob.ParasiteEntity;
import net.celestium.client.screen.BackpackScreen;
import net.celestium.client.screen.CorruptedEnchantingScreen;
import net.celestium.init.ModEntities;
import net.celestium.init.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Enregistrements cote client.
 *
 * <p>C'est ce qui manquait entierement au mod d'origine : les classes de rendu et d'ecran
 * existaient, mais rien ne les branchait. Concretement, les trois entites animees n'avaient aucun
 * rendu associe et les quatre interfaces ne pouvaient pas s'ouvrir.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD,
		value = Dist.CLIENT)
public final class ClientSetup {

	private ClientSetup() {
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		// L'association menu / ecran manquait entierement au mod d'origine : ses quatre interfaces
		// etaient enregistrees cote serveur mais ne pouvaient pas s'afficher.
		event.enqueueWork(() -> {
			MenuScreens.register(ModMenus.BACKPACK.get(), BackpackScreen::new);
			MenuScreens.register(ModMenus.CORRUPTED_ENCHANTING.get(), CorruptedEnchantingScreen::new);
		});
	}

	/**
	 * La geometrie de la licorne doit etre declaree avant qu'un rendu la demande.
	 *
	 * <p>C'est le seul modele que le mod ajoute a ceux du jeu de base : les autres creatures
	 * passent par GeckoLib, qui charge les siens depuis des fichiers.
	 */
	@SubscribeEvent
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(UnicornModel.LAYER, UnicornModel::createBodyLayer);
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.MINI_WARDEN.get(),
				context -> new SimpleGeoRenderer<>(context,
						new SimpleGeoModel<>("miniwarden", "miniwarden_texture"), 0.5F));

		event.registerEntityRenderer(ModEntities.DEMON_SWORDSMAN.get(),
				context -> new SimpleGeoRenderer<>(context,
						new SimpleGeoModel<>("demonepeiste", "demonepeiste"), 0.9F));

		// Le parasite reutilise la geometrie du gardien miniature, au tiers de sa taille.
		event.registerEntityRenderer(ModEntities.PARASITE.get(),
				context -> new SimpleGeoRenderer<>(context,
						new SimpleGeoModel<>("miniwarden", "parasite"), 0.2F, ParasiteEntity.SCALE));

		event.registerEntityRenderer(ModEntities.CORRUPTED_VILLAGER.get(), CorruptedVillagerRenderer::new);
		event.registerEntityRenderer(ModEntities.CELESTIAL_DRAGON.get(), CelestialDragonRenderer::new);

		event.registerEntityRenderer(ModEntities.UNICORN.get(), UnicornRenderer::new);
		event.registerEntityRenderer(ModEntities.FENNEC.get(), FennecRenderer::new);

		// Les deux familiers des dimensions reprennent la geometrie de leurs grands freres, comme
		// le parasite reprend celle du gardien miniature : seules la taille et la robe changent.
		event.registerEntityRenderer(ModEntities.MINI_GUARDIAN.get(),
				context -> new SimpleGeoRenderer<>(context,
						new SimpleGeoModel<>("miniwarden", "mini_guardian"), 0.3F, 0.35F));

		event.registerEntityRenderer(ModEntities.MINI_DEMON.get(),
				context -> new SimpleGeoRenderer<>(context,
						new SimpleGeoModel<>("demonepeiste", "mini_demon"), 0.3F, 0.25F));

		event.registerEntityRenderer(ModEntities.CELESTIAL_BOLT.get(),
				context -> new SimpleGeoRenderer<>(context,
						new SimpleGeoModel<>("bdfceleste", "bdfceleste"), 0.0F));
	}
}
