package net.celestium.init;

import net.celestium.CelestiumMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registre des sons.
 *
 * <p>Vide pour l'instant : les trois sons du mod d'origine ont ete retires. Deux n'etaient
 * references nulle part, et le troisieme etait un MP3 — Minecraft ne lit que l'OGG, il n'a donc
 * jamais pu etre joue.
 *
 * <p>Pour ajouter un son : deposer le fichier {@code .ogg} dans
 * {@code assets/celestium/sounds/}, l'enregistrer ici, et le declarer dans {@code sounds.json}.
 */
public class ModSounds {

	public static final DeferredRegister<SoundEvent> SOUNDS =
			DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CelestiumMod.MOD_ID);

	private ModSounds() {
	}

	/**
	 * Cree un evenement sonore a portee variable.
	 *
	 * <p>Le constructeur {@code new SoundEvent(...)} utilise par MCreator n'existe plus en 1.20.1.
	 */
	@SuppressWarnings("unused")
	private static SoundEvent variableRange(String name) {
		return SoundEvent.createVariableRangeEvent(CelestiumMod.id(name));
	}
}
