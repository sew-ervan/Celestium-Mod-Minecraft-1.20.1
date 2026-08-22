package net.celestium.init;

import net.celestium.CelestiumMod;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * Essences de bois du mod.
 *
 * <p>La 1.20.1 a rendu {@link BlockSetType} et {@link WoodType} obligatoires : un bouton, une
 * plaque de pression ou un portillon ne peuvent plus etre construits sans, car ce sont eux qui
 * portent les sons d'ouverture et de fermeture. MCreator, genere pour la 1.19.2, ne les connait
 * pas — c'est l'un des points de rupture du portage.
 *
 * <p>Ces valeurs doivent etre initialisees avant la construction du moindre bloc concerne.
 */
public final class ModWoodTypes {

	public static final BlockSetType BOIS_DU_DEMON_SET =
			BlockSetType.register(new BlockSetType(CelestiumMod.MOD_ID + ":bois_du_demon"));

	public static final WoodType BOIS_DU_DEMON =
			WoodType.register(new WoodType(CelestiumMod.MOD_ID + ":bois_du_demon", BOIS_DU_DEMON_SET));

	private ModWoodTypes() {
	}
}
