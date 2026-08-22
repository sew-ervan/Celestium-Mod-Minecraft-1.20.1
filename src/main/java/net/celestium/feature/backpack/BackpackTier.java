package net.celestium.feature.backpack;

import net.minecraft.util.StringRepresentable;

/**
 * Taille d'un sac celeste.
 *
 * <p>Le mod d'origine consacrait a chaque taille un menu complet, ou les dix-huit emplacements
 * etaient enumeres un par un avec leurs coordonnees en dur.
 */
public enum BackpackTier implements StringRepresentable {

	SMALL("small", 3, 3),
	MEDIUM("medium", 9, 9),
	LARGE("large", 18, 9);

	private final String name;
	private final int size;
	private final int columns;

	BackpackTier(String name, int size, int columns) {
		this.name = name;
		this.size = size;
		this.columns = columns;
	}

	public static BackpackTier byOrdinal(int ordinal) {
		BackpackTier[] values = values();
		return ordinal >= 0 && ordinal < values.length ? values[ordinal] : SMALL;
	}

	public int size() {
		return this.size;
	}

	public int columns() {
		return this.columns;
	}

	public int rows() {
		return (this.size + this.columns - 1) / this.columns;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
