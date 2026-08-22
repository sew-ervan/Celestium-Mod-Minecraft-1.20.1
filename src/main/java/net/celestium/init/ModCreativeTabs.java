package net.celestium.init;

import net.celestium.CelestiumMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Onglet creatif du mod.
 *
 * <p>C'est le principal point de rupture du portage depuis 1.19.2 : l'appel
 * {@code new Item.Properties().tab(...)}, utilise par chaque item genere par MCreator, a disparu.
 * Les onglets sont desormais un registre a part entiere et decident eux-memes de leur contenu.
 *
 * <p>L'onglet se remplit en parcourant {@link ModItems#ITEMS} : tout item ajoute au mod y apparait
 * sans qu'il faille penser a l'inscrire ici.
 */
public class ModCreativeTabs {

	public static final DeferredRegister<CreativeModeTab> TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CelestiumMod.MOD_ID);

	public static final RegistryObject<CreativeModeTab> CELESTIAL_UNIVERS = TABS.register("celestial_univers",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.celestium.celestial_univers"))
					.icon(() -> new ItemStack(ModItems.CELESTIUM_INGOT.get()))
					.displayItems((parameters, output) ->
							ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
					.build());

	private ModCreativeTabs() {
	}
}
