package net.celestium.feature.enchant;

/**
 * Salve celeste : une seule bande, plusieurs fleches.
 *
 * <p>Chaque niveau ajoute une fleche, tiree en meme temps que la premiere et ecartee d'un cran.
 * L'ecart est petit — cinq degres — pour que la salve reste une salve et non un tir de dispersion :
 * a dix blocs elle couvre a peine la largeur d'un joueur, a quarante elle balaie un groupe.
 *
 * <p>Les fleches ajoutees ne se ramassent pas. C'est la seule restriction, et elle est necessaire :
 * sans elle, tirer une fleche pour en recuperer quatre transformerait l'enchantement en machine a
 * fabriquer des fleches.
 */
public class VolleyEnchantment extends BowEnchantment {

	private static final int MAX_LEVEL = 3;

	/** Ecart entre deux fleches voisines, en degres. */
	public static final double SPREAD = 5.0;

	public VolleyEnchantment() {
		super(Rarity.RARE);
	}

	@Override
	public int getMaxLevel() {
		return MAX_LEVEL;
	}

	/** Nombre de fleches ajoutees a celle qu'on tire. */
	public static int extraArrows(int level) {
		return Math.max(0, Math.min(MAX_LEVEL, level));
	}
}
