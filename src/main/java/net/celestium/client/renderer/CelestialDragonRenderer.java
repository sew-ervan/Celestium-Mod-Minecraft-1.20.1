package net.celestium.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.celestium.CelestiumMod;
import net.celestium.feature.mob.CelestialDragonEntity;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Le rendu du dragon celeste.
 *
 * <p>Il emprunte la geometrie du phantasme du jeu de base plutot que d'en declarer une nouvelle.
 * C'est un choix assume : la silhouette convient — une bete ailee, sans pattes, faite pour le vol —
 * et modeler un dragon de toutes pieces demanderait un travail de sculpture que la texture seule
 * suffit ici a remplacer.
 *
 * <p>La texture, elle, est propre au mod. Elle n'emprunte rien : c'est un ciel etoile applique sur
 * la bete, ce qui la distingue au premier coup d'oeil du phantasme dont elle reprend la forme.
 */
public class CelestialDragonRenderer extends MobRenderer<CelestialDragonEntity, PhantomModel<CelestialDragonEntity>> {

	private static final ResourceLocation TEXTURE =
			CelestiumMod.id("textures/entity/celestial_dragon.png");

	/** Facteur d'agrandissement. Un phantasme a taille reelle ne serait pas un gardien de tresor. */
	private static final float SCALE = 3.0F;

	public CelestialDragonRenderer(EntityRendererProvider.Context context) {
		super(context, new PhantomModel<>(context.bakeLayer(ModelLayers.PHANTOM)), 1.2F);
	}

	@Override
	protected void scale(CelestialDragonEntity dragon, PoseStack pose, float partialTick) {
		pose.scale(SCALE, SCALE, SCALE);
	}

	@Override
	public ResourceLocation getTextureLocation(CelestialDragonEntity dragon) {
		return TEXTURE;
	}
}
