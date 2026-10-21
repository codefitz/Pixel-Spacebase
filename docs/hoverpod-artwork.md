# Hoverpod artwork refresh — larger screened cockpit

The Hoverpod is a rounded silver/navy maintenance capsule with a broad blue glass canopy, orange hazard tabs and twin cyan hover jets. The captain, commander, shapeshifter and DM3000 heads are copied from their existing unarmoured sprites into the intact pod's cockpit. Mild glass tint and reflections appear over the pilot, keeping the face visible behind the screen. Damaged/death poses show cracked and collapsed wreckage without a pilot overlay.

The built-in imagegen tool generated the vehicle artwork, using the older blue bubble cockpit and previous silver/navy pod as references. The source is saved in `docs/hoverpod-screened-source.png`. `scripts/InstallScreenedHoverpodArtwork.java` packs all 21 poses into dedicated 20×20 logical-pixel frames, enlarged four times to 80×80 pixels with hard pixel edges. The normal hull occupies about 72 pixels, extending slightly beyond a 64-pixel floor tile. The cockpit position is detected per pose to keep the pilot inside the canopy during flight and actions.

From the repository root:

```sh
java scripts/InstallScreenedHoverpodArtwork.java docs/hoverpod-screened-source.png 6 3
```

The game uses `core/src/main/assets/hoverpod_captain.png`, `hoverpod_commander.png`, `hoverpod_shapeshifter.png` and `hoverpod_dm3000.png`. Each contains 21 frames in a horizontal strip. `hoverpod_animations.png` contains the empty vehicle poses. `hoverpod.png` is the inventory icon, installed at the existing item slot 163. Compact copies remain in tier-four rows of all four hero atlases for portraits. The preview in `docs/hoverpod-preview.png` shows captain, commander, shapeshifter and DM3000 in order.

The previous 4/3 display scale is replaced by the dedicated larger frames. Sprite dimensions now also describe its positioning and effects. Original assets from immediately before this update are preserved in `docs/sprite-backups/*-before-screened-hoverpod.png`; earlier artwork backups remain available. The installer changes only the Hoverpod row of hero atlases and its inventory item slot. In-game appearance remains unconfirmed.

## Generation prompt

Use case: precise-object-edit. Create a cooler, slightly larger Hoverpod based on the old enclosed blue bubble cockpit and current silver/navy capsule. Large clear blue canopy occupying the upper half, thick silver rim, empty dark-blue interior for the actual hero, cyan glass reflection pixels at the upper right, metallic silver lower bowl, gunmetal belly, orange hazard tabs and twin cyan hover jets. No generated pilot. Chunky pixel art designed at 20×20 logical pixels with an 18×18 maximum footprint and transparent margins. Exactly seven columns and three rows, 21 poses: idle/reflection, six glides, five damaged/wrecked poses, three attack light poses, two maintenance gripper poses, strong flight exhaust and two raised control-screen poses. Intact poses retain the same clear cockpit design. No legs, wheels, humanoid suit, environment, floor, shadows, labels, text or watermark. Genuine transparent background.
