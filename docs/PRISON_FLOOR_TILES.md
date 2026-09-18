# Prison floor tiles

The prison atlas uses the existing maintenance terrain mapping. No gameplay mapping changes are needed. Only frames 2, 9, 15, 39, 41, and 44 in `core/src/main/assets/tiles1.png` were redrawn.

| Frame | Visual role | Alternate |
| --- | --- | --- |
| 2 | Powered panel | 39 |
| 9 | Spent/broken circuitry | 41 |
| 15 | Open wiring hatch | 44 |

Editable 64 x 64 source frames and a contact sheet are in `art/prison-tiles/`. They were created with built-in image generation, using each matching maintenance tile and the existing prison atlas as references. The prompts requested a top-down, full-bleed, opaque 64 x 64 pixel-art tile in the prison's dark steel palette, with subtle cyan power details and alternate variants for each pair. The output was reduced to 64 x 64 with nearest-neighbour sampling and darkened neutral steel tones to suit the prison sheet.

These are terrain frames, not overlays. Touched/untouched transitions remain controlled by the existing shared terrain mapping, so searched panels do not revert to a plain floor frame.
