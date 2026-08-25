package net.celestium.gametest;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.ModTags;
import net.celestium.core.material.ModArmorMaterials;
import net.celestium.feature.celestium.ArmorSetEffects;
import net.celestium.feature.magie.Faction;
import net.celestium.feature.familiar.FennecFamiliar;
import net.celestium.feature.familiar.MiniDemonFamiliar;
import net.celestium.feature.familiar.MiniGuardianFamiliar;
import net.celestium.feature.mob.CelestialDragonEntity;
import net.celestium.feature.mob.UnicornEntity;
import net.celestium.feature.mob.CorruptedVillagerEntity;
import net.celestium.feature.mob.CorruptedVillagerTrades;
import net.celestium.feature.mob.DemonSwordsmanEntity;
import net.celestium.feature.cloak.CloakInvisibility;
import net.celestium.feature.corruption.DimensionMining;
import net.celestium.feature.darkmatter.DarkMatterAnchoring;
import net.celestium.feature.enchant.CorruptedEnchantingMenu;
import net.celestium.feature.enchant.ExcavationEnchantment;
import net.celestium.feature.enchant.TamerEnchantment;
import net.celestium.feature.enchant.ThunderstrikeEnchantment;
import net.celestium.feature.luckyblock.LuckyOutcome;
import net.celestium.feature.luckyblock.LuckyTier;
import net.celestium.feature.portal.CorruptedPortalFrameBlock;
import net.celestium.feature.portal.CorruptedPortalShape;
import net.celestium.feature.portal.DemonPortalShape;
import net.celestium.feature.portal.DemonPortalTravel;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModEnchantments;
import net.celestium.init.ModEntities;
import net.celestium.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;

/**
 * Tests en jeu du mod.
 *
 * <p>Se lancent avec {@code gradlew runGameTestServer}, qui echoue si un test echoue. Ils
 * s'executent tous dans une arene vide de trois blocs de cote, decrite par
 * {@code data/celestium/structures/gametest/empty.nbt}.
 */
@GameTestHolder(CelestiumMod.MOD_ID)
@PrefixGameTestTemplate(false)
public class CelestiumGameTests {

	/**
	 * Forge prefixe automatiquement l'espace de noms declare par {@code @GameTestHolder} :
	 * ce nom devient {@code celestium:empty} et pointe sur
	 * {@code data/celestium/structures/empty.nbt}. L'ecrire qualifie ici donnerait
	 * {@code celestium:celestium:empty}.
	 */
	private static final String ARENA = "empty";

	/**
	 * Le minerai rend des fragments, jamais rien.
	 *
	 * <p>Le mod d'origine n'avait pas de table de butin et rendait ses objets depuis
	 * {@code getDrops} : ce test aurait alors porte sur du code, pas sur une donnee.
	 */
	@GameTest(template = ARENA)
	public static void celestiumOreDropsFragments(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 1, 1);
		helper.setBlock(pos, ModBlocks.CELESTIUM_ORE.get());

		List<ItemStack> drops = dropsOf(helper, pos);

		helper.assertTrue(!drops.isEmpty(), "Le minerai de Celestium ne rend rien");
		helper.assertTrue(
				drops.stream().allMatch(stack -> stack.is(ModItems.CELESTIUM_FRAGMENT.get())),
				"Le minerai de Celestium rend autre chose que des fragments");
		helper.assertTrue(
				drops.stream().mapToInt(ItemStack::getCount).sum() >= 2,
				"Le minerai de Celestium rend moins de deux fragments");

		helper.succeed();
	}

	/**
	 * Les trois blocs chance se classent du plus chanceux au moins chanceux.
	 *
	 * <p>C'est la propriete qui definit l'echelle, et elle ne se lit pas dans une ligne : elle
	 * resulte de la somme des poids de chaque table. Rendre un mauvais coup plus rare ou ajouter
	 * une bonne issue peut la renverser sans qu'aucune intention n'ait ete exprimee.
	 */
	@GameTest(template = ARENA)
	public static void luckyBlocksRankFromLuckyToCursed(GameTestHelper helper) {
		double ordinary = LuckyTier.ORDINARY.fortuneRate(0.0F);
		double corrupted = LuckyTier.CORRUPTED.fortuneRate(0.0F);
		double demon = LuckyTier.DEMON.fortuneRate(0.0F);

		helper.assertTrue(ordinary > corrupted,
				"Le bloc chance ordinaire (" + percent(ordinary) + ") n'est pas plus chanceux que le"
						+ " corrompu (" + percent(corrupted) + ")");
		helper.assertTrue(corrupted > demon,
				"Le bloc chance corrompu (" + percent(corrupted) + ") n'est pas plus chanceux que celui"
						+ " du demon (" + percent(demon) + ")");

		helper.succeed();
	}

	/**
	 * La chance et la malchance jouent toutes deux sur le tirage.
	 *
	 * <p>C'est ce qui distingue ces blocs d'un simple tirage fixe : une potion de chance doit se
	 * ressentir, et l'effet de malchance aussi. Le test le verifie sur les trois paliers, car la
	 * qualite est declaree issue par issue et rien ne garantit qu'on ne l'oublie pas sur l'une.
	 */
	@GameTest(template = ARENA)
	public static void luckShiftsTheOdds(GameTestHelper helper) {
		for (LuckyTier tier : LuckyTier.values()) {
			double cursed = tier.fortuneRate(-1.0F);
			double plain = tier.fortuneRate(0.0F);
			double blessed = tier.fortuneRate(1.0F);

			helper.assertTrue(blessed > plain,
					"La chance n'ameliore pas le tirage de " + tier + " (" + percent(plain)
							+ " puis " + percent(blessed) + ")");
			helper.assertTrue(cursed < plain,
					"La malchance n'aggrave pas le tirage de " + tier + " (" + percent(plain)
							+ " puis " + percent(cursed) + ")");
		}

		helper.succeed();
	}

	/**
	 * Chaque evenement se declenche sans casser.
	 *
	 * <p>Vingt-deux effets touchent a autant de coins de l'API — creatures, projectiles, blocs
	 * tombants, explosions, constructions. Le compilateur en verifie les signatures, pas le
	 * comportement : une creature que le monde refuse de creer ou un tag vide passeraient la
	 * compilation et lanceraient une exception au premier bloc casse. Ce test les declenche tous.
	 *
	 * <p>Les deux issues explosives sont ecartees : leur code tient en un appel, et faire sauter
	 * neuf TNT dans une arene de test risquerait d'atteindre les arenes voisines.
	 */
	@GameTest(template = ARENA, timeoutTicks = 400)
	public static void everyLuckyOutcomeFires(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();
		BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
		RandomSource random = RandomSource.create(1234L);

		for (LuckyTier tier : LuckyTier.values()) {
			helper.assertTrue(!tier.outcomes().isEmpty(), "Le palier " + tier + " n'a aucune issue");

			for (LuckyOutcome outcome : tier.outcomes()) {
				helper.assertTrue(outcome.message() != null && !outcome.message().isEmpty(),
						"Une issue de " + tier + " n'annonce rien");

				if (outcome.message().endsWith(".tnt") || outcome.message().endsWith(".detonation")) {
					continue;
				}
				outcome.event().fire(helper.getLevel(), pos, player, random);
			}
		}

		helper.succeed();
	}

	/**
	 * L'anneau corrompu ne s'allume qu'au douzieme oeil.
	 *
	 * <p>La reconnaissance part du bloc qu'on vient de garnir et essaie tous les centres possibles.
	 * Le risque est qu'elle en accepte un incomplet — onze cadres et un trou passent tres bien
	 * inapercus a l'oeil nu quand on tourne autour.
	 */
	@GameTest(template = ARENA)
	public static void corruptedRingLightsOnlyWhenComplete(GameTestHelper helper) {
		BlockPos centre = new BlockPos(8, 1, 8);
		int[][] ring = CorruptedPortalShape.ringOffsets();

		BlockState filled = ModBlocks.CORRUPTED_PORTAL_FRAME.get().defaultBlockState()
				.setValue(CorruptedPortalFrameBlock.HAS_EYE, true);
		BlockState empty = ModBlocks.CORRUPTED_PORTAL_FRAME.get().defaultBlockState()
				.setValue(CorruptedPortalFrameBlock.HAS_EYE, false);

		// Onze yeux sur douze : rien ne doit s'ouvrir.
		for (int i = 0; i < ring.length; i++) {
			BlockPos where = centre.offset(ring[i][0], 0, ring[i][1]);
			helper.setBlock(where, i == 0 ? empty : filled);
		}

		BlockPos absoluteCentre = helper.absolutePos(centre);
		helper.assertFalse(
				CorruptedPortalShape.isComplete(helper.getLevel(), absoluteCentre),
				"Un anneau a onze yeux passe pour complet");

		// Le douzieme.
		helper.setBlock(centre.offset(ring[0][0], 0, ring[0][1]), filled);
		helper.assertTrue(
				CorruptedPortalShape.isComplete(helper.getLevel(), absoluteCentre),
				"Un anneau a douze yeux n'est pas reconnu");

		BlockPos found = CorruptedPortalShape.findCompleteRing(helper.getLevel(),
				helper.absolutePos(centre.offset(ring[0][0], 0, ring[0][1])));
		helper.assertTrue(absoluteCentre.equals(found),
				"L'anneau trouve n'est pas centre au bon endroit : " + found);

		helper.succeed();
	}

	/**
	 * Les deux enchantements du mod restent introuvables ailleurs qu'a la table corrompue.
	 *
	 * <p>C'etait la condition posee. Trois portes derobees existent en 1.20.1 — la table ordinaire,
	 * le troc avec un villageois, et les livres de coffre — et chacune se ferme par une methode
	 * distincte. En oublier une suffirait a rendre la table corrompue inutile.
	 */
	@GameTest(template = ARENA)
	public static void modEnchantmentsComeOnlyFromTheCorruptedTable(GameTestHelper helper) {
		for (Enchantment enchantment : List.of(
				ModEnchantments.TIMBER.get(),
				ModEnchantments.EXCAVATION.get(),
				ModEnchantments.VEIN_MINER.get(),
				ModEnchantments.HARVEST.get(),
				ModEnchantments.SMELTING.get(),
				ModEnchantments.MAGNETISM.get(),
				ModEnchantments.THUNDERSTRIKE.get(),
				ModEnchantments.MIDAS_CURSE.get(),
				ModEnchantments.TAMER.get())) {
			helper.assertFalse(enchantment.isDiscoverable(),
					enchantment.getDescriptionId() + " apparait sur une table d'enchantement ordinaire");
			helper.assertFalse(enchantment.isTradeable(),
					enchantment.getDescriptionId() + " s'achete a un villageois");
			helper.assertFalse(enchantment.isAllowedOnBooks(),
					enchantment.getDescriptionId() + " se trouve sur un livre");
		}

		helper.succeed();
	}

	/**
	 * Chaque enchantement ne s'applique qu'a l'outil qui le concerne.
	 *
	 * <p>La categorie du jeu de base reunit pioche, pelle, hache et houe sous « outil de creusement ».
	 * Sans filtre supplementaire, l'abattage se poserait sur une pelle et l'excavation sur une hache.
	 */
	@GameTest(template = ARENA)
	public static void enchantmentsAcceptOnlyTheirOwnTools(GameTestHelper helper) {
		Enchantment timber = ModEnchantments.TIMBER.get();
		Enchantment excavation = ModEnchantments.EXCAVATION.get();

		helper.assertTrue(timber.canEnchant(new ItemStack(Items.DIAMOND_AXE)),
				"L'abattage refuse une hache");
		helper.assertFalse(timber.canEnchant(new ItemStack(Items.DIAMOND_PICKAXE)),
				"L'abattage accepte une pioche");

		helper.assertTrue(excavation.canEnchant(new ItemStack(Items.DIAMOND_PICKAXE)),
				"L'excavation refuse une pioche");
		helper.assertTrue(excavation.canEnchant(new ItemStack(Items.DIAMOND_SHOVEL)),
				"L'excavation refuse une pelle");
		helper.assertFalse(excavation.canEnchant(new ItemStack(Items.DIAMOND_AXE)),
				"L'excavation accepte une hache");

		helper.succeed();
	}

	/**
	 * Les quatre paliers d'excavation donnent bien trois, cinq, sept et neuf blocs de cote.
	 *
	 * <p>Le carre se centre sur le bloc vise, donc son cote est toujours impair : neuf est le dernier
	 * palier sous la dizaine, et un cinquieme donnerait onze.
	 */
	@GameTest(template = ARENA)
	public static void excavationSquaresGrowByTwo(GameTestHelper helper) {
		int[] expected = {3, 5, 7, 9};

		for (int level = 1; level <= expected.length; level++) {
			int side = ExcavationEnchantment.sideFor(level);
			helper.assertTrue(side == expected[level - 1],
					"Le niveau " + level + " donne un carre de " + side + " au lieu de "
							+ expected[level - 1]);
			helper.assertTrue(ExcavationEnchantment.radiusFor(level) * 2 + 1 == side,
					"Le rayon du niveau " + level + " ne correspond pas a son cote");
		}

		helper.assertTrue(ModEnchantments.EXCAVATION.get().getMaxLevel() == expected.length,
				"L'excavation n'a pas quatre niveaux");

		helper.succeed();
	}

	/**
	 * Les baremes de l'Eclair fulgurant et du Dompteur.
	 *
	 * <p>Ce sont les deux seuls enchantements du mod dont l'effet se chiffre, et des chiffres ecrits
	 * dans un tableau se decalent sans bruit. Le test fige ceux qui ont ete demandes.
	 */
	@GameTest(template = ARENA)
	public static void combatEnchantmentOddsMatchTheirSpecification(GameTestHelper helper) {
		int[] againstMobs = {10, 20, 30};
		int[] againstPlayers = {3, 7, 13};

		for (int level = 1; level <= 3; level++) {
			int mob = ThunderstrikeEnchantment.chanceFor(level, false);
			int player = ThunderstrikeEnchantment.chanceFor(level, true);

			helper.assertTrue(mob == againstMobs[level - 1],
					"Eclair fulgurant niveau " + level + " : " + mob + " % sur une creature au lieu de "
							+ againstMobs[level - 1]);
			helper.assertTrue(player == againstPlayers[level - 1],
					"Eclair fulgurant niveau " + level + " : " + player + " % sur un joueur au lieu de "
							+ againstPlayers[level - 1]);
			helper.assertTrue(player < mob,
					"Eclair fulgurant frappe un joueur aussi souvent qu'une creature");
		}

		// Le Dompteur decroit avec la robustesse de la cible, et s'annule sur ce qui tient vraiment.
		int weak = TamerEnchantment.chanceFor(3, 20.0F);
		int sturdy = TamerEnchantment.chanceFor(3, 35.0F);

		helper.assertTrue(weak > sturdy,
				"Le Dompteur detourne une creature robuste aussi bien qu'une fragile");
		helper.assertTrue(TamerEnchantment.chanceFor(3, 40.0F) == 0,
				"Le Dompteur agit encore au-dela du seuil de resistance");
		helper.assertTrue(TamerEnchantment.chanceFor(1, 20.0F) < TamerEnchantment.chanceFor(3, 20.0F),
				"Les niveaux du Dompteur ne se distinguent pas");

		helper.succeed();
	}

	/**
	 * Le puits de gravite attire, et la parure complete de matiere noire annule les chutes.
	 *
	 * <p>Les deux promesses de la matiere noire, verifiees la ou elles se decident. Le puits est
	 * eprouve sur un objet lache a portee : s'il ne bouge pas, le bloc n'a aucune raison d'exister.
	 */
	@GameTest(template = ARENA, timeoutTicks = 200)
	public static void gravityWellDrawsItemsIn(GameTestHelper helper) {
		// Un sol, comme pour le demon epeiste : l'arene n'est que de l'air, et un objet lache dedans
		// tombe hors du monde sans avoir eu le temps de deriver. L'objet etait aussi pose hors de
		// l'arene, ou ce qu'il rencontrait dependait des tests voisins — donc du nombre de tests.
		for (int dx = 0; dx <= 5; dx++) {
			for (int dz = 0; dz <= 5; dz++) {
				helper.setBlock(new BlockPos(dx, 0, dz), Blocks.STONE);
			}
		}

		BlockPos well = new BlockPos(1, 1, 1);
		helper.setBlock(well, ModBlocks.GRAVITY_WELL.get());

		Vec3 centre = Vec3.atCenterOf(helper.absolutePos(well));
		ItemEntity dropped = helper.spawnItem(Items.DIAMOND, 5.0F, 1.5F, 5.0F);
		double start = dropped.position().distanceTo(centre);

		helper.succeedWhen(() -> {
			double now = dropped.position().distanceTo(centre);
			helper.assertTrue(now < start - 1.0,
					"Le puits de gravite n'attire pas : distance " + start + " puis " + now);
		});
	}

	/** La parure complete annule les chutes, une piece seule non. */
	@GameTest(template = ARENA)
	public static void darkMatterSetCountsAllFourPieces(GameTestHelper helper) {
		Player bare = helper.makeMockPlayer();
		helper.assertTrue(DarkMatterAnchoring.worn(bare) == 0,
				"Un joueur nu porte de la matiere noire");

		Player partial = helper.makeMockPlayer();
		partial.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.DARK_MATTER_BOOTS.get()));
		helper.assertTrue(DarkMatterAnchoring.worn(partial) == 1,
				"Une piece seule n'est pas comptee pour une");

		Player full = helper.makeMockPlayer();
		full.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.DARK_MATTER_HELMET.get()));
		full.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.DARK_MATTER_CHESTPLATE.get()));
		full.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.DARK_MATTER_LEGGINGS.get()));
		full.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.DARK_MATTER_BOOTS.get()));
		helper.assertTrue(DarkMatterAnchoring.worn(full) == 4,
				"La parure complete n'est pas reconnue");

		// Une parure d'un autre materiau ne doit pas passer pour de la matiere noire.
		Player other = helper.makeMockPlayer();
		other.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.CELESTIUM_CHESTPLATE.get()));
		helper.assertTrue(DarkMatterAnchoring.worn(other) == 0,
				"Le Celestium passe pour de la matiere noire");

		helper.succeed();
	}

	/**
	 * Le dragon celeste tient dans les airs et ne craint pas le jour.
	 *
	 * <p>Ce sont ses deux ecarts avec le phantasme dont il herite, et les deux qui le rendent
	 * jouable : un gardien qui prendrait feu au lever du soleil ne garderait rien.
	 */
	@GameTest(template = ARENA, timeoutTicks = 200)
	public static void celestialDragonIgnoresDaylight(GameTestHelper helper) {
		CelestialDragonEntity dragon = helper.spawn(ModEntities.CELESTIAL_DRAGON.get(), 8, 6, 8);

		helper.assertFalse(dragon.isSunBurnTick(),
				"Le dragon celeste brule au soleil comme un phantasme");
		helper.assertTrue(dragon.getMaxHealth() >= 150.0F,
				"Le dragon celeste n'a que " + dragon.getMaxHealth() + " points de vie");

		// Il vole : au bout de quelques secondes il ne doit pas s'etre ecrase au sol de l'arene.
		helper.runAtTickTime(60L, () -> {
			helper.assertTrue(dragon.isAlive(), "Le dragon celeste est mort dans une arene vide");
			helper.assertFalse(dragon.onGround(),
					"Le dragon celeste est tombe au sol au lieu de voler");
			helper.succeed();
		});
	}

	/** Le casque en Celestium accorde la vision nocturne des qu'il est porte. */
	@GameTest(template = ARENA)
	public static void celestiumHelmetGrantsNightVision(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();
		player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.CELESTIUM_HELMET.get()));

		ArmorSetEffects.applyFor(ModArmorMaterials.CELESTIUM, ArmorItem.Type.HELMET, player);

		helper.assertTrue(player.hasEffect(MobEffects.NIGHT_VISION),
				"Le casque en Celestium n'accorde pas la vision nocturne");

		// Le refactor tient dans ce point : un effet encore largement valide n'est pas reconstruit
		// a chaque tick. Les trois autres pieces le faisaient vingt fois par seconde.
		int durationBefore = player.getEffect(MobEffects.NIGHT_VISION).getDuration();
		ArmorSetEffects.applyFor(ModArmorMaterials.CELESTIUM, ArmorItem.Type.HELMET, player);
		int durationAfter = player.getEffect(MobEffects.NIGHT_VISION).getDuration();

		helper.assertTrue(durationAfter == durationBefore,
				"L'effet du casque est reapplique alors qu'il est encore valide");

		helper.succeed();
	}

	/**
	 * Les planches du bois du demon brulent.
	 *
	 * <p>MCreator surchargeait {@code getFlammability} dans chacune des dix classes de blocs ;
	 * Forge attend une declaration centralisee, faite au demarrage. Ce test verifie qu'elle a bien
	 * eu lieu.
	 */
	@GameTest(template = ARENA)
	public static void demonWoodBurns(GameTestHelper helper) {
		BlockState planks = ModBlocks.BOIS_DU_DEMON.planks.get().defaultBlockState();
		BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));

		// L'interrogation passe par l'extension Forge de BlockState, pas par le bloc de feu.
		helper.assertTrue(planks.getFlammability(helper.getLevel(), pos, Direction.UP) > 0,
				"Les planches du bois du demon ne sont pas inflammables");
		helper.assertTrue(planks.getFireSpreadSpeed(helper.getLevel(), pos, Direction.UP) > 0,
				"Le feu ne se propage pas au bois du demon");

		helper.succeed();
	}

	/**
	 * Le demon bascule en seconde phase sous la moitie de ses points de vie.
	 *
	 * <p>Le sol est pose explicitement : l'arene de test ne contient que de l'air, et une creature
	 * qui tombe dans le vide cesse de reflechir avant d'avoir pu changer de phase.
	 */
	@GameTest(template = ARENA, timeoutTicks = 200)
	public static void demonSwordsmanEntersSecondPhase(GameTestHelper helper) {
		// Un enclos, et pas un seul bloc. Sur un bloc isole, le demon finissait par en descendre —
		// de lui-meme ou pousse par une creature venue d'une arene voisine — et une creature tombee
		// dans le vide cesse de reflechir avant d'avoir pu changer de phase. Le test echouait alors
		// une fois sur plusieurs, sans que rien de son sujet ne soit en cause.
		for (int dx = 0; dx <= 4; dx++) {
			for (int dz = 0; dz <= 4; dz++) {
				helper.setBlock(new BlockPos(dx, 0, dz), Blocks.STONE);

				boolean onEdge = dx == 0 || dx == 4 || dz == 0 || dz == 4;
				if (onEdge) {
					helper.setBlock(new BlockPos(dx, 1, dz), Blocks.STONE);
					helper.setBlock(new BlockPos(dx, 2, dz), Blocks.STONE);
				}
			}
		}

		DemonSwordsmanEntity demon = helper.spawn(ModEntities.DEMON_SWORDSMAN.get(), 2, 1, 2);

		helper.assertTrue(!demon.isSecondPhase(),
				"Le demon commence deja en seconde phase");

		demon.setHealth(demon.getMaxHealth() * 0.4F);

		helper.succeedWhen(() -> {
			helper.assertTrue(demon.isSecondPhase(),
					"Le demon ne bascule pas en seconde phase sous la moitie de ses points de vie");
			helper.assertTrue(
					demon.getAttributeValue(Attributes.ATTACK_DAMAGE) > 14.0,
					"La seconde phase n'augmente pas les degats du demon");
		});
	}

	/** Le camp celeste est le seul a echapper a la magie celeste. */
	@GameTest(template = ARENA)
	public static void onlyCelestialFactionResistsCelestialMagic(GameTestHelper helper) {
		helper.assertTrue(Faction.DEMON.isTargetedByCelestialMagic(),
				"Le camp demon devrait etre une cible");
		helper.assertTrue(Faction.NEUTRE.isTargetedByCelestialMagic(),
				"Le camp neutre devrait etre une cible");
		helper.assertTrue(!Faction.CELESTE.isTargetedByCelestialMagic(),
				"Le camp celeste ne devrait jamais etre une cible");

		// Les anciennes valeurs numeriques doivent rester lisibles pour reprendre une sauvegarde.
		helper.assertTrue(Faction.fromLegacyValue(-1) == Faction.DEMON, "-1 devrait valoir demon");
		helper.assertTrue(Faction.fromLegacyValue(0) == Faction.NEUTRE, "0 devrait valoir neutre");
		helper.assertTrue(Faction.fromLegacyValue(1) == Faction.CELESTE, "1 devrait valoir celeste");

		helper.succeed();
	}

	/**
	 * Un cadre en blocs de Celestium s'allume et se remplit de surface de portail.
	 *
	 * <p>Le cadre est bati en dur : deux montants, un seuil et un linteau, pour un interieur de
	 * deux blocs de large sur trois de haut.
	 */
	@GameTest(template = ARENA)
	public static void celestiumFrameLightsAPortal(GameTestHelper helper) {
		int innerWidth = 2;
		int innerHeight = 3;
		BlockPos origin = new BlockPos(2, 1, 2);

		// Montants gauche et droit, seuil et linteau compris.
		for (int y = -1; y <= innerHeight; y++) {
			helper.setBlock(origin.offset(-1, y, 0), ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get());
			helper.setBlock(origin.offset(innerWidth, y, 0), ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get());
		}
		for (int x = 0; x < innerWidth; x++) {
			helper.setBlock(origin.offset(x, -1, 0), ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get());
			helper.setBlock(origin.offset(x, innerHeight, 0), ModBlocks.CORRUPTED_CELESTIUM_BLOCK.get());
		}

		DemonPortalShape shape =
				DemonPortalShape.find(helper.getLevel(), helper.absolutePos(origin));

		helper.assertTrue(shape != null, "Le cadre en blocs de Celestium n'est pas reconnu");
		shape.createPortal();

		for (int x = 0; x < innerWidth; x++) {
			for (int y = 0; y < innerHeight; y++) {
				helper.assertBlockPresent(ModBlocks.DEMON_PORTAL.get(), origin.offset(x, y, 0));
			}
		}

		// Casser un montant doit dissiper toute la surface.
		helper.setBlock(origin.offset(-1, 0, 0), Blocks.AIR);
		helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, origin));
	}

	/**
	 * L'attente devant le portail avance d'un cran par tick, pas par appel.
	 *
	 * <p>Un joueur debout dans un portail touche plusieurs blocs a la fois, et chacun signale sa
	 * presence. Le compteur avait ete ecrit comme si un seul le faisait : le deuxieme signal du
	 * meme tick passait pour une interruption et remettait tout a zero. L'attente plafonnait a un
	 * tick et le portail n'emportait jamais personne.
	 *
	 * <p>Le test simule quatre signaux par tick sur trois ticks : le compte doit valoir trois.
	 * Avec le defaut d'origine il valait un.
	 */
	@GameTest(template = ARENA, timeoutTicks = 120)
	public static void portalWarmupCountsOncePerTick(GameTestHelper helper) {
		ArmorStand stand = helper.spawn(EntityType.ARMOR_STAND, 1, 1, 1);
		int signalsPerTick = 4;
		int ticks = 3;

		for (int tick = 1; tick <= ticks; tick++) {
			helper.runAtTickTime(tick, () -> {
				for (int signal = 0; signal < signalsPerTick; signal++) {
					DemonPortalTravel.onEntityInPortal(stand);
				}
			});
		}

		helper.runAtTickTime(ticks + 1L, () -> {
			int warmup = DemonPortalTravel.warmupOf(stand);
			helper.assertTrue(warmup == ticks,
					"L'attente du portail vaut " + warmup + " apres " + ticks + " ticks");
			helper.succeed();
		});
	}

	/**
	 * Seuls les outils corrompus et demoniaques mordent la pierre des Terres du demon.
	 *
	 * <p>Le netherite sert de temoin : c'est le meilleur outil du jeu de base, et il doit echouer.
	 */
	@GameTest(template = ARENA)
	public static void onlyCorruptedAndDemonToolsBreak(GameTestHelper helper) {
		helper.assertTrue(DimensionMining.breaks(new ItemStack(ModItems.CORRUPTED_CELESTIUM_PICKAXE.get())),
				"La pioche en Celestium corrompu ne casse rien dans les Terres du demon");
		helper.assertTrue(DimensionMining.breaks(new ItemStack(ModItems.DEMONIUM_PICKAXE.get())),
				"La pioche en Demonium ne casse rien dans les Terres du demon");

		helper.assertFalse(DimensionMining.breaks(new ItemStack(Items.NETHERITE_PICKAXE)),
				"La pioche en netherite casse dans les Terres du demon");
		helper.assertFalse(DimensionMining.breaks(new ItemStack(ModItems.CELESTIUM_PICKAXE.get())),
				"La pioche en Celestium pur casse dans les Terres du demon");
		helper.assertFalse(DimensionMining.breaks(ItemStack.EMPTY),
				"Les mains nues cassent dans les Terres du demon");

		helper.succeed();
	}

	/**
	 * La pioche corrompue extrait bien le Demonium.
	 *
	 * <p>C'est la condition qui rend la panoplie de voyage utile : elle est le seul outillage
	 * emportable, et le Demonium est le seul minerai sur place. Si son palier etait mal classe
	 * face au diamant, le joueur arriverait equipe et repartirait bredouille.
	 */
	@GameTest(template = ARENA)
	public static void corruptedPickaxeMinesDemonium(GameTestHelper helper) {
		BlockState ore = ModBlocks.DEMONIUM_ORE.get().defaultBlockState();

		helper.assertTrue(
				new ItemStack(ModItems.CORRUPTED_CELESTIUM_PICKAXE.get()).isCorrectToolForDrops(ore),
				"La pioche en Celestium corrompu n'extrait pas le Demonium");
		helper.assertFalse(new ItemStack(Items.IRON_PICKAXE).isCorrectToolForDrops(ore),
				"La pioche en fer extrait le Demonium");

		helper.succeed();
	}

	/** Le demon laisse son coeur, a coup sur. */
	@GameTest(template = ARENA, timeoutTicks = 200)
	public static void demonSwordsmanDropsHeart(GameTestHelper helper) {
		helper.setBlock(new BlockPos(1, 0, 1), Blocks.STONE);

		DemonSwordsmanEntity demon = helper.spawn(ModEntities.DEMON_SWORDSMAN.get(), 1, 1, 1);
		demon.kill();

		helper.succeedWhen(() -> helper.assertItemEntityPresent(
				ModItems.DEMON_HEART.get(), new BlockPos(1, 1, 1), 6.0));
	}

	/**
	 * Les villageois corrompus ne reconnaissent que qui porte leur matiere.
	 *
	 * <p>C'est ce qui permet a une creature hostile de tenir boutique : sans armure on est charge,
	 * avec on est servi.
	 */
	@GameTest(template = ARENA)
	public static void corruptedVillagerRecognisesTheirOwn(GameTestHelper helper) {
		Player stranger = helper.makeMockPlayer();
		helper.assertFalse(CorruptedVillagerEntity.recognises(stranger),
				"Un joueur sans armure passe pour un des leurs");

		Player kin = helper.makeMockPlayer();
		kin.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.CORRUPTED_CELESTIUM_HELMET.get()));
		helper.assertTrue(CorruptedVillagerEntity.recognises(kin),
				"Un joueur en Celestium corrompu n'est pas reconnu");

		Player demonKin = helper.makeMockPlayer();
		demonKin.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.DEMONIUM_CHESTPLATE.get()));
		helper.assertTrue(CorruptedVillagerEntity.recognises(demonKin),
				"Un joueur en Demonium n'est pas reconnu");

		Player imposter = helper.makeMockPlayer();
		imposter.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.CELESTIUM_HELMET.get()));
		helper.assertFalse(CorruptedVillagerEntity.recognises(imposter),
				"Le Celestium pur suffit a se faire passer pour un des leurs");

		helper.succeed();
	}

	/**
	 * Le troc se paie en Demonium, sauf l'offre du coeur.
	 *
	 * <p>La monnaie doit se ramasser sur place : une devise apportee de l'Overworld ferait de leur
	 * boutique une extension du commerce d'a cote.
	 */
	@GameTest(template = ARENA)
	public static void corruptedVillagerTradesInDemonium(GameTestHelper helper) {
		MerchantOffers offers = CorruptedVillagerTrades.create(RandomSource.create(1234L));

		helper.assertTrue(!offers.isEmpty(), "Les villageois corrompus n'ont rien a echanger");

		long paidInDemonium = offers.stream()
				.filter(offer -> offer.getBaseCostA().is(ModItems.DEMONIUM_FRAGMENT.get()))
				.count();
		long paidInHearts = offers.stream()
				.filter(offer -> offer.getBaseCostA().is(ModItems.DEMON_HEART.get()))
				.count();

		helper.assertTrue(paidInDemonium + paidInHearts == offers.size(),
				"Une offre se paie dans une autre monnaie que le Demonium ou le coeur");
		helper.assertTrue(paidInHearts == 1,
				"Le coeur du demon n'ouvre pas exactement une offre");

		helper.succeed();
	}

	/**
	 * La cape se porte sur le torse, et n'est pas une piece d'armure.
	 *
	 * <p>Les deux points tiennent ensemble : c'est parce qu'elle n'est pas une piece d'armure que
	 * la couche de rendu l'ignore et qu'un porteur invisible le reste vraiment, et c'est le retour
	 * de {@code getEquipmentSlot} qui la fait malgre tout accepter dans l'emplacement du plastron.
	 * Faire de la cape un {@code ArmorItem} reglerait le second point en cassant le premier.
	 */
	@GameTest(template = ARENA)
	public static void cloakGoesOnTheChestWithoutBeingArmour(GameTestHelper helper) {
		ItemStack cloak = new ItemStack(ModItems.INVISIBILITY_CLOAK.get());

		helper.assertFalse(cloak.getItem() instanceof ArmorItem,
				"La cape est une piece d'armure : elle se dessinerait sur un porteur invisible");
		helper.assertTrue(Mob.getEquipmentSlotForItem(cloak) == EquipmentSlot.CHEST,
				"La cape ne va pas dans l'emplacement du plastron");

		Player wearer = helper.makeMockPlayer();
		wearer.setItemSlot(EquipmentSlot.CHEST, cloak);

		helper.assertTrue(CloakInvisibility.worn(wearer), "La cape portee n'est pas reconnue");
		helper.assertFalse(wearer.hasEffect(MobEffects.INVISIBILITY),
				"La cape passe par l'effet de potion : elle trainerait ses volutes");

		helper.succeed();
	}

	/**
	 * La table corrompue propose les quatre enchantements d'arc, et a n'importe quel arc.
	 *
	 * <p>Les quatre sont declares indecouvrables : la table corrompue est le seul endroit qui puisse
	 * les donner. Si elle ne les proposait pas, ils n'existeraient dans aucune partie.
	 */
	@GameTest(template = ARENA)
	public static void corruptedTableOffersTheBowEnchantments(GameTestHelper helper) {
		List<Enchantment> offers = CorruptedEnchantingMenu.offersFor(
				new ItemStack(ModItems.CELESTIAL_BOW.get()));

		helper.assertTrue(offers.contains(ModEnchantments.VOLLEY.get()),
				"La table ne propose pas la Salve celeste");
		helper.assertTrue(offers.contains(ModEnchantments.PIERCING_SHOT.get()),
				"La table ne propose pas le Transpercement");
		helper.assertTrue(offers.contains(ModEnchantments.SEEKER.get()),
				"La table ne propose pas le Traqueur");
		helper.assertTrue(offers.contains(ModEnchantments.COLLAPSE.get()),
				"La table ne propose pas l'Effondrement");

		// Un arc du jeu de base les recoit aussi : l'enchantement appartient a l'arc, pas au metal.
		helper.assertTrue(CorruptedEnchantingMenu.offersFor(new ItemStack(Items.BOW)).size() == 4,
				"Un arc ordinaire ne recoit pas les quatre enchantements");

		// Et rien de tout cela sur autre chose qu'un arc.
		helper.assertFalse(
				CorruptedEnchantingMenu.offersFor(new ItemStack(ModItems.CELESTIUM_SWORD.get()))
						.contains(ModEnchantments.VOLLEY.get()),
				"Une epee recoit un enchantement d'arc");

		helper.succeed();
	}

	/**
	 * La Salve celeste met des fleches en plus en l'air, et aucune ne se ramasse.
	 *
	 * <p>La seconde moitie compte autant que la premiere : des fleches ajoutees ramassables
	 * feraient de l'enchantement une facon de fabriquer des fleches a partir d'une seule.
	 */
	@GameTest(template = ARENA)
	public static void celestialVolleyAddsArrowsThatCannotBeGathered(GameTestHelper helper) {
		Vec3 origin = helper.absoluteVec(new Vec3(1.5, 2.0, 1.5));

		Player archer = helper.makeMockPlayer();
		archer.setPos(origin.x, origin.y, origin.z);
		archer.setItemInHand(InteractionHand.MAIN_HAND,
				enchanted(ModItems.CELESTIAL_BOW.get(), ModEnchantments.VOLLEY.get(), 3));

		Arrow shot = new Arrow(helper.getLevel(), origin.x, origin.y, origin.z);
		shot.setOwner(archer);
		shot.setDeltaMovement(0.0, 0.0, 1.0);
		shot.pickup = AbstractArrow.Pickup.ALLOWED;

		helper.getLevel().addFreshEntity(shot);

		// La boite est serree : les quatre fleches partent du meme point, et une arene voisine ne
		// doit pas pouvoir compter dans ce test.
		List<Arrow> volley = helper.getLevel().getEntitiesOfClass(
				Arrow.class, shot.getBoundingBox().inflate(1.0));

		helper.assertTrue(volley.size() == 4,
				"La salve de niveau trois ne met pas quatre fleches en l'air, mais " + volley.size());

		long gatherable = volley.stream()
				.filter(arrow -> arrow.pickup == AbstractArrow.Pickup.ALLOWED)
				.count();
		helper.assertTrue(gatherable == 1,
				"La salve rend " + gatherable + " fleches ramassables au lieu d'une");

		// Les fleches ajoutees s'ecartent : trois fleches sur la meme trajectoire ne seraient
		// qu'une fleche plus lourde.
		long straightAhead = volley.stream()
				.filter(arrow -> Math.abs(arrow.getDeltaMovement().x) < 1.0E-6)
				.count();
		helper.assertTrue(straightAhead == 1,
				"La salve ne s'ecarte pas : " + straightAhead + " fleches vont tout droit");

		helper.succeed();
	}

	/** Le Transpercement se reporte sur la fleche au moment du tir. */
	@GameTest(template = ARENA)
	public static void piercingShotCarriesOverToTheArrow(GameTestHelper helper) {
		Vec3 origin = helper.absoluteVec(new Vec3(1.5, 2.0, 1.5));

		Player archer = helper.makeMockPlayer();
		archer.setPos(origin.x, origin.y, origin.z);
		archer.setItemInHand(InteractionHand.MAIN_HAND,
				enchanted(ModItems.CELESTIAL_BOW.get(), ModEnchantments.PIERCING_SHOT.get(), 2));

		Arrow shot = new Arrow(helper.getLevel(), origin.x, origin.y, origin.z);
		shot.setOwner(archer);
		shot.setDeltaMovement(0.0, 0.0, 1.0);

		helper.assertTrue(shot.getPierceLevel() == 0,
				"La fleche traverse deja avant d'etre tiree");

		helper.getLevel().addFreshEntity(shot);

		helper.assertTrue(shot.getPierceLevel() == 2,
				"Le Transpercement de niveau deux ne se reporte pas sur la fleche");

		helper.succeed();
	}

	/**
	 * Le Traqueur rattrape une visee approximative.
	 *
	 * <p>La fleche part six degres a cote d'un cochon, ce qui reste dans le cone du premier niveau,
	 * et doit arriver alignee sur lui. La creature est posee sur son propre sol : sans lui elle
	 * tombe, et une cible en chute libre n'est plus a l'endroit ou le test la croit.
	 */
	@GameTest(template = ARENA)
	public static void seekerCorrectsAnApproximateAim(GameTestHelper helper) {
		for (int dx = 0; dx <= 3; dx++) {
			for (int dz = 0; dz <= 3; dz++) {
				helper.setBlock(new BlockPos(dx, 0, dz), Blocks.STONE);
			}
		}

		Pig target = helper.spawn(EntityType.PIG, 3, 1, 3);
		Vec3 origin = helper.absoluteVec(new Vec3(0.5, 1.5, 0.5));

		Player archer = helper.makeMockPlayer();
		archer.setPos(origin.x, origin.y, origin.z);
		archer.setItemInHand(InteractionHand.MAIN_HAND,
				enchanted(ModItems.CELESTIAL_BOW.get(), ModEnchantments.SEEKER.get(), 1));

		Vec3 toTarget = target.getEyePosition().subtract(origin).normalize();
		Vec3 askew = turned(toTarget, 6.0);

		Arrow shot = new Arrow(helper.getLevel(), origin.x, origin.y, origin.z);
		shot.setOwner(archer);
		shot.setDeltaMovement(askew.scale(2.0));

		helper.getLevel().addFreshEntity(shot);

		double alignment = shot.getDeltaMovement().normalize().dot(toTarget);
		helper.assertTrue(alignment > 0.99,
				"Le Traqueur ne corrige pas la visee : alignement " + alignment);
		helper.assertTrue(Math.abs(shot.getDeltaMovement().length() - 2.0) < 0.01,
				"Le Traqueur change la vitesse de la fleche au lieu de sa seule direction");

		helper.succeed();
	}

	/** Un exemplaire portant un seul enchantement, a un niveau donne. */
	private static ItemStack enchanted(Item item, Enchantment enchantment, int level) {
		ItemStack stack = new ItemStack(item);
		stack.enchant(enchantment, level);
		return stack;
	}

	/** Fait tourner une direction autour de la verticale, en degres. */
	private static Vec3 turned(Vec3 direction, double degrees) {
		double radians = Math.toRadians(degrees);
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);

		return new Vec3(
				direction.x * cos - direction.z * sin,
				direction.y,
				direction.x * sin + direction.z * cos);
	}

	/**
	 * La licorne va plus vite que le plus rapide des chevaux.
	 *
	 * <p>C'est sa seule defense, et donc la seule chose qui la rende difficile a prendre. Une
	 * licorne qu'on rattraperait au galop d'un cheval ordinaire ne serait qu'un cheval blanc.
	 */
	@GameTest(template = ARENA)
	public static void unicornOutrunsEveryHorse(GameTestHelper helper) {
		// Le cheval le plus rapide que le jeu de base sache tirer au sort.
		double fastestHorse = 0.3375;

		UnicornEntity unicorn = helper.spawn(ModEntities.UNICORN.get(), 1, 2, 1);
		double speed = unicorn.getAttributeValue(Attributes.MOVEMENT_SPEED);

		helper.assertTrue(speed > fastestHorse,
				"La licorne ne distance pas un cheval : " + speed + " contre " + fastestHorse);
		helper.assertFalse(unicorn.isTamed(), "Une licorne sauvage arrive deja domptee");

		helper.succeed();
	}

	/**
	 * Deux licornes se valent.
	 *
	 * <p>Le jeu de base fait varier chaque cheval, ce qui a du sens quand on en eleve un troupeau.
	 * Il n'y a qu'une sorte de licorne : si elles differaient, on serait tente d'en abattre
	 * plusieurs pour comparer, ce qui n'est pas le jeu qu'on veut.
	 */
	@GameTest(template = ARENA)
	public static void unicornsAreAllAlike(GameTestHelper helper) {
		UnicornEntity first = helper.spawn(ModEntities.UNICORN.get(), 1, 2, 1);
		UnicornEntity second = helper.spawn(ModEntities.UNICORN.get(), 2, 2, 2);

		// La mise en place est le moment ou le jeu de base tire les caracteristiques d'un cheval au
		// sort : c'est donc elle qu'il faut declencher pour verifier qu'ici, elle ne tire rien.
		BlockPos where = helper.absolutePos(new BlockPos(1, 2, 1));
		first.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(where),
				MobSpawnType.NATURAL, null, null);
		second.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(where),
				MobSpawnType.NATURAL, null, null);

		helper.assertTrue(
				first.getAttributeValue(Attributes.MOVEMENT_SPEED)
						== second.getAttributeValue(Attributes.MOVEMENT_SPEED),
				"Deux licornes n'ont pas la meme vitesse");
		helper.assertTrue(first.getMaxHealth() == second.getMaxHealth(),
				"Deux licornes n'ont pas la meme sante");

		helper.succeed();
	}

	/**
	 * Chaque familier ne mange que ce que produit son monde.
	 *
	 * <p>C'est ce qui rattache le compagnon a l'endroit ou on le trouve. Un familier qu'on
	 * apprivoiserait avec du ble se ramasserait au passage, sans qu'on ait rien fait pour lui.
	 */
	@GameTest(template = ARENA)
	public static void familiarsEatWhatTheirWorldProduces(GameTestHelper helper) {
		FennecFamiliar fennec = helper.spawn(ModEntities.FENNEC.get(), 1, 2, 1);
		helper.assertTrue(fennec.isFood(new ItemStack(Items.RABBIT)),
				"Le fennec refuse le lapin");
		helper.assertFalse(fennec.isFood(new ItemStack(Items.WHEAT)),
				"Le fennec se laisse apprivoiser au ble");

		MiniGuardianFamiliar guardian = helper.spawn(ModEntities.MINI_GUARDIAN.get(), 2, 2, 1);
		helper.assertTrue(
				guardian.isFood(new ItemStack(ModItems.CORRUPTED_CELESTIUM_FRAGMENT.get())),
				"Le petit gardien refuse le Celestium corrompu");
		helper.assertFalse(guardian.isFood(new ItemStack(ModItems.DEMONIUM_FRAGMENT.get())),
				"Le petit gardien mange la matiere d'une autre dimension");

		MiniDemonFamiliar demon = helper.spawn(ModEntities.MINI_DEMON.get(), 1, 2, 2);
		helper.assertTrue(demon.isFood(new ItemStack(ModItems.DEMONIUM_FRAGMENT.get())),
				"Le petit demon refuse le Demonium");
		helper.assertFalse(demon.isFood(new ItemStack(ModItems.CELESTIUM_INGOT.get())),
				"Le petit demon mange du Celestium pur");

		helper.succeed();
	}

	/**
	 * Un familier dompte obeit a son maitre, et a lui seul.
	 *
	 * <p>La main tendue par son maitre l'assied ou le releve ; celle d'un inconnu ne fait rien. Sans
	 * cette seconde regle, n'importe qui pourrait clouer sur place le compagnon d'un autre.
	 */
	@GameTest(template = ARENA)
	public static void tamedFamiliarObeysItsOwnerAlone(GameTestHelper helper) {
		FennecFamiliar fennec = helper.spawn(ModEntities.FENNEC.get(), 1, 2, 1);
		Player owner = helper.makeMockPlayer();
		Player stranger = helper.makeMockPlayer();

		helper.assertFalse(fennec.isTame(), "Le fennec arrive deja apprivoise");

		fennec.tame(owner);
		helper.assertTrue(fennec.isTame(), "Le fennec ne retient pas qu'il a ete apprivoise");
		helper.assertTrue(fennec.obeys(owner), "Le fennec ne reconnait pas son maitre");
		helper.assertFalse(fennec.obeys(stranger), "Le fennec appartient a tout le monde");

		fennec.mobInteract(owner, InteractionHand.MAIN_HAND);
		helper.assertTrue(fennec.isOrderedToSit(), "Le fennec refuse de s'asseoir");

		fennec.mobInteract(stranger, InteractionHand.MAIN_HAND);
		helper.assertTrue(fennec.isOrderedToSit(),
				"Un inconnu remet debout le compagnon d'un autre");

		fennec.mobInteract(owner, InteractionHand.MAIN_HAND);
		helper.assertFalse(fennec.isOrderedToSit(), "Le fennec refuse de se relever");

		// Un compagnon ne disparait pas parce qu'on s'en est eloigne.
		helper.assertFalse(fennec.removeWhenFarAway(4096.0),
				"Un fennec apprivoise finit par s'evaporer");

		helper.succeed();
	}

	private static String percent(double rate) {
		return Math.round(rate * 100.0) + " %";
	}

	/** Butin d'un bloc casse a mains nues, sans Fortune ni Toucher de soie. */
	private static List<ItemStack> dropsOf(GameTestHelper helper, BlockPos relativePos) {
		BlockPos absolute = helper.absolutePos(relativePos);
		BlockState state = helper.getLevel().getBlockState(absolute);

		LootParams.Builder params = new LootParams.Builder(helper.getLevel())
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(absolute))
				.withParameter(LootContextParams.TOOL, ItemStack.EMPTY);

		return state.getDrops(params);
	}
}
