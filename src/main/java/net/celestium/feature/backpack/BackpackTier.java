package net.celestium.feature.backpack;

import net.minecraft.util.StringRepresentable;

/**
 * Taille d'un sac celeste.
 *
 * <p>Le mod d'origine consacrait a chaque taille un menu complet, ou les dix-huit emplacements
 * etaient enumeres un par un avec leurs coordonnees en dur.
 */
public enum BackpackTier implements StringRepresentable {

	SMALL("small", 2),
	MEDIUM("medium", 5),
	LARGE("large", 10),
	HUGE("huge", 20);

	/** Neuf colonnes pour tous : c'est la largeur d'un coffre, et l'oeil y est habitue. */
	public static final int COLUMNS = 9;

	private final String name;
	private final int rows;

	BackpackTier(String name, int rows) {
		this.name = name;
		this.rows = rows;
	}

	public static BackpackTier byOrdinal(int ordinal) {
		BackpackTier[] values = values();
		return ordinal >= 0 && ordinal < values.length ? values[ordinal] : SMALL;
	}

	public int size() {
		return this.rows * COLUMNS;
	}

	public int columns() {
		return COLUMNS;
	}

	public int rows() {
		return this.rows;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
