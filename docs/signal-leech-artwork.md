# Signal Leech plasma bug

The built-in image-generation tool created the molten insect artwork. `scripts/PrepareSignalLeechSprites.java` samples the generated 4-column, 3-row sheet to 12x15 logical pixels per frame and expands it with exact 4x pixel blocks. The game asset is `core/src/main/assets/signal_leech.png` (192x180 RGBA). `docs/signal-leech-preview.png` shows the installed pixels enlarged. The former caster sheet is preserved in `docs/sprite-backups/signal-leech-before-plasma-bug.png`.

Frames 0–1 idle, 2–4 crawl, 5–6 bite/spit, 7–10 collapse. Frame 11 is unused. The existing class name, asset path, save aliases, encounter distribution, stats and loot remain intact. Plasma spit replaces Weakness with Burning, uses existing plasma protection rules, and does not change map terrain. Fire/plasma immunity replaces the old undead classification.

## Generation prompt

Use case: stylized-concept. Asset: tiny pixel-art enemy animation sheet for Pixel Spacebase. Create an alien lava/plasma beetle, low squat six-legged insect, obsidian charcoal armored shell with glowing orange molten cracks, bright amber swollen abdomen, short antennae, fanged mouth pointing slightly right ready to spit plasma. Not a humanoid, no robes or weapons. Crisp chunky pixel clusters readable at just 12x15 logical pixels per frame, dark outlines with bright orange/yellow silhouette details. Transparent background. Exact 4 columns by 3 rows of equal cells with NO margins or gutters; each cell has a complete bug centered with generous transparent padding and same floor baseline. Eleven animation frames in row-major order: 0 idle, 1 pulsing abdomen idle, 2 crawl front legs step, 3 crawl back legs step, 4 crawl body bob, 5 mandibles open charging orange mouth, 6 spits small bright orange globule staying within cell, 7 wounded curl, 8 shell cracks, 9 collapses low, 10 dead flattened shell; final cell 11 completely empty. Consistent bug scale across all frames, creature silhouette about 10 logical pixels wide by 10 high inside 12x15 cell. No labels, grid lines, text, ground, shadows, props, detached particles crossing cells, watermark or background. Whole-sheet aspect ratio 48:45.

## Verification

The installer checks nonempty artwork and transparent pixels in all eleven used frames. Automated tests check PNG dimensions, plasma damage source, Burning rather than Weakness, Burning immunity and lethal hits. In-game appearance, animation and suit/shield interactions still require playthrough verification.
