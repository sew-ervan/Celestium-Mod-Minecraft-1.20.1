package net.celestium.server.command;

import net.celestium.CelestiumMod;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Enregistrement des commandes du mod.
 *
 * <p>MCreator generait une classe par commande, chacune avec sa propre annotation d'ecoute
 * d'evenement. Un point d'entree unique rend visible d'un coup d'oeil tout ce que le mod ajoute.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class ModCommands {

	private ModCommands() {
	}

	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		HomeCommand.register(event.getDispatcher());
		SpawnCommand.register(event.getDispatcher());
		RtpCommand.register(event.getDispatcher());
		AnnounceCommand.register(event.getDispatcher());
		MorphCommand.register(event.getDispatcher());
		CelestiumAdminCommand.register(event.getDispatcher());
	}
}
