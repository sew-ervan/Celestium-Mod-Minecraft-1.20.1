
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.celestium.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.celestium.CelestiumMod;

public class CelestiumModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CelestiumMod.MODID);
	public static final RegistryObject<SoundEvent> ESPECE_DE_TERRORISTE = REGISTRY.register("espece_de_terroriste", () -> new SoundEvent(new ResourceLocation("celestium", "espece_de_terroriste")));
	public static final RegistryObject<SoundEvent> NOOT_NOOT_DRILL = REGISTRY.register("noot_noot_drill", () -> new SoundEvent(new ResourceLocation("celestium", "noot_noot_drill")));
	public static final RegistryObject<SoundEvent> GG = REGISTRY.register("gg", () -> new SoundEvent(new ResourceLocation("celestium", "gg")));
}
