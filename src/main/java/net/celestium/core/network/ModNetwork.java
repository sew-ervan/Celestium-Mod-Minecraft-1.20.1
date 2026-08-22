package net.celestium.core.network;

import net.celestium.CelestiumMod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Canal reseau du mod.
 *
 * <p>Les paquets sont enregistres depuis {@link #register()}, appele au chargement. Chaque paquet
 * possede un identifiant stable : ne jamais reordonner les appels existants, seulement ajouter a
 * la suite, sous peine de desynchroniser un client et un serveur de versions differentes.
 */
public final class ModNetwork {

	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			CelestiumMod.id("main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	private static int nextId = 0;

	private ModNetwork() {
	}

	public static void register() {
		// Les paquets seront enregistres ici au fil des lots (donnees joueur, sorts).
	}

	/** Reserve le prochain identifiant de paquet libre. */
	public static int nextId() {
		return nextId++;
	}
}
