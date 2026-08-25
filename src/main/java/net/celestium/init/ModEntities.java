package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.feature.magie.entity.CelestialBoltEntity;
import net.celestium.feature.familiar.FennecFamiliar;
import net.celestium.feature.familiar.MiniDemonFamiliar;
import net.celestium.feature.familiar.MiniGuardianFamiliar;
import net.celestium.feature.mob.CelestialDragonEntity;
import net.celestium.feature.mob.UnicornEntity;
import net.celestium.feature.mob.CorruptedVillagerEntity;
import net.celestium.feature.mob.DemonSwordsmanEntity;
import net.celestium.feature.mob.ParasiteEntity;
import net.celestium.feature.mob.MiniWardenEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
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

	/**
	 * Le dragon celeste : un gardien de tresor, pas un verrou de progression.
	 *
	 * <p>Il ne se declare pas dans les regles d'apparition : on ne le rencontre qu'au-dessus du tas
	 * qu'il garde, pose la par la structure. Un dragon qui surgirait au hasard cesserait d'etre une
	 * trouvaille.
	 */
	public static final RegistryObject<EntityType<CelestialDragonEntity>> CELESTIAL_DRAGON =
			ENTITIES.register("celestial_dragon", () -> EntityType.Builder
					.<CelestialDragonEntity>of(CelestialDragonEntity::new, MobCategory.MONSTER)
					.sized(2.6F, 1.4F)
					.fireImmune()
					.clientTrackingRange(16)
					.build("celestial_dragon"));

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

	// --- La licorne et les familiers ---

	/**
	 * La licorne : elle appartient a la categorie des creatures, comme les chevaux.
	 *
	 * <p>Cette categorie decide de la frequence d'apparition et du moment ou le jeu s'en debarrasse.
	 * La declarer hostile l'aurait fait naitre la nuit, dans le noir, et disparaitre au matin.
	 */
	public static final RegistryObject<EntityType<UnicornEntity>> UNICORN =
			ENTITIES.register("unicorn", () -> EntityType.Builder
					.<UnicornEntity>of(UnicornEntity::new, MobCategory.CREATURE)
					.sized(1.3964844F, 1.6F)
					.clientTrackingRange(10)
					.build("unicorn"));

	public static final RegistryObject<EntityType<FennecFamiliar>> FENNEC =
			ENTITIES.register("fennec", () -> EntityType.Builder
					.<FennecFamiliar>of(FennecFamiliar::new, MobCategory.CREATURE)
					.sized(0.6F, 0.7F)
					.clientTrackingRange(8)
					.build("fennec"));

	public static final RegistryObject<EntityType<MiniGuardianFamiliar>> MINI_GUARDIAN =
			ENTITIES.register("mini_guardian", () -> EntityType.Builder
					.<MiniGuardianFamiliar>of(MiniGuardianFamiliar::new, MobCategory.CREATURE)
					.sized(0.6F, 1.1F)
					.clientTrackingRange(8)
					.build("mini_guardian"));

	public static final RegistryObject<EntityType<MiniDemonFamiliar>> MINI_DEMON =
			ENTITIES.register("mini_demon", () -> EntityType.Builder
					.<MiniDemonFamiliar>of(MiniDemonFamiliar::new, MobCategory.CREATURE)
					.sized(0.6F, 1.2F)
					.fireImmune()
					.clientTrackingRange(8)
					.build("mini_demon"));

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

		// La licorne et les trois familiers naissent au jour comme a la nuit : ce ne sont pas des
		// creatures hostiles, et la regle des animaux du jeu de base — de l'herbe, de la lumiere —
		// ne vaudrait ni dans un desert de sable ni dans deux dimensions sans ciel. Il leur suffit
		// d'un sol et de la place pour tenir debout.
		SpawnPlacements.register(UNICORN.get(),
				SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				Animal::checkAnimalSpawnRules);

		SpawnPlacements.register(FENNEC.get(),
				SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				Mob::checkMobSpawnRules);

		SpawnPlacements.register(MINI_GUARDIAN.get(),
				SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				Mob::checkMobSpawnRules);

		SpawnPlacements.register(MINI_DEMON.get(),
				SpawnPlacements.Type.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				Mob::checkMobSpawnRules);
	}

	@SubscribeEvent
	public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
		event.put(MINI_WARDEN.get(), MiniWardenEntity.createAttributes().build());
		event.put(DEMON_SWORDSMAN.get(), DemonSwordsmanEntity.createAttributes().build());
		event.put(PARASITE.get(), ParasiteEntity.createAttributes().build());
		event.put(CORRUPTED_VILLAGER.get(), CorruptedVillagerEntity.createAttributes().build());
		event.put(CELESTIAL_DRAGON.get(), CelestialDragonEntity.createAttributes().build());
		event.put(UNICORN.get(), UnicornEntity.createAttributes().build());
		event.put(FENNEC.get(), FennecFamiliar.createAttributes().build());
		event.put(MINI_GUARDIAN.get(), MiniGuardianFamiliar.createAttributes().build());
		event.put(MINI_DEMON.get(), MiniDemonFamiliar.createAttributes().build());
	}
}
