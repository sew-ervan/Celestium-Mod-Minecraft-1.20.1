
package net.celestium.client.renderer;

import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.celestium.entity.model.DemonEpeisteModel;
import net.celestium.entity.DemonEpeisteEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class DemonEpeisteRenderer extends GeoEntityRenderer<DemonEpeisteEntity> {
	public DemonEpeisteRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new DemonEpeisteModel());
		this.shadowRadius = 1f;
	}

	@Override
	public RenderType getRenderType(DemonEpeisteEntity entity, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		stack.scale(1f, 1f, 1f);
		return RenderType.entityTranslucent(getTextureLocation(entity));
	}
}
