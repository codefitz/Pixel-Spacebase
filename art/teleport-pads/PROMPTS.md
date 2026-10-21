# Exact built-in image-generation prompts

The orange and blue prompts below produced the original open rings, retained as
the rim sources. The v1.0.6 edit prompt at the end produced the filled-centre
reference; the installer copies only its interior pixels into the original rings.

## orange

Use case: stylized-concept. Asset type: one 64 by 64 pixel floor-tile overlay for the sci-fi roguelike Pixel Spacebase. A SINGLE CIRCULAR TELEPORT PAD seen exactly from above, perfectly round, no perspective or ellipse. Slim concentric charcoal/silver annular metal rings with 8 small rectangular luminous segments symmetrically spaced around the ring and a slender inner glowing circle. Clean intentional chunky pixel art, designed as 32 by 32 logical pixels scaled 2x. The outside AND THE LARGE EMPTY CIRCULAR CENTER MUST BE GENUINELY TRANSPARENT so the existing floor tile shows through; absolutely no solid disc, filled center, background, floor, shadow, platform base, arrow, chevron, directional motif, text, labels or watermark. Fill almost the full tile with a circular ring while retaining a 4 pixel transparent outside margin at 64px. The center is open transparent, occupying at least half the diameter. Bright small lights embedded into metal rim. Do not put anything inside the empty center. Display one ring centered on a square canvas, genuinely transparent background. Colour: warm vivid ORANGE-amber lights for the PREVIOUS-FLOOR teleport pad.

## blue

Use case: stylized-concept. Asset type: one 64 by 64 pixel floor-tile overlay for the sci-fi roguelike Pixel Spacebase. A SINGLE CIRCULAR TELEPORT PAD seen exactly from above, perfectly round, no perspective or ellipse. Slim concentric charcoal/silver annular metal rings with 8 small rectangular luminous segments symmetrically spaced around the ring and a slender inner glowing circle. Clean intentional chunky pixel art, designed as 32 by 32 logical pixels scaled 2x. The outside AND THE LARGE EMPTY CIRCULAR CENTER MUST BE GENUINELY TRANSPARENT so the existing floor tile shows through; absolutely no solid disc, filled center, background, floor, shadow, platform base, arrow, chevron, directional motif, text, labels or watermark. Fill almost the full tile with a circular ring while retaining a 4 pixel transparent outside margin at 64px. The center is open transparent, occupying at least half the diameter. Bright small lights embedded into metal rim. Do not put anything inside the empty center. Display one ring centered on a square canvas, genuinely transparent background. Colour: bright CYAN-BLUE lights for the NEXT-FLOOR teleport pad.

## v1.0.6 filled centres

Mode: built-in imagegen edit, with `transparent_background: true`.
Edit target: the previous unfilled `core/src/main/assets/teleport_pads.png` strip.
Supporting references: `orange-source.png` and `blue-source.png`.
Saved result: `filled-centres-source.png`.

Use case: precise-object-edit. The FIRST reference is the edit target: a two-frame horizontal sprite sheet showing the existing orange previous-deck teleport pad on the LEFT and cyan-blue next-deck teleport pad on the RIGHT. The other two references show the same existing metal rims at higher resolution, for detail/style only. Fill the empty circular interior of BOTH pads with a fully opaque, low-contrast dark gunmetal standing platform. Give the centre restrained concentric metal panel detail and a subtle warm orange or cool blue reflection matching its rim; it must read as a solid flat sci-fi metal disc, with no hole and no floor visible through its centre. Preserve the existing silver/charcoal metal rims, eight luminous rim segments, orange/blue hues, perfectly top-down circular silhouettes, pixel-art treatment, equal sizes and left/right order. Keep the outer area genuinely transparent. Keep the composition as exactly TWO pads side by side in equal square frames in a 2:1 horizontal strip, with transparent margins and no overlap. No extra pads, perspective, floor background, arrows, symbols, text, labels, figures or shadows outside the pads. The eventual game sheet is 128 by 64 pixels, so keep the centre design simple, legible and restrained at small pixel-art size.
