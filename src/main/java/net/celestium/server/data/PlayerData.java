package net.celestium.server.data;

import net.celestium.feature.magie.Faction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Donnees persistantes d'un joueur.
 *
 * <p>Reprise des variables generees par MCreator, avec quatre corrections de fond :
 * <ul>
 *   <li>{@code HomeX}, {@code HomeY}, {@code HomeZ}, {@code HomeHasBeenSet} et
 *       {@code HomeDimension} — cette derniere une chaine valant "Surface", "Nether" ou "End" —
 *       deviennent un unique {@link GlobalPos} eventuellement absent ;</li>
 *   <li>{@code TempPlayer1/2/3} disparaissent : c'etait la position retenue le temps d'une
 *       teleportation differee, une donnee de quelques secondes qui n'avait rien a faire dans une
 *       sauvegarde. Elle vit desormais en memoire, dans {@link WarmupTeleport} ;</li>
 *   <li>{@code magie_equipe}, un {@code double}, devient un {@link Faction} ;</li>
 *   <li>{@code JourRTP}, une date au format texte reformatee a chaque comparaison, devient un
 *       numero de jour ;</li>
 * </ul>
 *
 * <p>{@code TPA_Joueur}, {@code Commande_A_Decider} et {@code TempStringPlayerPersistent} sont
 * supprimees : elles n'etaient lues nulle part.
 */
public class PlayerData {

	private static final String KEY_HOME_DIMENSION = "HomeDimension";
	private static final String KEY_HOME_POS = "HomePos";
	private static final String KEY_FACTION = "Faction";
	private static final String KEY_LAST_RTP_DAY = "LastRtpDay";

	/** Ancien nom du champ de camp, lu une derniere fois pour reprendre les sauvegardes 1.19.2. */
	private static final String LEGACY_KEY_TEAM = "magie_equipe";

	@Nullable
	private GlobalPos home;

	private Faction faction = Faction.getDefault();

	/** Jour du dernier {@code /rtp}, en jours depuis l'epoque. -1 si jamais utilise. */
	private long lastRtpDay = -1L;

	public Optional<GlobalPos> getHome() {
		return Optional.ofNullable(this.home);
	}

	public void setHome(@Nullable GlobalPos home) {
		this.home = home;
	}

	public Faction getFaction() {
		return this.faction;
	}

	public void setFaction(Faction faction) {
		this.faction = faction;
	}

	public long getLastRtpDay() {
		return this.lastRtpDay;
	}

	public void setLastRtpDay(long day) {
		this.lastRtpDay = day;
	}

	/** Recopie l'integralite des donnees, a la mort du joueur ou au changement de dimension. */
	public void copyFrom(PlayerData other) {
		this.home = other.home;
		this.faction = other.faction;
		this.lastRtpDay = other.lastRtpDay;
	}

	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		if (this.home != null) {
			tag.putString(KEY_HOME_DIMENSION, this.home.dimension().location().toString());
			tag.put(KEY_HOME_POS, NbtUtils.writeBlockPos(this.home.pos()));
		}
		tag.putString(KEY_FACTION, this.faction.getSerializedName());
		tag.putLong(KEY_LAST_RTP_DAY, this.lastRtpDay);
		return tag;
	}

	public void load(CompoundTag tag) {
		if (tag.contains(KEY_HOME_DIMENSION) && tag.contains(KEY_HOME_POS)) {
			ResourceLocation dimensionId = ResourceLocation.tryParse(tag.getString(KEY_HOME_DIMENSION));
			if (dimensionId != null) {
				BlockPos pos = NbtUtils.readBlockPos(tag.getCompound(KEY_HOME_POS));
				this.home = GlobalPos.of(ResourceKey.create(Registries.DIMENSION, dimensionId), pos);
			}
		} else {
			this.home = null;
		}

		if (tag.contains(KEY_FACTION)) {
			this.faction = Faction.byName(tag.getString(KEY_FACTION));
		} else if (tag.contains(LEGACY_KEY_TEAM)) {
			this.faction = Faction.fromLegacyValue(tag.getDouble(LEGACY_KEY_TEAM));
		} else {
			this.faction = Faction.getDefault();
		}

		this.lastRtpDay = tag.getLong(KEY_LAST_RTP_DAY);
	}

	/** Vrai si le joueur a deja utilise le {@code /rtp} pendant la journee indiquee. */
	public boolean hasUsedRtpOn(long day) {
		return this.lastRtpDay == day;
	}

	/** Dimension du home, ou {@link Level#OVERWORLD} par defaut. */
	public ResourceKey<Level> getHomeDimensionOrOverworld() {
		return this.home != null ? this.home.dimension() : Level.OVERWORLD;
	}
}
