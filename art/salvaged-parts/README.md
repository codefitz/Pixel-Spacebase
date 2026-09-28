# Salvaged parts icon

Replaces the gold nugget graphic used by SpareBaseParts, Leonard's engineering quest item. The display name is now "salvaged parts". Currency parts keep their original icon and behaviour.

- Runtime atlas: `core/src/main/assets/items.png`.
- Slot: `ItemSpriteSheet.SALVAGED_PARTS`, index 405, rectangle (320, 1600, 64, 64).
- `icon.png`: transparent 64 × 64 exported icon.
- `preview.png`: currency and salvaged parts side by side.
- Backup: `docs/sprite-backups/items-before-salvaged-parts.png`.

Created with built-in image generation. The transparent artwork was cropped to its occupied bounds, scaled with nearest-neighbour sampling to fit 56 × 56, and inserted into the existing atlas slot. Pixel comparison confirmed every pixel outside that slot is unchanged, including the currency icon. The class and slot index are preserved for existing saves.

## Prompt

Create one production game inventory icon: SALVAGED ELECTRONIC PARTS, a compact pile consisting of one teal green circuit board tilted diagonally, two chunky silver steel washers or nuts, a short copper-coloured bent cable with connector, and a small dark steel relay. Crisp chunky pixel art matching a retro sci-fi roguelike, readable at 64x64 pixels. Small limited palette, steel blue grey and teal, copper accent, dark navy outline, simple distinct silhouettes, subtle hard-edged highlights. Three-quarter overhead view, centred as a single coherent pile, fits within central 80% of square canvas. Genuinely transparent background, no shadow, no text, no numbers, no border, no coins, no gold nuggets, no currency symbols. Output square PNG. This is repair salvage for a quest, visually distinct from currency. Avoid fine detail; deliberately chunky game pixels.
