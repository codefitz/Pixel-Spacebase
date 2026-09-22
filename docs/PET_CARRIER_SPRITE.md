# Pet carrier sprite

The editable standalone sprite is `art/pet-carrier.png` (64x64 RGBA).
Its runtime copy occupies frame 23 of `core/src/main/assets/items.png`:
column 8, row 2 (one-based), rectangle x=448, y=64, width=64, height=64.
`ItemSpriteSheet.PET_CARRIER` selects this frame for `PetCarrier`.

Created with the built-in image-generation tool, then sampled down to 64x64
using nearest-neighbor sampling, preserving alpha. Ordinary chest frames
remain separate. To revise the artwork, edit this frame in the item sheet
and keep the standalone copy in sync.

## Generation prompt

Create a single production pixel-art inventory sprite of a PET CARRIER for a retro sci-fi dungeon game. One empty portable cat travel kennel, instantly recognizable by its prominent top carrying handle, barred front entrance with latch, side ventilation slots, rounded hard-shell upper casing and flat sturdy base. Three-quarter front view, front entrance facing slightly left and side visible on right, modest top visibility. Light cool grey shell, muted teal lower body, dark charcoal door interior and grille, restrained cyan highlights. Hand-placed crisp pixel clusters, limited 12-color palette, no gradients, no soft shading, no glow, no text, no animal, no floor, no scenery. Match an actual 64 by 64 pixel sprite grid with fine individual pixel detail; present as an exact nearest-neighbor enlargement if larger output is required. Entire object centered within a square transparent canvas, object occupies roughly 54 by 50 of the 64 pixel grid with a few transparent pixels padding. Genuine transparent background and clean alpha edges. This is an in-game sprite, not a presentation mockup. Single sprite only.
