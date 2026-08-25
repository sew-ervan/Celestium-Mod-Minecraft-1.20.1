package net.celestium.feature.bow;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * L'arc celeste.
 *
 * <p>Le mod n'avait aucune arme de jet : tout son equipement supposait qu'on aille au contact, ce
 * qui fermait une facon entiere de jouer. Cet arc l'ouvre, et il s'inscrit dans la progression du
 * Celestium comme les outils et l'armure — il se forge du meme metal, aux memes conditions.
 *
 * <p>Il ne reinvente pas le tir. Ce qui le distingue d'un arc ordinaire tient en trois points : il
 * frappe plus fort a charge egale, il dure deux fois plus longtemps, et il ne brule pas. Le reste —
 * la portee, le temps de bande, la trajectoire — est celui que tout joueur connait deja, et c'est
 * voulu : une arme de jet qui se manie autrement demanderait d'etre reapprise.
 *
 * <p>Sa vraie difference est ailleurs, dans ce qu'il accepte de recevoir. Les quatre enchantements
 * d'arc du mod ne s'obtiennent qu'a la table corrompue, et c'est la que cet arc prend son sens.
 */
public class CelestialBowItem extends BowItem {

	/**
	 * Degats ajoutes a chaque fleche tiree.
	 *
	 * <p>Une fleche ordinaire porte deux points de degats de base, que la charge multiplie ensuite.
	 * Un point de plus represente donc la moitie d'un cran de Puissance, sans rien couter en
	 * enchantement : de quoi rendre l'arc preferable a un arc ordinaire, pas de quoi rendre les
	 * enchantements superflus.
	 */
	private static final double EXTRA_DAMAGE = 1.0;

	/** Deux fois la resistance d'un arc du jeu de base, qui en compte trois cent quatre-vingt-quatre. */
	private static final int DURABILITY = 768;

	public CelestialBowItem() {
		super(new Item.Properties().durability(DURABILITY).rarity(Rarity.UNCOMMON).fireResistant());
	}

	@Override
	public AbstractArrow customArrow(AbstractArrow arrow) {
		arrow.setBaseDamage(arrow.getBaseDamage() + EXTRA_DAMAGE);
		return arrow;
	}
}
