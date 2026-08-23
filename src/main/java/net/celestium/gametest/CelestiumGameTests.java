package net.celestium.gametest;

import net.celestium.CelestiumMod;
import net.celestium.core.registry.ModTags;
import net.celestium.core.material.ModArmorMaterials;
import net.celestium.feature.celestium.ArmorSetEffects;
import net.celestium.feature.magie.Faction;
import net.celestium.feature.mob.CorruptedVillagerEntity;
import net.celestium.feature.mob.CorruptedVillagerTrades;
import net.celestium.feature.mob.DemonSwordsmanEntity;
import net.celestium.feature.corruption.DimensionMining;
import net.celestium.feature.portal.DemonPortalShape;
import net.celestium.feature.portal.DemonPortalTravel;
import net.celestium.init.ModBlocks;
import net.celestium.init.ModEntities;
import net.celestium.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
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

	/** Chaque bloc chance rend exactement un objet, tresor ou rebut, jamais lui-meme. */
	@GameTest(template = ARENA)
	public static void luckyBlockDropsOneItem(GameTestHelper helper) {
		assertSingleDraw(helper, ModBlocks.LUCKY_BLOCK.get(), ModTags.Items.LUCKY_BLOCK_REWARDS);
		assertSingleDraw(helper, ModBlocks.CORRUPTED_LUCKY_BLOCK.get(),
				ModTags.Items.CORRUPTED_LUCKY_BLOCK_REWARDS);
		assertSingleDraw(helper, ModBlocks.DEMON_LUCKY_BLOCK.get(),
				ModTags.Items.DEMON_LUCKY_BLOCK_REWARDS);

		helper.succeed();
	}

	/**
	 * Les trois blocs chance se classent du plus chanceux au moins chanceux.
	 *
	 * <p>C'est la seule propriete qui compte vraiment, et elle ne se lit pas dans les poids : elle
	 * depend a la fois de ceux-ci et du nombre d'items declares dans chaque tag. Ajouter une
	 * recompense au bloc du demon le rendrait plus chanceux que le corrompu sans qu'aucune ligne
	 * de reglage n'ait bouge — ce test le verrait.
	 *
	 * <p>Le tirage est repete : sur trois cents essais, l'ecart entre quatre-vingts pour cent et
	 * dix-sept ne tient pas du hasard.
	 */
	@GameTest(template = ARENA)
	public static void luckyBlocksRankFromLuckyToCursed(GameTestHelper helper) {
		double ordinary = treasureRate(helper, ModBlocks.LUCKY_BLOCK.get(),
				ModTags.Items.LUCKY_BLOCK_REWARDS);
		double corrupted = treasureRate(helper, ModBlocks.CORRUPTED_LUCKY_BLOCK.get(),
				ModTags.Items.CORRUPTED_LUCKY_BLOCK_REWARDS);
		double demon = treasureRate(helper, ModBlocks.DEMON_LUCKY_BLOCK.get(),
				ModTags.Items.DEMON_LUCKY_BLOCK_REWARDS);

		helper.assertTrue(ordinary > corrupted,
				"Le bloc chance ordinaire (" + percent(ordinary) + ") n'est pas plus chanceux que le"
						+ " corrompu (" + percent(corrupted) + ")");
		helper.assertTrue(corrupted > demon,
				"Le bloc chance corrompu (" + percent(corrupted) + ") n'est pas plus chanceux que celui"
						+ " du demon (" + percent(demon) + ")");

		helper.succeed();
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
		helper.setBlock(new BlockPos(1, 0, 1), Blocks.STONE);

		DemonSwordsmanEntity demon = helper.spawn(ModEntities.DEMON_SWORDSMAN.get(), 1, 1, 1);

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

	/** Nombre de tirages servant a estimer la chance d'un bloc. */
	private static final int DRAWS = 300;

	/** Verifie qu'un bloc chance rend une seule chose, prise dans son tresor ou dans les rebuts. */
	private static void assertSingleDraw(GameTestHelper helper, Block block, TagKey<Item> treasure) {
		BlockPos pos = new BlockPos(1, 1, 1);
		helper.setBlock(pos, block);

		List<ItemStack> drops = dropsOf(helper, pos);

		helper.assertTrue(drops.size() == 1,
				block.getName().getString() + " a rendu " + drops.size() + " objets au lieu d'un seul");
		helper.assertTrue(
				drops.get(0).is(treasure) || drops.get(0).is(ModTags.Items.LUCKY_BLOCK_JUNK),
				block.getName().getString() + " a rendu un objet absent de ses deux listes");
		helper.assertTrue(!drops.get(0).is(block.asItem()),
				block.getName().getString() + " se rend lui-meme");
	}

	/** Proportion de tirages qui donnent un tresor plutot qu'un rebut. */
	private static double treasureRate(GameTestHelper helper, Block block, TagKey<Item> treasure) {
		BlockPos pos = new BlockPos(1, 1, 1);
		helper.setBlock(pos, block);

		int treasures = 0;
		for (int draw = 0; draw < DRAWS; draw++) {
			List<ItemStack> drops = dropsOf(helper, pos);
			if (drops.size() == 1 && drops.get(0).is(treasure)) {
				treasures++;
			}
		}
		return (double) treasures / DRAWS;
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
