package net.celestium.entity.model;

import software.bernie.geckolib3.model.AnimatedGeoModel;

import net.minecraft.resources.ResourceLocation;

import net.celestium.entity.MiniWardenEntity;

public class MiniWardenModel extends AnimatedGeoModel<MiniWardenEntity> {
	@Override
	public ResourceLocation getAnimationResource(MiniWardenEntity entity) {
		return new ResourceLocation("celestium", "animations/miniwarden.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MiniWardenEntity entity) {
		return new ResourceLocation("celestium", "geo/miniwarden.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MiniWardenEntity entity) {
		return new ResourceLocation("celestium", "textures/entities/" + entity.getTexture() + ".png");
	}

}
