package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.feature.backpack.BackpackMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registre des menus.
 *
 * <p>Un seul type suffit pour les trois tailles de sac : le palier voyage dans le paquet
 * d'ouverture. Le mod d'origine enregistrait quatre types pour quatre interfaces presque
 * identiques.
 */
public class ModMenus {

	public static final DeferredRegister<MenuType<?>> MENUS =
			DeferredRegister.create(ForgeRegistries.MENU_TYPES, CelestiumMod.MOD_ID);

	public static final RegistryObject<MenuType<BackpackMenu>> BACKPACK =
			MENUS.register("backpack", () -> IForgeMenuType.create(BackpackMenu::new));

	private ModMenus() {
	}
}
