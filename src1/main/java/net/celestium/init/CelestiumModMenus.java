
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.celestium.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import net.celestium.world.inventory.SacPetitGUIMenu;
import net.celestium.world.inventory.SacMoyenGUIMenu;
import net.celestium.world.inventory.SacADosMagiqueGUIMenu;
import net.celestium.world.inventory.QCMMacronGUIMenu;
import net.celestium.CelestiumMod;

public class CelestiumModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CelestiumMod.MODID);
	public static final RegistryObject<MenuType<SacMoyenGUIMenu>> SAC_MOYEN_GUI = REGISTRY.register("sac_moyen_gui", () -> IForgeMenuType.create(SacMoyenGUIMenu::new));
	public static final RegistryObject<MenuType<SacADosMagiqueGUIMenu>> SAC_A_DOS_MAGIQUE_GUI = REGISTRY.register("sac_a_dos_magique_gui", () -> IForgeMenuType.create(SacADosMagiqueGUIMenu::new));
	public static final RegistryObject<MenuType<SacPetitGUIMenu>> SAC_PETIT_GUI = REGISTRY.register("sac_petit_gui", () -> IForgeMenuType.create(SacPetitGUIMenu::new));
	public static final RegistryObject<MenuType<QCMMacronGUIMenu>> QCM_MACRON_GUI = REGISTRY.register("qcm_macron_gui", () -> IForgeMenuType.create(QCMMacronGUIMenu::new));
}
