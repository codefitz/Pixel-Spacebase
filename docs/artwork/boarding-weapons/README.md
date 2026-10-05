# Boarding weapons artwork

Generated using the built-in `image_gen` tool with transparent backgrounds. The Repulsor Baton replaces Drill (tier 2, defence bonus up to 2); the Phase Cutter replaces Spade (tier 3, 20% accuracy bonus). Existing damage and upgrade scaling are preserved.

## Assets

- `repulsor_baton-generated.png`: selected, refined baton source.
- `phase_cutter-generated.png`: selected cutter source.
- `preview.png`: enlarged fitted sprites, baton left and cutter right.
- `core/src/main/assets/repulsor_baton.png` and `phase_cutter.png`: editable fitted icons.
- `core/src/main/assets/items.png`: installed sprites in slots 107 and 113.
- `docs/sprite-backups/items-before-boarding-weapons.png`: original atlas backup.

From the repository root, reinstall the selected sources with:

```sh
java scripts/InstallBoardingWeaponsArtwork.java docs/artwork/boarding-weapons/repulsor_baton-generated.png docs/artwork/boarding-weapons/phase_cutter-generated.png
```

The installer fits each image to a 16×16 logical cell using nearest-neighbour sampling and checks that every other atlas cell is preserved. Its installation check passed. The final fitted preview was inspected visually. Java compilation passed with `:core:compileDebugJavaWithJavac`. No gameplay or save-load run was performed. Automatic approval review blocked adding/running a test because the session requires an explicit user request for tests.

## Generation prompts

### Initial Repulsor Baton

Use case: stylized-concept. Asset type: one pixel-art inventory weapon sprite for Pixel Spacebase. Generate only one isolated REPULSOR BATON, no text or labels, genuine transparent background. Security-grade sci-fi baton: chunky dark gunmetal shaft, short charcoal ribbed grip, tiny handguard, prominent blunt cylindrical head with two amber/orange repulsor coil bands and pale steel endcap. Diagonal orientation, grip at bottom left and striking head top right. Flat crisp retro game pixel art, designed on a STRICT 16 by 16 logical pixel grid and enlarged with nearest-neighbor pixels. Minimal readable silhouette, large simple pixel clusters, only 6-8 colors. Fit object inside 14x14 logical pixels with clear transparent margins. No photorealism, no soft blur, no glow halo, no drop shadow, no hands, no background. Style should fit a tiny top-down roguelike inventory. Make head and grip distinct even at 16px. Prefer a square canvas.

### Phase Cutter

Use case: stylized-concept. Asset type: one pixel-art inventory weapon sprite for Pixel Spacebase. Generate only one isolated PHASE CUTTER, no text or labels, genuine transparent background. Compact futuristic boarding weapon: short black/gunmetal ribbed hilt with violet power cell, angular steel containment emitter at hilt top, a hooked broad crescent blade of bright icy cyan energy with a white cutting edge and deep turquoise shadow. Distinct from a straight laser sword and from a spade: asymmetric curved hooked cutting blade. Diagonal orientation grip bottom left and blade top right. Flat crisp retro game pixel art, designed on a STRICT 16 by 16 logical pixel grid and enlarged with nearest-neighbor pixels. Minimal very readable silhouette, large simple pixel clusters, only 6-8 colors. Fit object inside 14x14 logical pixels with clear transparent margins. No photorealism, no soft blur, no glow halo, no drop shadow, no hands, no background. Style should fit a tiny top-down roguelike inventory. Prefer a square canvas.

### Selected baton refinement

Reference: the initial generated baton image.

Edit this Repulsor Baton inventory sprite. Keep the dark gunmetal, charcoal grip, bright silver endcap and amber/orange coil palette, diagonal grip-bottom-left head-top-right orientation, and transparent background. Make the silhouette MUCH chunkier and more compact: striking head should occupy approximately the upper HALF of the weapon's total length, with a broad cylindrical head 5 logical pixels wide and a shorter 2-pixel-wide handle. Show TWO very bold separate amber coil bands across the head, with a dark separator so both remain readable at 16x16 pixels. This is a true tiny roguelike sprite on a STRICT 16x16 logical pixel grid, enlarged into big square pixel blocks; no details smaller than one logical pixel. Fit within 14x14 logical pixels. Reduce decorative subpixel detail. No text, no shadows, no glow halo, no background.
