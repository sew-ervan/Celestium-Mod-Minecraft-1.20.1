# Celestium

Mod Minecraft du serveur **Celestial Univers**, pour **Forge 1.20.1**.

Réécrit intégralement en code à la main depuis une base générée par MCreator 2023.2 (Forge 1.19.2).

## Prérequis

Un **JDK 17**. Temurin est celui qu'utilise Forge :

```bash
winget install --id EclipseAdoptium.Temurin.17.JDK -e
```

Vérifie dans un terminal neuf que `javac -version` répond `javac 17.x`.

## Commandes

| Commande | Effet |
|---|---|
| `gradlew build` | Compile et produit le jar dans `build/libs/` |
| `gradlew runClient` | Lance un client Minecraft avec le mod |
| `gradlew runServer` | Lance un serveur dédié |
| `gradlew runData` | Régénère `src/generated/resources/` |
| `gradlew runGameTestServer` | Exécute les tests en jeu |

**`gradlew runData` est à relancer après toute modification d'un bloc, d'un item, d'une recette ou d'une traduction.** L'intégration continue échoue si `src/generated/` n'est pas à jour.

## Architecture

```
src/main/java/net/celestium/
├── CelestiumMod.java     Point d'entrée, branche les registres
├── init/                 Tous les DeferredRegister
├── core/                 Matériaux, réseau, helpers d'enregistrement, tags
├── feature/              Code métier, groupé par fonctionnalité
├── server/               Module « essentials » : données joueur, commandes
├── client/               Tout ce qui est côté client uniquement
├── worldgen/             Filons et modificateurs de biome
├── datagen/              Providers DataGen
└── gametest/             Tests en jeu
```

Deux règles à tenir :

- **Aucune classe chargée sur un serveur dédié ne doit toucher `client/` directement.** Un serveur dédié n'a pas les classes clientes et planterait au chargement. La seule exception admise est le passage par `DistExecutor`, qui diffère le chargement de classe — voir `server/data/PlayerDataSyncPacket`. `ClientSetup` reste le point d'entrée client.
- **Ne jamais éditer `src/generated/` à la main.** Ce dossier est réécrit par `runData`.

## Ajouter du contenu

**Un item** — une ligne dans `init/ModItems.java`, une ligne dans `datagen/ModItemModelProvider.java`, une traduction dans chacun des deux `Mod*Provider`, une texture dans `assets/celestium/textures/item/`. Il apparaît automatiquement dans l'onglet créatif.

**Une essence de bois** — une déclaration `new WoodSet(...)` dans `init/ModBlocks.java` produit les dix blocs, leurs items et leur inflammabilité. Il reste à les brancher dans les providers de modèles, tags, butin et recettes.

**Un sort** — une classe qui implémente `feature/magie/Spell`, puis une ligne dans `init/ModSpells.java`. Le coût en énergie, le temps de recharge et le camp requis sont vérifiés par `SpellCaster`, pas par le sort.

**Un son** — le `.ogg` dans `assets/celestium/sounds/`, l'enregistrement dans `init/ModSounds.java`, la déclaration dans `sounds.json`. Minecraft ne lit **que** l'OGG.
