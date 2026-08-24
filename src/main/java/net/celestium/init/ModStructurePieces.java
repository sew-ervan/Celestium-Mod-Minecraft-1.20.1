package net.celestium.init;

import net.celestium.CelestiumMod;
import net.celestium.worldgen.sanctum.CorruptedSanctumPiece;
import net.celestium.worldgen.village.DemonVillagePiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Types de morceaux de structure.
 *
 * <p>Chaque morceau doit etre enregistre pour pouvoir etre relu depuis la sauvegarde : sans cela
 * un village genere disparaitrait au rechargement du monde.
 */
public class ModStructurePieces {

	public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES =
			DeferredRegister.create(Registries.STRUCTURE_PIECE, CelestiumMod.MOD_ID);

	public static final RegistryObject<StructurePieceType> DEMON_VILLAGE =
			STRUCTURE_PIECES.register("demon_village",
					() -> (StructurePieceType.ContextlessType) DemonVillagePiece::new);

	public static final RegistryObject<StructurePieceType> CORRUPTED_SANCTUM =
			STRUCTURE_PIECES.register("corrupted_sanctum",
					() -> (StructurePieceType.ContextlessType) CorruptedSanctumPiece::new);

	private ModStructurePieces() {
	}
}
