
package net.celestium.client.renderer;

import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.celestium.entity.model.MagieBdfCelesteModel;
import net.celestium.entity.MagieBdfCelesteEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class MagieBdfCelesteRenderer extends GeoEntityRenderer<MagieBdfCelesteEntity> {
	public MagieBdfCelesteRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new MagieBdfCelesteModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	public RenderType getRenderType(MagieBdfCelesteEntity entity, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		stack.scale(3.9f, 3.9f, 3.9f);
		return RenderType.entityTranslucent(getTextureLocation(entity));
	}
}
