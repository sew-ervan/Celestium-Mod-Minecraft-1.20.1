package net.celestium.feature.enchant;

/**
 * Transpercement : la fleche traverse ce qu'elle touche et poursuit sa course.
 *
 * <p>Le jeu de base reserve cette propriete a l'arbalete. Rien dans le tir a l'arc ne s'y oppose —
 * les fleches savent deja traverser, seul l'enchantement manquait — et c'est la reponse la plus
 * directe a une file de creatures, la ou l'arbalete demandait de recharger entre deux tirs.
 *
 * <p>Trois niveaux, comme a l'arbalete : autant de creatures traversees que de niveaux.
 */
public class PiercingShotEnchantment extends BowEnchantment {

	private static final int MAX_LEVEL = 3;

	public PiercingShotEnchantment() {
		super(Rarity.UNCOMMON);
	}

	@Override
	public int getMaxLevel() {
		return MAX_LEVEL;
	}
}
