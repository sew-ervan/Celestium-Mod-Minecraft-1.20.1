package net.celestium.client.renderer;

import net.celestium.CelestiumMod;
import net.celestium.feature.familiar.FennecFamiliar;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Rendu du fennec.
 *
 * <p>Il emprunte la geometrie de l'ocelot du jeu de base — le seul modele quadrupede dont la
 * declaration accepte une creature qui n'est pas de son espece. Corps bas, longue queue, museau
 * pointu : la silhouette passe pour un petit canide sans qu'on ait a la redessiner, et tout ce qui
 * distingue le fennec tient alors dans sa robe.
 */
public class FennecRenderer extends MobRenderer<FennecFamiliar, OcelotModel<FennecFamiliar>> {

	private static final ResourceLocation TEXTURE = CelestiumMod.id("textures/entity/fennec.png");

	public FennecRenderer(EntityRendererProvider.Context context) {
		super(context, new OcelotModel<>(context.bakeLayer(ModelLayers.OCELOT)), 0.4F);
	}

	@Override
	public ResourceLocation getTextureLocation(FennecFamiliar entity) {
		return TEXTURE;
	}
}
