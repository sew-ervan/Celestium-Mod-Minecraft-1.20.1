package net.celestium.feature.enchant;

/**
 * Traqueur : la fleche part corrigee vers la creature visee.
 *
 * <p>La correction se fait au depart, une fois, et non pendant le vol. Une fleche qui virerait en
 * l'air ne raterait jamais, ce qui reviendrait a supprimer la visee ; celle-ci ne fait que rattraper
 * la main. Elle ne cherche d'ailleurs que dans un cone etroit devant le tireur : viser a cote reste
 * rater.
 *
 * <p>Elle ignore les joueurs. Une visee automatique en duel enleverait au tir a l'arc ce qui en fait
 * une competence, et c'est la seule ligne que le mod ne veut pas franchir.
 */
public class SeekerEnchantment extends BowEnchantment {

	private static final int MAX_LEVEL = 2;

	/** Demi-angle du cone de recherche, en degres, par niveau. */
	private static final double[] CONE = {12.0, 22.0};

	/** Portee de la recherche, en blocs, par niveau. */
	private static final double[] REACH = {20.0, 30.0};

	public SeekerEnchantment() {
		super(Rarity.VERY_RARE);
	}

	@Override
	public int getMaxLevel() {
		return MAX_LEVEL;
	}

	public static double coneFor(int level) {
		return CONE[index(level)];
	}

	public static double reachFor(int level) {
		return REACH[index(level)];
	}

	private static int index(int level) {
		return Math.max(1, Math.min(MAX_LEVEL, level)) - 1;
	}
}
