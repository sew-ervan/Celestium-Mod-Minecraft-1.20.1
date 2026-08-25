package net.celestium.client.renderer;

import net.celestium.CelestiumMod;
import net.celestium.client.model.UnicornModel;
import net.celestium.feature.mob.UnicornEntity;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Rendu de la licorne.
 *
 * <p>Il derive du rendu de cheval du jeu de base, ce qui lui donne gratuitement la reduction du
 * poulain : le meme modele sert la bete adulte et son petit, a l'echelle pres.
 */
public class UnicornRenderer extends AbstractHorseRenderer<UnicornEntity, UnicornModel<UnicornEntity>> {

	private static final ResourceLocation TEXTURE = CelestiumMod.id("textures/entity/unicorn.png");

	public UnicornRenderer(EntityRendererProvider.Context context) {
		super(context, new UnicornModel<>(context.bakeLayer(UnicornModel.LAYER)), 1.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(UnicornEntity entity) {
		return TEXTURE;
	}
}
