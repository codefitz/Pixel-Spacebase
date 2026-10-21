# DM3000 armour progression

The unarmoured look (tier 0) uses the original DM300-based tier-two chassis: all 21 frames are copied exactly from the pre-reskin backup. Tier 1 retains its existing Data-inspired artwork. Tier 2 (`SpaceSuit`) is a gold astromech tribute with a rounded dome, circular sensor, panelled body, silver stabiliser legs and blue indicators. Tier 3 (`HunterSpaceSuit`) uses asymmetrical black/gunmetal Borg-inspired plating, cables, a partially gold face and a green optical implant.

The built-in image-generation tool created `gold-astromech-source.png` and `borg-hunter-source.png`. The exact prompts are in `PROMPTS.md`. Each source contains 21 poses in a seven-column, three-row grid, ordered for `HeroSprite.configureSuitAnimations`: two idle, six movement, five death, three attack, two operate, one flight and two read poses.

Run from the repository root:

```sh
java -Djava.awt.headless=true scripts/InstallDM3000ArmourArtwork.java
```

The installer packs two strips of 21 existing 48x60 physical-pixel frames (12x15 game units). It uses hard 2x2 pixel clusters so the optical sensors survive reduction, preserves a transparent margin for the Hunter's existing frame crop, removes neighbouring-pose fragments, and centres standing poses on a shared baseline. Crouching/death poses retain the standing scale unless they need extra room. It derives matching 64x64 inventory icons from the idle poses.

The game consumes the installed rows in `core/src/main/assets/dm3000.png` (unarmoured at y=0, tier 2 at y=120, tier 3 at y=180) and existing item slots 169/170 in `items.png`. The same hero rows supply portraits. `unarmoured-dm300-animations.png` is the exact old tier-two strip extracted from `docs/sprite-backups/dm3000-before-dm3000-armour.png` at y=120. The standalone strips and icons here are reproducible install outputs, not additional runtime textures. No gameplay, save keys, item indices, class names or other hero artwork changes are required.

Original atlases are preserved in `docs/sprite-backups/*-before-dm3000-armour.png`. The installer checks dimensions, nonempty generated poses, exact unarmoured-frame copying and pixel preservation outside the three target rows and two inventory slots against the current input atlases, so later artwork changes such as the launcher arm survive regeneration. The Data/uniform row, class armour and vehicle artwork are retained.

`preview.png` shows the four stages and icons sampled from the live atlases. `animation-preview.png` shows all 21 poses for the restored unarmoured look, gold astromech and Borg Hunter. These were visually inspected; in-game animation and appearance confirmation remains pending.

Validation: the existing `HunterSuitArtworkTest` passed and the debug APK built. The separate `HoverpodArtworkTest` fails because it still expects a 1008px-wide animation strip while the existing, unchanged strip is 1680px wide after its dedicated 80px-frame redesign.
