package net.celestium.client.renderer;

import net.celestium.feature.mob.CorruptedVillagerEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Rendu du villageois corrompu.
 *
 * <p>Reprend le modele de villageois vanilla et la texture du villageois zombifie. Cette derniere
 * est referencee, pas copiee : le client la possede deja, et elle dit exactement ce qu'il faut
 * comprendre de la creature.
 */
public class CorruptedVillagerRenderer
		extends MobRenderer<CorruptedVillagerEntity, VillagerModel<CorruptedVillagerEntity>> {

	private static final ResourceLocation TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/zombie_villager/zombie_villager.png");

	public CorruptedVillagerRenderer(EntityRendererProvider.Context context) {
		super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(CorruptedVillagerEntity entity) {
		return TEXTURE;
	}
}
