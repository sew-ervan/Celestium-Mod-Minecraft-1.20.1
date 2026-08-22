package net.celestium.feature.magie;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Registre des sorts, indexe par identifiant. */
public final class SpellRegistry {

	private static final Map<ResourceLocation, Spell> SPELLS = new LinkedHashMap<>();

	private SpellRegistry() {
	}

	public static <T extends Spell> T register(T spell) {
		Spell previous = SPELLS.putIfAbsent(spell.id(), spell);
		if (previous != null) {
			throw new IllegalStateException("Deux sorts partagent l'identifiant " + spell.id());
		}
		return spell;
	}

	@Nullable
	public static Spell get(ResourceLocation id) {
		return SPELLS.get(id);
	}

	public static Collection<Spell> all() {
		return Collections.unmodifiableCollection(SPELLS.values());
	}
}
