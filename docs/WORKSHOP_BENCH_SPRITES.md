# Workshop bench sprites

The workshop's upgrade and breakdown benches use their own 64 x 64 item-atlas frames in `core/src/main/assets/items.png`:

| Object | Atlas column | Atlas row | Constant |
| --- | ---: | ---: | --- |
| Upgrade bench | 8 | 1 | `UPGRADE_BENCH` |
| Breakdown bench (`MAKER_BENCH`) | 9 | 1 | `BREAKDOWN_BENCH` |

Columns and rows are zero-based. The command terminal remains at column 2, row 1. Standalone sprites are in `art/workshop-benches/` for further edits.

The sprites were created with built-in image generation using the existing item-atlas container row as a style reference. The upgrade prompt requested a low precision worktable with clamping jaws, tools and cyan calibration detail. The breakdown prompt requested a rugged recycler with an open hopper, crushing rollers, parts tray and amber hazard marks. Both requested a transparent background, hard pixel edges, a compact readable silhouette and no terminal tower. The generated images were reduced to atlas size with nearest-neighbour sampling.
