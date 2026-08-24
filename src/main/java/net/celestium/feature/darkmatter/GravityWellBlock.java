package net.celestium.feature.darkmatter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


/**
 * Le puits de gravite : il tire a lui tout ce qui tombe alentour.
 *
 * <p>C'est le seul usage propre qu'on puisse donner a la matiere noire sans trahir ce qu'elle est.
 * Dans l'univers reel, elle ne brille pas, ne se touche pas, ne reagit a rien — on ne la connait
 * que parce qu'elle attire. Un bloc qui attire est donc la seule chose qu'elle sache faire, et
 * c'est deja beaucoup : pose au-dessus d'un entonnoir, il ramasse une recolte entiere.
 *
 * <p>L'experience est attiree elle aussi. Un puits qui trierait ce qu'il aspire demanderait une
 * explication ; celui-ci n'en demande aucune.
 */
public class GravityWellBlock extends Block {

	/** Portee de l'attraction, en blocs. */
	private static final double RANGE = 8.0;

	/** Periode entre deux tirages, en ticks. Vingt fois par seconde serait du gaspillage. */
	private static final int INTERVAL = 5;

    /** Vitesse imprimee a ce qui est attire, par tick. */
	private static final double PULL = 0.16;

	/** Distance sous laquelle on considere que l'objet est arrive. */
	private static final double REACHED = 0.8;

	public GravityWellBlock(Properties properties) {
		super(properties.randomTicks());
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
		this.pull(level, pos);
		level.scheduleTick(pos, this, INTERVAL);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
		super.onPlace(state, level, pos, previous, moving);
		level.scheduleTick(pos, this, INTERVAL);
	}

	/**
	 * Rapproche d'un cran tout ce qui flotte a portee.
	 *
	 * <p>La vitesse est imprimee vers le centre plutot que la position posee d'autorite : un objet
	 * teleporte disparaitrait et reapparaitrait, la ou un objet attire se voit venir. C'est ce qui
	 * rend le bloc lisible sans qu'il ait besoin d'etre explique.
	 */
	private void pull(ServerLevel level, BlockPos pos) {
		Vec3 centre = Vec3.atCenterOf(pos);
		AABB around = new AABB(pos).inflate(RANGE);

		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, around)) {
			drawIn(item, centre);
		}
		for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, around)) {
			drawIn(orb, centre);
		}

		level.sendParticles(ParticleTypes.PORTAL,
				centre.x, centre.y + 0.6, centre.z, 2, 0.3, 0.2, 0.3, 0.02);
	}

	private static void drawIn(net.minecraft.world.entity.Entity entity, Vec3 centre) {
		Vec3 toCentre = centre.subtract(entity.position());
		double distance = toCentre.length();

		if (distance < REACHED || distance > RANGE) {
			return;
		}

		// L'attraction se renforce en approchant, comme une vraie gravite : de loin on derive, de
		// pres on tombe.
		double strength = PULL * Mth.clamp(1.5 - distance / RANGE, 0.2, 1.5);
		entity.setDeltaMovement(entity.getDeltaMovement().add(toCentre.normalize().scale(strength)));
		entity.hasImpulse = true;
	}
}
