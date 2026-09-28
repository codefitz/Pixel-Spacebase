# Habitat transporter bays

The habitat entry/exit atlas slots previously showed ordinary floor panels. They now show circular transporter disks: cyan/down for the previous deck and amber/up for the next deck.

Runtime asset: `core/src/main/assets/tiles3.png` (1024 × 512, 4× asset scale). Tiles 8 and 7 replace the arrival/departure markers; new tiles 64 and 65 supply dedicated transporter room floors and illuminated walls. The atlas retains power-of-two dimensions. All other existing tiles are preserved pixel for pixel.

HabitationRingLevel supplies visual overrides using the saved entrance and exit room bounds. SpacebaseTilemap applies these to levels using the habitat atlas, including the final workshop. Existing saves receive the room treatment without changing terrain, room generation, hazards, door behaviour, fog, or travel logic. Water, planters, traps, doors, and usable fixtures retain their own visuals. Decorative smoke is omitted from the new illuminated bulkheads. Holodeck atlases and their independent powered-down entrance/exit handling are unchanged.

`generated-grid.png` is the original built-in image-generation output. `preview.png` is a representative tile layout, not an in-game screenshot. `PackTransporterTiles.java` packs the four source tiles with nearest-neighbour scaling and verifies opaque new tiles and unchanged pixels elsewhere. The previous habitat atlas is saved at `docs/sprite-backups/tiles3-before-transporters.png`.

Rebuild from the repository root:

```sh
java -Djava.awt.headless=true art/habitat-transporters/PackTransporterTiles.java art/habitat-transporters/generated-grid.png docs/sprite-backups/tiles3-before-transporters.png core/src/main/assets/tiles3.png art/habitat-transporters/preview.png
```

## Generation prompt

Create a production 2x2 game TILE ATLAS for a retro top-down sci-fi station roguelike, using the attached existing habitat tilesheet as style context. Exactly four equally sized SQUARE tiles in a 2 by 2 grid, fill entire square output with tiles, NO gutters, no outer margin, no labels, no text. Hard-edged chunky pixel art for downsampling each tile to 64x64 physical pixels (16x16 game units). All tiles have fully opaque backgrounds. Top LEFT: round Star Trek inspired transporter disk for ARRIVAL, circular silver concentric rings inset flush into dark slate-blue metal floor, cyan illuminated ring, a large clear cyan DOWN chevron in the centre indicating travel to a lower deck. Top RIGHT: matching transporter disk for DEPARTURE, identical geometry but warm amber lit ring, large clear amber UP chevron in centre for travel to higher deck. Each circular pad centred and occupies 80 percent of its tile, surrounded by opaque dark slate-blue floor. Bottom LEFT: seamless repeating transporter room FLOOR panel, dark slate-blue brushed metal with subtle squared seams at tile edges and four small recessed silver corners; flat and uncluttered middle, no glowing symbol or round disk. Bottom RIGHT: matching transporter room WALL panel viewed from same top-down roguelike perspective as reference walls, layered silver blue metal bulkhead, two restrained vertical cyan light strips, strong dark boundary, no door, no console. Each tile independent, evenly divided at exactly 50 percent width and height. Consistent palette and scale. The new room should feel like a clean dedicated transporter bay, visibly distinct from the white habitat floor in the reference. Avoid 3D perspective, gradients, fuzzy glow, tiny text, characters, objects, checkerboard backgrounds, diagonal camera angle, water, transparency.
