package net.celestium.feature.magie;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Un sort lancable par un joueur.
 *
 * <p>Ajouter un sort au mod se resume a implementer cette interface et a l'inscrire dans
 * {@link net.celestium.init.ModSpells}. Le mod d'origine codait chaque effet magique en dur dans
 * l'item ou l'entite qui le declenchait, sans notion partagee de cout, de temps de recharge ni
 * d'appartenance a un camp.
 */
public interface Spell {

	/** Identifiant unique, qui sert aussi de cle de traduction et de cle de recharge. */
	ResourceLocation id();

	/** Cout en energie celeste. */
	int manaCost();

	/** Temps de recharge en ticks. */
	int cooldownTicks();

	/**
	 * Camp exige pour lancer le sort, ou {@code null} si le sort est accessible a tous.
	 */
	default Faction requiredFaction() {
		return null;
	}

	/**
	 * Execute le sort. N'est appele que si le cout, la recharge et le camp ont deja ete verifies.
	 *
	 * @return vrai si le sort a produit son effet ; faux pour ne consommer ni energie ni recharge
	 */
	boolean cast(ServerPlayer caster, ServerLevel level);

	default Component displayName() {
		return Component.translatable("spell." + id().getNamespace() + "." + id().getPath());
	}
}
