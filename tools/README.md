# Outils

Utilitaires hors compilation du mod. `tools/` n'est pas un dossier source : rien d'ici n'entre
dans le jar.

## DemoniumTextures.java

Dérive les seize textures du Demonium de celles du Celestium : même dessin, teinte basculée vers
le rouge, surface assombrie, et corruption pixel à pixel — points brûlés et braises.

Le bruit est déterministe, sa graine dérivant du nom du fichier et de la position du pixel.
Relancer l'outil réécrit exactement les mêmes images, donc sans différence parasite dans le dépôt.

```
java tools/DemoniumTextures.java src/main/resources/assets/celestium/textures
```

Les réglages sont en tête de fichier : largeur de la bande rouge, assombrissement, proportion de
pixels brûlés et de braises. À relancer après toute retouche d'une texture de Celestium.
