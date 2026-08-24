package net.celestium.feature.mount;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * La selle deux places.
 *
 * <p>Elle ne remplace pas la selle du jeu : elle s'ajoute derriere elle. On la pose sur une monture
 * deja sellee, d'un clic droit, et elle y reste. Ce n'est pas un oubli — une selle se pose et ne se
 * reprend pas, et pouvoir la recuperer a volonte reviendrait a la deplacer de monture en monture
 * pour n'en fabriquer qu'une.
 *
 * <p>Tout ce que le jeu declare sellable l'accepte : chevaux, anes, mulets, cochons, striders. La
 * mecanique de la seconde place est dans {@link TandemRiding}.
 */
public class TandemSaddleItem extends Item {

	public TandemSaddleItem() {
		super(new Item.Properties().stacksTo(1));
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
			InteractionHand hand) {

		if (!TandemRiding.canFit(target)) {
			return InteractionResult.PASS;
		}

		if (player.level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		TandemRiding.fit(target);
		stack.shrink(1);

		target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
				SoundEvents.HORSE_SADDLE, SoundSource.NEUTRAL, 0.5F, 1.0F);

		return InteractionResult.CONSUME;
	}
}
