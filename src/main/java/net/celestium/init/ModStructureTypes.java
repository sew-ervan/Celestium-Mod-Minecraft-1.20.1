package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.worldgen.hoard.CelestialHoardStructure;
import net.celestium.worldgen.sanctum.CorruptedSanctumStructure;
import net.celestium.worldgen.village.DemonVillageStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/** Types de structures ecrites en code, par opposition a celles decrites en donnees. */
public class ModStructureTypes {

	public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
			DeferredRegister.create(Registries.STRUCTURE_TYPE, CelestiumMod.MOD_ID);

	public static final RegistryObject<StructureType<DemonVillageStructure>> DEMON_VILLAGE =
			STRUCTURE_TYPES.register("demon_village", () -> () -> DemonVillageStructure.CODEC);

	public static final RegistryObject<StructureType<CorruptedSanctumStructure>> CORRUPTED_SANCTUM =
			STRUCTURE_TYPES.register("corrupted_sanctum", () -> () -> CorruptedSanctumStructure.CODEC);

	public static final RegistryObject<StructureType<CelestialHoardStructure>> CELESTIAL_HOARD =
			STRUCTURE_TYPES.register("celestial_hoard", () -> () -> CelestialHoardStructure.CODEC);

	private ModStructureTypes() {
	}
}
