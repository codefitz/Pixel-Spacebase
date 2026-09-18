# Quartermaster bio-charge mission sprites

The bio-charge mission keeps its current behavior and save-game classes. Only the visuals changed:

- `core/src/main/assets/rot_heart.png`: immobile robot assembly with a damaged power core, eight 64 x 64 frames.
- `core/src/main/assets/rot_lasher.png`: articulated robot arm, seven 48 x 64 frames.
- `core/src/main/assets/items.png`: bio-charge component in the `HUNTER_TRAPPER` icon slot (column 0, row 21), 64 x 64.

The standalone first frames and component icon are in `art/quartermaster/` for future edits. The robot assembly, robot arm and portable core were made with built-in image generation using the previous sprites and `drone.png` as style references. The prompts requested opaque mechanical subjects on transparent backgrounds, hard pixel edges, charcoal steel with cyan/amber details, and no organic features. The frames were fitted to the established sprite sizes with nearest-neighbour sampling; attack and death variants were generated from those first frames.
