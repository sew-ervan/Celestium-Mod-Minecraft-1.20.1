package net.celestium.feature.familiar;

import net.celestium.init.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * Le petit demon : le familier des Terres du demon.
 *
 * <p>Il reprend la geometrie du demon epeiste, a une fraction de sa taille. Le rapprochement est
 * voulu : c'est la meme espece, prise avant qu'elle ne devienne ce qui garde la dimension. En
 * apprivoiser un revient a emmener chez soi ce qu'on est venu combattre.
 *
 * <p>Il ne brule pas — il vient d'un monde qui brule — et c'est ce qui le distingue vraiment des
 * deux autres : on peut le suivre partout, y compris la ou l'on ne devrait pas.
 */
public class MiniDemonFamiliar extends AnimatedFamiliar {

	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
	private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");

	public MiniDemonFamiliar(EntityType<? extends TamableAnimal> type, Level level) {
		super(type, level);
	}

	/** Le plus mordant des trois, et le plus fragile : il frappe et se fait frapper. */
	public static AttributeSupplier.Builder createAttributes() {
		return FamiliarEntity.createAttributes()
				.add(Attributes.MAX_HEALTH, 18.0)
				.add(Attributes.MOVEMENT_SPEED, 0.33)
				.add(Attributes.ATTACK_DAMAGE, 5.0);
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(ModItems.DEMONIUM_FRAGMENT.get());
	}

	@Override
	protected RawAnimation idleAnimation() {
		return IDLE;
	}

	@Override
	protected RawAnimation walkAnimation() {
		return WALK;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.HOGLIN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.HOGLIN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.HOGLIN_DEATH;
	}
}
