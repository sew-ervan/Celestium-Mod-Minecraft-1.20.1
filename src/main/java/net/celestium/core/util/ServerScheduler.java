package net.celestium.core.util;

import net.celestium.CelestiumMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Execute une action apres un delai exprime en ticks serveur.
 *
 * <p>Remplace le {@code queueServerWork} genere par MCreator, qui souffrait de trois defauts :
 * une tache planifiee avec un delai nul ou negatif n'etait jamais executee (le test portait sur
 * {@code == 0} et non {@code <= 0}), la file etait mutee pendant son propre parcours, et rien
 * n'etait purge a l'arret du serveur — les taches d'un monde survivaient au monde suivant.
 */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID)
public final class ServerScheduler {

	private static final List<Task> RUNNING = new ArrayList<>();
	private static final List<Task> INCOMING = new ArrayList<>();

	private ServerScheduler() {
	}

	/**
	 * Planifie une action sur le thread serveur.
	 *
	 * @param delayTicks delai en ticks ; toute valeur inferieure a 1 est ramenee au tick suivant
	 * @param action     l'action a executer
	 */
	public static void schedule(int delayTicks, Runnable action) {
		synchronized (INCOMING) {
			INCOMING.add(new Task(action, Math.max(1, delayTicks)));
		}
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		synchronized (INCOMING) {
			if (!INCOMING.isEmpty()) {
				RUNNING.addAll(INCOMING);
				INCOMING.clear();
			}
		}

		if (RUNNING.isEmpty()) {
			return;
		}

		// Les actions echues sont collectees avant d'etre lancees : une action qui replanifie
		// une tache ne doit pas perturber le parcours en cours.
		List<Runnable> due = new ArrayList<>();
		Iterator<Task> it = RUNNING.iterator();
		while (it.hasNext()) {
			Task task = it.next();
			if (--task.ticksLeft <= 0) {
				due.add(task.action);
				it.remove();
			}
		}

		for (Runnable action : due) {
			try {
				action.run();
			} catch (Exception e) {
				CelestiumMod.LOGGER.error("Echec d'une tache planifiee", e);
			}
		}
	}

	@SubscribeEvent
	public static void onServerStopped(ServerStoppedEvent event) {
		synchronized (INCOMING) {
			INCOMING.clear();
		}
		RUNNING.clear();
	}

	private static final class Task {
		private final Runnable action;
		private int ticksLeft;

		private Task(Runnable action, int ticksLeft) {
			this.action = action;
			this.ticksLeft = ticksLeft;
		}
	}
}
