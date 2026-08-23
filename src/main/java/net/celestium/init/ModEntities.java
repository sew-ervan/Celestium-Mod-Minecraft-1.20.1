package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.feature.magie.entity.CelestialBoltEntity;
import net.celestium.feature.mob.CorruptedVillagerEntity;
import net.celestium.feature.mob.DemonSwordsmanEntity;
import net.celestium.feature.mob.ParasiteEntity;
import net.celestium.feature.mob.MiniWardenEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Registre des entites et de leurs attributs. */
@Mod.EventBusSubscriber(modid = CelestiumMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {

	public static final DeferredRegister<EntityType<?>> ENTITIES =
			DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CelestiumMod.MOD_ID);

	public static final RegistryObject<EntityType<MiniWardenEntity>> MINI_WARDEN =
			ENTITIES.register("mini_warden", () -> EntityType.Builder
					.<MiniWardenEntity>of(MiniWardenEntity::new, MobCategory.MONSTER)
					.sized(0.6F, 1.8F)
					.fireImmune()
					.clientTrackingRange(10)
					.build("mini_warden"));

	public static final RegistryObject<EntityType<DemonSwordsmanEntity>> DEMON_SWORDSMAN =
			ENTITIES.register("demon_swordsman", () -> EntityType.Builder
					.<DemonSwordsmanEntity>of(DemonSwordsmanEntity::new, MobCategory.MONSTER)
					.sized(1.0F, 4.5F)
					.fireImmune()
					.clientTrackingRange(12)
					.build("demon_swordsman"));

	public static final RegistryObject<EntityType<CelestialBoltEntity>> CELESTIAL_BOLT =
			ENTITIES.register("celestial_bolt", () -> EntityType.Builder
					.<CelestialBoltEntity>of(CelestialBoltEntity::new, MobCategory.MISC)
					.sized(0.5F, 0.5F)
					.fireImmune()
					.clientTrackingRange(8)
					.updateInterval(1)
					.build("celestial_bolt"));

	public static final RegistryObject<EntityType<ParasiteEntity>> PARASITE =
			ENTITIES.register("parasite", () -> EntityType.Builder
					.<ParasiteEntity>of(ParasiteEntity::new, MobCategory.MONSTER)
					.sized(0.5F, 0.6F)
					.fireImmune()
					.clientTrackingRange(8)
					.build("parasite"));

	public static final RegistryObject<EntityType<CorruptedVillagerEntity>> CORRUPTED_VILLAGER =
			ENTITIES.register("corrupted_villager", () -> EntityType.Builder
					.<CorruptedVillagerEntity>of(CorruptedVillagerEntity::new, MobCategory.MONSTER)
					.sized(0.6F, 1.95F)
					.clientTrackingRange(10)
					.build("corrupted_villager"));

	private ModEntities() {
	}

	/**
	 * Declare ou et quand les creatures peuvent apparaitre.
	 *
	 * <p>Sans cet enregistrement, un modificateur de biome a beau les proposer, le jeu ne sait pas
	 * a quelles conditions les poser et elles n'apparaissent jamais.
	 *
	 * <p>Le gardien miniature n'est volontairement pas declare : il reste accessible par oeuf de
	 * spawn uniquement, comme dans le mod d'origine.
	 */
	public static void registerSpawnPlacements() {
		// Le demon epeiste ne hante que sa propre dimension, en nuit permanente : exiger
		// l'obscurite y serait sans objet.
		SpawnPlacements.register(DEMON_SWORDSMAN.get(),
				SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				Monster::checkAnyLightMonsterSpawnRules);

		// Les terres du demon sont en nuit permanente : exiger l'obscurite y serait sans effet,
		// mais la regle vanilla verifie aussi le sol et la place disponible.
		SpawnPlacements.register(PARASITE.get(),
				SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				Monster::checkAnyLightMonsterSpawnRules);

		SpawnPlacements.register(CORRUPTED_VILLAGER.get(),
				SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				Monster::checkAnyLightMonsterSpawnRules);
	}

	@SubscribeEvent
	public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
		event.put(MINI_WARDEN.get(), MiniWardenEntity.createAttributes().build());
		event.put(DEMON_SWORDSMAN.get(), DemonSwordsmanEntity.createAttributes().build());
		event.put(PARASITE.get(), ParasiteEntity.createAttributes().build());
		event.put(CORRUPTED_VILLAGER.get(), CorruptedVillagerEntity.createAttributes().build());
	}
}
