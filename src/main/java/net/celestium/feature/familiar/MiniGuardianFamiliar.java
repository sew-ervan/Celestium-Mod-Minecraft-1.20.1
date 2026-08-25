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
 * Le petit gardien : le familier des terres corrompues.
 *
 * <p>A ne pas confondre avec le gardien miniature, qui est une creature hostile et n'existe que par
 * oeuf d'apparition. Celui-ci en reprend la geometrie — elle est deja dessinee et animee — mais rien
 * d'autre : il est plus petit, plus pale, et il ne s'en prend a personne de lui-meme.
 *
 * <p>Il se dompte au Celestium corrompu, qu'on ne trouve que la ou il vit. Le lien est le meme pour
 * les trois familiers du mod : on nourrit une bete de ce que produit son monde.
 */
public class MiniGuardianFamiliar extends AnimatedFamiliar {

	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
	private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");

	public MiniGuardianFamiliar(EntityType<? extends TamableAnimal> type, Level level) {
		super(type, level);
	}

	/** Le plus resistant des trois, et le plus lent : il encaisse la ou le fennec esquive. */
	public static AttributeSupplier.Builder createAttributes() {
		return FamiliarEntity.createAttributes()
				.add(Attributes.MAX_HEALTH, 24.0)
				.add(Attributes.MOVEMENT_SPEED, 0.28)
				.add(Attributes.ATTACK_DAMAGE, 4.0)
				.add(Attributes.ARMOR, 3.0);
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(ModItems.CORRUPTED_CELESTIUM_FRAGMENT.get());
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
		return SoundEvents.WARDEN_HEARTBEAT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.WARDEN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.WARDEN_DEATH;
	}
}
