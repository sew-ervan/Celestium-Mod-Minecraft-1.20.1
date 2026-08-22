
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.celestium.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

import net.celestium.entity.MiniWardenEntity;
import net.celestium.entity.MagieBdfCelesteEntity;
import net.celestium.entity.DemonEpeisteEntity;
import net.celestium.CelestiumMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CelestiumModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CelestiumMod.MODID);
	public static final RegistryObject<EntityType<MiniWardenEntity>> MINI_WARDEN = register("mini_warden", EntityType.Builder.<MiniWardenEntity>of(MiniWardenEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
			.setUpdateInterval(3).setCustomClientFactory(MiniWardenEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<DemonEpeisteEntity>> DEMON_EPEISTE = register("demon_epeiste", EntityType.Builder.<DemonEpeisteEntity>of(DemonEpeisteEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(DemonEpeisteEntity::new).fireImmune().sized(1f, 4.5f));
	public static final RegistryObject<EntityType<MagieBdfCelesteEntity>> MAGIE_BDF_CELESTE = register("magie_bdf_celeste", EntityType.Builder.<MagieBdfCelesteEntity>of(MagieBdfCelesteEntity::new, MobCategory.MISC)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MagieBdfCelesteEntity::new).fireImmune().sized(0.5f, 0.5f));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			MiniWardenEntity.init();
			DemonEpeisteEntity.init();
			MagieBdfCelesteEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(MINI_WARDEN.get(), MiniWardenEntity.createAttributes().build());
		event.put(DEMON_EPEISTE.get(), DemonEpeisteEntity.createAttributes().build());
		event.put(MAGIE_BDF_CELESTE.get(), MagieBdfCelesteEntity.createAttributes().build());
	}
}
