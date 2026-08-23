package net.celestium.feature.luckyblock;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

/**
 * Ce qui arrive quand un bloc chance est casse.
 *
 * <p>Une table de butin ne sait rendre que des objets. Tout le reste — une horde qui surgit, une
 * tour qui se construit, le sol qui s'ouvre — demande du code, et c'est ce que cette interface
 * represente : un effet, declenche cote serveur, a l'emplacement du bloc.
 *
 * <p>Le tirage aleatoire est passe en parametre plutot que pris sur le monde : les evenements
 * restent ainsi reproductibles a partir d'une graine, ce qui les rend testables.
 */
@FunctionalInterface
public interface LuckyEvent {

	/**
	 * Declenche l'effet.
	 *
	 * @param level  le monde, cote serveur
	 * @param pos    l'emplacement qu'occupait le bloc
	 * @param player celui qui l'a casse
	 * @param random la source de hasard a utiliser
	 */
	void fire(ServerLevel level, BlockPos pos, Player player, RandomSource random);
}
