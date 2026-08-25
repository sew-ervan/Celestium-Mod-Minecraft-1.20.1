package net.celestium.feature.mob;

import net.celestium.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;

/**
 * L'oeuf de familier licorne.
 *
 * <p>Ce n'est pas un oeuf d'apparition ordinaire : celui-ci fait naitre un poulain deja acquis a
 * qui le pose. C'est ce qui en fait un familier plutot qu'une bete de plus — on ne le dompte pas, on
 * en herite.
 *
 * <p>Il ne se fabrique pas. Une licorne abattue le laisse parfois, et c'est la seule facon d'en
 * obtenir un : le familier est ce qui reste de la bete qu'on a tuee.
 */
public class UnicornFoalEggItem extends Item {

	public UnicornFoalEggItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!(context.getLevel() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}

		BlockPos above = context.getClickedPos().relative(context.getClickedFace());

		UnicornEntity foal = ModEntities.UNICORN.get().create(level);
		if (foal == null) {
			return InteractionResult.FAIL;
		}

		foal.moveTo(above.getX() + 0.5, above.getY(), above.getZ() + 0.5,
				context.getHorizontalDirection().toYRot(), 0.0F);

		// L'ordre compte : la mise en place du jeu de base fixe l'age et remet les caracteristiques
		// a leur valeur d'espece. Declarer le poulain avant qu'elle passe reviendrait a la laisser
		// le vieillir.
		foal.finalizeSpawn(level, level.getCurrentDifficultyAt(above), MobSpawnType.SPAWN_EGG,
				null, null);

		foal.setBaby(true);
		foal.setTamed(true);
		foal.setPersistenceRequired();

		Player player = context.getPlayer();
		if (player != null) {
			foal.setOwnerUUID(player.getUUID());
		}

		level.addFreshEntity(foal);
		level.playSound(null, above, SoundEvents.HORSE_AMBIENT, SoundSource.NEUTRAL, 1.0F, 1.6F);

		context.getItemInHand().shrink(1);
		return InteractionResult.CONSUME;
	}
}
