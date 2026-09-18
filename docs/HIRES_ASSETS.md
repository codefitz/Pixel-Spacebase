# 4x asset sheets

Game PNGs are now stored at four source pixels per game unit. A terrain tile
occupies 64x64 pixels in `tiles0.png` through `tiles4.png`, while its logical
size remains 16x16 in the renderer and level code. This keeps movement,
hitboxes, camera framing, and UI layout unchanged.

The existing art was enlarged with nearest-neighbour sampling as a starting
point. It contains no additional detail until the 4x sheets are redrawn. Keep
the current grid and frame positions when editing a sheet: for example, the
original 12x16 sprite frame is now 48x64 source pixels. The custom tile sheets
follow the same rule.

The three bitmap font sheets (`pixel_font.png`, `font1x.png`, `font2x.png`)
remain at their original resolution because their glyph metrics are measured
directly from source pixels. `asset_pixel_scale.txt` marks the migrated asset
set; do not run the one-time conversion again.
