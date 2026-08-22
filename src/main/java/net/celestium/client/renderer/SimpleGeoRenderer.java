package net.celestium.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** Rendu GeckoLib generique : un modele et un rayon d'ombre suffisent. */
public class SimpleGeoRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {

	public SimpleGeoRenderer(EntityRendererProvider.Context context, GeoModel<T> model, float shadowRadius) {
		super(context, model);
		this.shadowRadius = shadowRadius;
	}
}
