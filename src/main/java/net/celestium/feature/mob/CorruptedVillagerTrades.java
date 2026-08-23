package net.celestium.feature.mob;

import net.celestium.init.ModBlocks;
import net.celestium.init.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;

/**
 * Ce que les villageois corrompus ont a echanger.
 *
 * <p>Leur monnaie est le fragment de Demonium. Il n'y a pas d'emeraude dans les Terres du demon,
 * et il ne doit pas y en avoir : une monnaie qu'on apporte de l'Overworld ferait de leur boutique
 * une extension du commerce d'a cote. Celle-ci se ramasse sur place, a la pioche.
 *
 * <p>Ils ne vendent rien qui ne vienne de chez eux. L'interet du troc n'est pas de contourner
 * l'artisanat mais d'eviter des allers-retours : refaire son equipement de voyage sans repasser le
 * portail, se procurer du bois sans abattre d'arbre.
 *
 * <p>Une seule offre sort de ce cadre, et elle se merite : contre un coeur de demon, ils cedent
 * leurs reserves. C'est la seule chose contre laquelle ils les cedent, et il n'y a qu'une facon
 * d'en obtenir un.
 */
public final class CorruptedVillagerTrades {

	/** Nombre d'utilisations d'une offre courante avant rupture de stock. */
	private static final int COMMON_USES = 12;

	/** Nombre d'utilisations de l'offre au coeur : elle ne se repete pas beaucoup. */
	private static final int PRIZE_USES = 3;

	/**
	 * Variation appliquee au prix a la creation du villageois.
	 *
	 * <p>Deux villageois n'affichent pas tout a fait les memes tarifs : cela rend la visite d'un
	 * second village utile, et evite que le premier rencontre ne fixe le prix pour toujours.
	 */
	private static final int PRICE_SPREAD = 2;

	private CorruptedVillagerTrades() {
	}

	public static MerchantOffers create(RandomSource random) {
		MerchantOffers offers = new MerchantOffers();

		// Du bois sans avoir a bucheronner : l'offre d'entree, la moins chere.
		offers.add(sale(random, 4, ModBlocks.BOIS_DU_DEMON.log.get(), 6, COMMON_USES));

		// De quoi rafistoler la tenue de voyage sur place, sans repasser le portail.
		offers.add(sale(random, 6, ModItems.CORRUPTED_CELESTIUM_FRAGMENT.get(), 1, COMMON_USES));
		offers.add(sale(random, 12, ModItems.CORRUPTED_CELESTIUM_INGOT.get(), 1, COMMON_USES));

		// L'offre du coeur. Le prix est fixe : on ne marchande pas la depouille d'un demon.
		offers.add(new MerchantOffer(
				new ItemStack(ModItems.DEMON_HEART.get(), 1),
				new ItemStack(ModItems.DEMONIUM_INGOT.get(), 12),
				PRIZE_USES, 0, 0.0F));

		return offers;
	}

	/**
	 * Une offre payee en fragments de Demonium.
	 *
	 * <p>Le prix demande varie autour de la valeur donnee, sans jamais descendre sous un fragment.
	 */
	private static MerchantOffer sale(RandomSource random, int basePrice, ItemLike sold, int amount,
			int uses) {

		int price = Math.max(1, basePrice + random.nextInt(PRICE_SPREAD * 2 + 1) - PRICE_SPREAD);

		return new MerchantOffer(
				new ItemStack(ModItems.DEMONIUM_FRAGMENT.get(), price),
				new ItemStack(sold, amount),
				uses, 0, 0.0F);
	}
}
