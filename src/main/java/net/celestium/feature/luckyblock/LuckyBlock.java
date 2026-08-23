package net.celestium.feature.luckyblock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Un bloc chance.
 *
 * <p>Le comportement tenait auparavant dans une table de butin : un tirage, un objet. C'etait
 * suffisant tant qu'on ne voulait que des objets, et c'est devenu la limite des le moment ou l'on
 * a voulu des hordes, des explosions et des batisses. Une table de butin ne sait rendre que des
 * objets ; elle ne sait pas ouvrir le sol.
 *
 * <p>Le tirage se fait donc ici, dans {@link LuckyTier#roll}, et l'issue tiree fait ce qu'elle veut
 * du monde. Le bloc ne rend plus rien de lui-meme — sa table de butin est vide, et les cadeaux
 * passent par les evenements comme le reste.
 *
 * <p>La chance du joueur pese sur le tirage dans les deux sens : elle rend les bonnes issues plus
 * probables et les mauvaises plus rares, et la malchance fait l'inverse.
 */
public class LuckyBlock extends Block {

	private final LuckyTier tier;

	public LuckyBlock(LuckyTier tier, Properties properties) {
		super(properties);
		this.tier = tier;
	}

	public LuckyTier tier() {
		return this.tier;
	}

	/**
	 * Casse par un joueur : c'est le cas courant.
	 *
	 * <p>L'appel a lieu avant que le bloc ne disparaisse, donc l'evenement peut ecrire a son
	 * emplacement sans que la suppression du bloc n'efface ce qu'il vient de poser — d'ou le retrait
	 * explicite avant de le declencher.
	 */
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (level instanceof ServerLevel server && !player.isCreative()) {
			level.removeBlock(pos, false);
			this.trigger(server, pos, player);
		}
		super.playerWillDestroy(level, pos, state, player);
	}

	/**
	 * Detruit par une explosion.
	 *
	 * <p>Sans cela, une chaine de blocs chance se desamorcerait d'elle-meme : le premier explose,
	 * les voisins disparaissent sans rien declencher. C'est justement l'enchainement qui fait
	 * l'interet du genre.
	 */
	@Override
	public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
		if (level instanceof ServerLevel server) {
			Player culprit = nearestPlayer(server, pos);
			if (culprit != null) {
				this.trigger(server, pos, culprit);
			}
		}
		super.wasExploded(level, pos, explosion);
	}

	private void trigger(ServerLevel level, BlockPos pos, Player player) {
		LuckyOutcome outcome = this.tier.roll(level.getRandom(), player.getLuck());

		outcome.event().fire(level, pos, player, level.getRandom());
		player.displayClientMessage(Component.translatable(outcome.message()), true);
	}

	/**
	 * Le joueur le plus proche, a qui imputer une explosion.
	 *
	 * <p>Un evenement a besoin de quelqu'un a viser. Au-dela de trente-deux blocs on considere que
	 * personne n'est concerne et le bloc se contente de disparaitre : declencher une horde dans un
	 * chunk desert ne ferait que peupler le monde de creatures que nul ne verra.
	 */
	@Nullable
	private static Player nearestPlayer(ServerLevel level, BlockPos pos) {
		return level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				32.0, false);
	}
}
