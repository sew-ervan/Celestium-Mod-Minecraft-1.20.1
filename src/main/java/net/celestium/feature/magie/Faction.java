package net.celestium.feature.magie;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

/**
 * Camp d'un joueur dans le conflit celeste.
 *
 * <p>Le mod d'origine stockait cette appartenance dans un {@code double} nomme
 * {@code magie_equipe}, valant -1, 0 ou 1, teste un peu partout par des comparaisons du genre
 * {@code magie_equipe < 1}. Rien n'empechait d'y ecrire 0,5 ou 42, et le sens de chaque valeur
 * n'existait que dans la tete de celui qui l'avait ecrit.
 */
public enum Faction implements StringRepresentable {

	DEMON("demon", -1),
	NEUTRE("neutre", 0),
	CELESTE("celeste", 1);

	private final String name;
	private final int legacyValue;

	Faction(String name, int legacyValue) {
		this.name = name;
		this.legacyValue = legacyValue;
	}

	/** Camp par defaut d'un joueur qui n'a jamais choisi. */
	public static Faction getDefault() {
		return NEUTRE;
	}

	/** Retrouve un camp par son nom, ou {@link #NEUTRE} si le nom est inconnu. */
	public static Faction byName(@Nullable String name) {
		if (name != null) {
			for (Faction faction : values()) {
				if (faction.name.equals(name)) {
					return faction;
				}
			}
		}
		return getDefault();
	}

	/** Traduit l'ancienne valeur numerique, pour lire les sauvegardes du mod d'origine. */
	public static Faction fromLegacyValue(double value) {
		int rounded = (int) Math.round(value);
		for (Faction faction : values()) {
			if (faction.legacyValue == rounded) {
				return faction;
			}
		}
		return getDefault();
	}

	/**
	 * Indique si ce camp est une cible legitime pour la magie celeste.
	 *
	 * <p>Remplace les tests {@code magie_equipe < 1} disperses dans les procedures.
	 */
	public boolean isTargetedByCelestialMagic() {
		return this != CELESTE;
	}

	/** Deux camps s'opposent des lors qu'ils different et qu'aucun n'est neutre. */
	public boolean isHostileTo(Faction other) {
		return this != NEUTRE && other != NEUTRE && this != other;
	}

	public Component getDisplayName() {
		return Component.translatable("faction.celestium." + this.name);
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
