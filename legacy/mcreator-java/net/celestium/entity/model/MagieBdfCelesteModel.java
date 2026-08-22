package net.celestium.entity.model;

import software.bernie.geckolib3.model.AnimatedGeoModel;

import net.minecraft.resources.ResourceLocation;

import net.celestium.entity.MagieBdfCelesteEntity;

public class MagieBdfCelesteModel extends AnimatedGeoModel<MagieBdfCelesteEntity> {
	@Override
	public ResourceLocation getAnimationResource(MagieBdfCelesteEntity entity) {
		return new ResourceLocation("celestium", "animations/bdfceleste.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MagieBdfCelesteEntity entity) {
		return new ResourceLocation("celestium", "geo/bdfceleste.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MagieBdfCelesteEntity entity) {
		return new ResourceLocation("celestium", "textures/entities/" + entity.getTexture() + ".png");
	}

}
