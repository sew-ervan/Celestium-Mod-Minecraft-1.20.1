package net.celestium.client.model;

import net.celestium.CelestiumMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/**
 * Modele GeckoLib defini par ses trois ressources.
 *
 * <p>Evite d'ecrire une classe de modele par creature : les trois entites du mod ne different que
 * par leurs chemins de fichiers.
 */
public class SimpleGeoModel<T extends GeoAnimatable> extends GeoModel<T> {

	private final ResourceLocation model;
	private final ResourceLocation texture;
	private final ResourceLocation animation;

	/**
	 * @param name nom de base partage par le modele, la texture et les animations
	 */
	public SimpleGeoModel(String name, String textureName) {
		this.model = CelestiumMod.id("geo/" + name + ".geo.json");
		this.texture = CelestiumMod.id("textures/entity/" + textureName + ".png");
		this.animation = CelestiumMod.id("animations/" + name + ".animation.json");
	}

	@Override
	public ResourceLocation getModelResource(T animatable) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(T animatable) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(T animatable) {
		return this.animation;
	}
}
