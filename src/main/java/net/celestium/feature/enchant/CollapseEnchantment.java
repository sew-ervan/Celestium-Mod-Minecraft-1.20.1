package net.celestium.feature.enchant;

/**
 * Effondrement : la ou la fleche se plante, tout ce qui est proche est tire vers elle.
 *
 * <p>C'est la matiere noire portee a bout de fleche — le meme effet que le puits de gravite, mais
 * sur un instant et a distance. L'arc cesse d'etre seulement une arme pour devenir un moyen de
 * rassembler : une fleche dans un groupe le ramene en tas, ou l'arrache d'un bord.
 *
 * <p>Un seul niveau. Un effondrement plus fort projetterait au lieu d'attirer, et un plus faible ne
 * se verrait pas.
 */
public class CollapseEnchantment extends BowEnchantment {

	/** Rayon de l'attraction, en blocs. */
	public static final double RADIUS = 5.0;

	/** Vitesse imprimee au plus proche ; elle decroit avec la distance. */
	public static final double PULL = 0.9;

	public CollapseEnchantment() {
		super(Rarity.VERY_RARE);
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}
}
