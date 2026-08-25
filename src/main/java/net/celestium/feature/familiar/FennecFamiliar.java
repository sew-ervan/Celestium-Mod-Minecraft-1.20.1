package net.celestium.feature.familiar;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Le fennec : le familier de l'Overworld, et le seul qu'on trouve sans changer de monde.
 *
 * <p>Il vit dans le desert, ou le mod n'avait jusqu'ici rien a offrir : une etendue qu'on traverse
 * sans raison de s'y arreter. Le fennec lui donne cette raison.
 *
 * <p>Il emprunte la silhouette de l'ocelot du jeu de base. C'est le seul quadrupede dont le modele
 * accepte une creature qui n'est pas de son espece, et sa forme — corps bas, longue queue, museau
 * pointu — passe pour un petit canide sans qu'on ait a la redessiner. Ce qui le distingue tient a la
 * robe : sable clair, ventre pale.
 */
public class FennecFamiliar extends FamiliarEntity {

	public FennecFamiliar(EntityType<? extends TamableAnimal> type, Level level) {
		super(type, level);
	}

	/** Plus vif et plus fragile que les autres : c'est une bete de desert, pas un garde du corps. */
	public static AttributeSupplier.Builder createAttributes() {
		return FamiliarEntity.createAttributes()
				.add(Attributes.MAX_HEALTH, 12.0)
				.add(Attributes.MOVEMENT_SPEED, 0.38)
				.add(Attributes.ATTACK_DAMAGE, 2.0);
	}

	/**
	 * Il ne mange que du lapin.
	 *
	 * <p>C'est ce que chassent les fennecs, et c'est ce que le desert offre : la proie et le
	 * predateur se trouvent au meme endroit, sans qu'il faille rien rapporter de chez soi.
	 */
	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(Items.RABBIT) || stack.is(Items.COOKED_RABBIT);
	}

	/**
	 * Assis, il se tasse.
	 *
	 * <p>Le modele de l'ocelot ne connait pas la pose assise d'un chat — celle-ci est propre au
	 * modele du chat. Il connait en revanche la pose accroupie, qui donne exactement ce qu'on
	 * attend : une bete posee au sol, pattes repliees.
	 */
	@Override
	public void tick() {
		super.tick();

		Pose wanted = this.isOrderedToSit() ? Pose.CROUCHING : Pose.STANDING;
		if (this.getPose() != wanted) {
			this.setPose(wanted);
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.FOX_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.FOX_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.FOX_DEATH;
	}
}
