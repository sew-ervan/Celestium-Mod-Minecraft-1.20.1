package net.celestium.client;

import net.celestium.CelestiumMod;
import net.celestium.client.model.SimpleGeoModel;
import net.celestium.client.renderer.SimpleGeoRenderer;
import net.celestium.init.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.MINI_WARDEN.get(),
				context -> new SimpleGeoRenderer<>(context,
						new SimpleGeoModel<>("miniwarden", "miniwarden_texture"), 0.5F));

		event.registerEntityRenderer(ModEntities.DEMON_SWORDSMAN.get(),
				context -> new SimpleGeoRenderer<>(context,
						new SimpleGeoModel<>("demonepeiste", "demonepeiste"), 0.9F));

		event.registerEntityRenderer(ModEntities.CELESTIAL_BOLT.get(),
				context -> new SimpleGeoRenderer<>(context,
						new SimpleGeoModel<>("bdfceleste", "bdfceleste"), 0.0F));
	}
}
