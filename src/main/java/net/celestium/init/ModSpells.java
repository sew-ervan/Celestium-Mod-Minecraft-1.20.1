package net.celestium.init;

import net.celestium.feature.magie.Spell;
import net.celestium.feature.magie.SpellRegistry;
import net.celestium.feature.magie.spells.CelestialBoltSpell;
import net.celestium.feature.magie.spells.CelestialStrikeSpell;

/**
 * Sorts du mod.
 *
 * <p>Ajouter un sort : ecrire une classe qui implemente {@link Spell}, puis ajouter une ligne ici.
 */
public class ModSpells {

	public static final Spell CELESTIAL_STRIKE = SpellRegistry.register(new CelestialStrikeSpell());
	public static final Spell CELESTIAL_BOLT = SpellRegistry.register(new CelestialBoltSpell());

	private ModSpells() {
	}

	/** Force le chargement de la classe, et donc l'inscription des sorts. */
	public static void init() {
	}
}
