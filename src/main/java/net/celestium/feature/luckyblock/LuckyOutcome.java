package net.celestium.feature.luckyblock;

/**
 * Une issue possible d'un bloc chance, avec son poids dans le tirage.
 *
 * @param event    l'effet a declencher
 * @param weight   son poids de base
 * @param quality  ce que chaque cran de chance ajoute a ce poids, negatif pour les mauvais coups
 * @param message  la cle de traduction annoncee au joueur
 * @param fortune  vrai si l'issue est bonne — sert au classement des blocs et aux tests
 */
public record LuckyOutcome(LuckyEvent event, int weight, int quality, String message, boolean fortune) {

	/**
	 * Poids reel de cette issue pour un joueur donne.
	 *
	 * <p>La formule est celle des tables de butin du jeu : {@code poids + qualite x chance}, sans
	 * jamais descendre sous zero. Une potion de chance monte l'attribut d'un cran, l'effet de
	 * malchance le descend d'autant. Les bonnes issues portent une qualite positive et les
	 * mauvaises une negative, si bien que la chance et la malchance se ressentent toutes deux.
	 */
	public int weightFor(float luck) {
		return Math.max(0, this.weight + Math.round(this.quality * luck));
	}

	/** Une bonne issue. */
	public static LuckyOutcome good(LuckyEvent event, int weight, int quality, String message) {
		return new LuckyOutcome(event, weight, quality, message, true);
	}

	/** Un mauvais coup : sa qualite est negative, donc la chance le rend plus rare. */
	public static LuckyOutcome bad(LuckyEvent event, int weight, int quality, String message) {
		return new LuckyOutcome(event, weight, -quality, message, false);
	}
}
