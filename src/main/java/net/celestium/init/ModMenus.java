package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.feature.backpack.BackpackMenu;
import net.celestium.feature.enchant.CorruptedEnchantingMenu;
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

	/**
	 * La table corrompue.
	 *
	 * <p>Rien ne voyage a l'ouverture : le menu deduit tout de l'outil qu'on lui presente, donc le
	 * constructeur reseau se contente d'ignorer le tampon.
	 */
	public static final RegistryObject<MenuType<CorruptedEnchantingMenu>> CORRUPTED_ENCHANTING =
			MENUS.register("corrupted_enchanting",
					() -> IForgeMenuType.create((id, inventory, buffer) ->
							new CorruptedEnchantingMenu(id, inventory)));

	private ModMenus() {
	}

	/** Le bloc que le menu de la table exige d'avoir sous les yeux pour rester ouvert. */
	public static net.minecraft.world.level.block.Block tableBlock() {
		return ModBlocks.CORRUPTED_ENCHANTING_TABLE.get();
	}
}
