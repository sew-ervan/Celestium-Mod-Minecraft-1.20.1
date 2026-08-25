package net.celestium.feature.mount;

import net.minecraft.world.item.Item;

/**
 * La selle deux places.
 *
 * <p>Elle ne remplace pas la selle du jeu : elle s'ajoute derriere elle. On la pose d'un clic droit
 * sur une monture domptee et deja sellee, et elle y reste. Ce n'est pas un oubli — une selle se pose
 * et ne se reprend pas, et pouvoir la recuperer a volonte reviendrait a la deplacer de monture en
 * monture pour n'en fabriquer qu'une.
 *
 * <p>Cette classe est volontairement vide. Tout ce que la selle fait — se poser, puis porter un
 * second cavalier — est dans {@link TandemRiding}, parce que les deux gestes doivent etre pris
 * avant que la monture ne reponde. Le jeu interroge la monture avant l'objet tenu : un cheval
 * dompte et selle fait monter le joueur, et la selle posee depuis l'objet ne serait jamais
 * consultee sur la seule monture qui l'interesse vraiment.
 */
public class TandemSaddleItem extends Item {

	public TandemSaddleItem() {
		super(new Item.Properties().stacksTo(1));
	}
}
