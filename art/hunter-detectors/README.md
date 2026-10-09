# Hunter detector artwork

Three distinct sensor icons made with the built-in image-generation tool:

| Detector | Display | Live asset | Atlas slot |
| --- | --- | --- | --- |
| Bio | Green life-sign silhouette and heartbeat | `core/src/main/assets/bio_detector.png` | 264 |
| Item | Blue cargo crate | `core/src/main/assets/item_detector.png` | 265 |
| Trap | Red warning triangle | `core/src/main/assets/trap_detector.png` | 266 |

The Hunter item and trap scanner classes reference their dedicated slots. Bio
artwork is reserved separately: the current game has a built-in Hunter suit
life-sign scanner, but no separate bio-detector item. Assignment of the bio icon
awaits clarification of its intended UI or item.

Icons share a charcoal housing, silver rim and gold connector prongs. They fit
64×64 frames, retaining detail on a 32×32 artwork grid with hard pixel edges
and transparent margins. The game's 16×16 logical item frames and display size
are unchanged. Existing serialized item classes and effects are preserved.

Generated sources are retained as `bio-source.png`, `item-source.png` and
`trap-source.png`; installed artwork is shown in `preview.png`. The original
atlas is retained in `docs/sprite-backups/items-before-hunter-detectors.png`.

## Install

From the repository root:

```sh
java -Djava.awt.headless=true scripts/InstallHunterDetectorIcons.java art/hunter-detectors
```

## Prompts

Common brief: a single pixel-art inventory icon for Pixel Spacebase. A small
square plug-in sensor module with charcoal metal housing, chunky silver rim,
two gold connector prongs along the bottom, front facing with slight depth on
the right edge, and an inset luminous screen. Genuine transparent background,
no outside shadow or glow. Low-resolution pixel art designed for a 16×16
logical grid, fitted within a 14×14 footprint and one-pixel transparent margins.
One large readable pictogram; no text, letters, numbers, labels or watermark.

- Bio: green display with a human life-sign silhouette and heartbeat pulse.
- Item: cyan-blue display with one simple outlined cargo crate with a lid.
- Trap: red-orange display with one warning triangle and central exclamation.

Build evidence is separate from in-game visual confirmation, which is pending.
