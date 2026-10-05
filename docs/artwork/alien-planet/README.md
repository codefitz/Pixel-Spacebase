# Alien colony artwork

Created with the built-in image_gen tool. Original sources are preserved alongside the fitted preview.

- `terrain-generated.png`: selected 2×2 terrain sheet; violet ground, rock, turquoise vegetation and landing beacon.
- `colonist-generated.png`: selected refined friendly alien source, with transparent background.
- `preview.png`: enlarged fitted tiles and NPC sprite; artwork preview, not a gameplay screenshot.
- `core/src/main/assets/alien_planet_tiles.png`: native 32×32 atlas, four 16-pixel frames.
- `core/src/main/assets/alien_colonist.png`: native 16×16 NPC sprite.

The existing Security boss artwork at `custom_tiles/alien_planet_tiles.png` is preserved. The new colony terrain uses its own path and asset constant.

Reinstall from the repository root:

```sh
java scripts/InstallAlienPlanetArtwork.java docs/artwork/alien-planet/terrain-generated.png docs/artwork/alien-planet/colonist-generated.png
```

The installer fits these new assets with nearest-neighbour sampling. The fitted preview was inspected. Compilation passed; no game session or automated tests were run for this change.

## Prompts

### Terrain

Use case: stylized-concept. Create a square pixel-art terrain tile sheet for a tiny top-down sci-fi roguelike, EXACTLY four equal square tiles arranged in a seamless 2 by 2 grid, no gaps, no labels. Each quadrant is designed at exactly 16x16 logical pixels with crisp enlarged nearest-neighbor blocks and flat 8-color pixel clusters. Top left: walkable violet rocky soil with sparse lighter lavender pebble flecks; top right: impassable dark plum rock outcrop filling the entire tile, rocky block clusters with lavender highlights; bottom left: walkable violet soil with short turquoise alien moss and magenta sprigs, no large blocking objects; bottom right: walkable soil with an embedded cyan alien landing beacon ring visible in the center. Fully opaque edge-to-edge terrain in every tile. Each tile viewed straight overhead, no perspective, no borders, no text, no lighting gradients, no smooth detail. Clear readable shapes at 16px. Consistent purple stone and turquoise vegetation palette. Output square canvas.

### Initial colonist

Use case: stylized-concept. Create ONE friendly alien NPC pixel art sprite for a tiny top-down roguelike. Isolated full body on genuine transparent background, front three-quarter game view. Short friendly teal alien with a wide oval head, two large dark purple eyes, two short antennae, stubby feet, wearing a simple plum trader robe and amber belt pouch. Gentle readable face, neutral standing pose with hands at sides, no weapon. STRICT 16x16 logical pixel grid enlarged as big crisp pixel blocks. Use chunky 6-8 color pixel clusters and fit within 14x14 pixels. No fine details, no shadows, no glow, no text, no background, no perspective scene. Single sprite only, square canvas.

### Selected colonist refinement

Reference: the initial colonist source.

Simplify this friendly teal alien into a TRUE tiny roguelike NPC sprite on a strict 16x16 logical pixel grid. Keep transparent background, teal head, plum robe, amber belt pouch, two antennae. HEAD must be 8 logical pixels wide and 5 high with TWO plainly visible dark purple eyes of 2x2 pixels each and a 1-pixel smile. Body robe 6 pixels wide, 5 high. Two stubby feet. Short single-pixel antennae with one-pixel tips. Use only 8 flat solid colors, absolutely no shaded subpixel texture or tiny decorative detail. Full figure fits within14x14 logical pixels. Render those logical pixels enlarged into big solid square blocks. Center sprite, no text, no shadow, no background, no scene.
