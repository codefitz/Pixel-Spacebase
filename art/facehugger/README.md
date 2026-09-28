# Live facehugger reskin

The living enemy now matches the ivory dead alien bug dropped by AlienEgg (AlienPod, item atlas column 5, row 25). The inventory item remains the visual reference.

- Runtime asset: `core/src/main/assets/facehugger.png`, 1024 × 64 RGBA.
- Frames 0–9: idle, breathe, crawl A, crawl B, prepare, lunge, recoil, curl, collapse, dead. Frames 10–15 are transparent.
- Each physical frame is 64 × 64 pixels, matching the existing 16 × 16 logical frame at 4× asset scale. FacehuggerSprite's existing animation indices remain valid.
- `generated-grid.png` preserves the built-in image-generation output; `preview.png` displays all exported frames against a dark background.
- Previous runtime sheet: `docs/sprite-backups/facehugger-before-live-reskin.png`.

## Generation prompt

Use case: precise-object-edit. Create a replacement LIVE FACEHUGGER enemy animation sprite sheet for a pixel-art sci-fi game. Reference 1 is the dead bug inventory item: faithfully use its ivory/bone cream colouring, narrow organic body, long finger-like jointed legs, and long curved tail, but animate this creature alive. Reference 2 is the old enemy strip solely for animation purposes; replace its crude green spider look entirely. Deliver a square 1024x1024 PNG with genuine transparent background, laid out as EXACTLY 4 columns by 4 rows of equally sized 256x256 cells with NO grid lines, labels or text. All creatures stay inside their own cell with at least 20 pixels transparent margin. Ten distinct poses in row-major order, last SIX cells fully transparent. Frame 0 idle crouched live facehugger facing right, long segmented tail curving to left, eight spread finger-like legs around a small flattened fleshy body, no eyes or human face. Frame 1 idle breath slightly raised body and curled tail. Frames 2 and 3 alternate crawling leg poses rightward. Frame 4 coiled preparing to spring; frame 5 lunging right with legs extended forward and tail straightened left. Frames 6 7 8 9 distinct death progression: recoil, legs curling upward, falling flattened, fully still curled pale corpse matching item reference. Consistent camera slight overhead side view, identical body scale and palette across every pose, whole creature about 210 pixels wide in each cell. Crisp chunky PIXEL ART, hard edges, limited palette of dark brown contour, muted ochre shadows, warm cream midtones and ivory highlights. Readable individual fingers and long tapered curling tail, small warm tan central body. Strong silhouette readable when each cell is shrunk to 64x64. Not a spider, not green, no oval spider abdomen, no diffuse glow, no ground plane or drop shadow, no scenery. This is a production sprite sheet; maintain exact equal grid alignment and cell isolation.

## Export and checks

The generated grid was 1254 × 1254 RGB with a baked neutral checkerboard, so export removed neutral pixels with channel spread ≤24 and minimum channel ≥75. The warm ivory/brown artwork was retained. Each of the first ten grid cells was reduced to 64 × 64 with nearest-neighbour sampling and packed horizontally into the runtime sheet. Downward offsets of 3, 4, 7, and 6 pixels align frames 4, 7, 8, and 9 respectively to the standing frames' baseline. Lunge and recoil remain raised.

Export checked alpha transparency, non-empty animation frames, and no occupied pixels touching frame edges. The final 5 × 2 preview was visually inspected. In-game animation has not been playtested.
