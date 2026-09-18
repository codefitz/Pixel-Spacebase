# Prison security camera

`art/prison-security-camera.png` is the 64x64 transparent camera sprite.
It is composited over the existing wall backgrounds in `core/src/main/assets/tiles1.png`:

| Frame | Rectangle | Wall background |
| --- | --- | --- |
| 12 | x=768, y=0, 64x64 | Frame 4 |
| 42 | x=640, y=128, 64x64 | Frame 40 |

`SecurityBlockLevel.SecurityCameraLight` adds a 3x3 source-pixel red indicator at
tile pixel (35,33), on for 0.35 seconds in each 1.4-second cycle. Indicators are
staggered by cell position, hidden outside current visibility, and removed when
the wall decoration is destroyed. This replaces the old flame emitter and halo
on prison floors and their boss level.

Art was created with built-in image generation, then sampled to 64x64 using
nearest-neighbor sampling and composited over the existing wall tiles. All
other atlas pixels were verified unchanged from the user's working copy.

## Generation prompt

Use case: precise-object-edit. Create a sharp replacement for the blurry security camera in the LEFT tile of the reference image; the right tile is only a reference for the muted grey game palette. Output ONLY one isolated wall-mounted CCTV camera sprite on a genuinely transparent square background, without any wall or tile background. The camera is a small retro sci-fi security camera, silver grey rectangular housing with a dark lens at the front, a clear short mounting bracket behind it. Three-quarter overhead view for a top-down dungeon game; camera points diagonally down and right. Strong crisp pixel art silhouette, flat limited palette, stepped pixel edges, no blur, gradients, glow, antialiasing, text, or logos. Fine detail should be readable on a 64x64 pixel grid, displayed enlarged with nearest-neighbor pixels. Center the complete camera including bracket within the central 44x44 pixels of that grid, leaving transparent margins. Put a small dark unlit indicator socket next to the lens so the game can overlay a blinking red LED. A recognizable utilitarian surveillance camera, not a photographic camera, no scene or presentation layout. Single square sprite only.

## Transparency correction

Background extraction only. Preserve the exact CCTV camera pixel artwork, its shape, colours, size, orientation and position. Remove ALL the white and grey checkerboard background and replace it with actual fully transparent pixels in the PNG alpha channel. The checkerboard is not transparency: do not draw a checkerboard or any solid background. Keep the camera opaque. Output only the isolated camera with genuine alpha transparency around it and in the gaps in its bracket. No other changes.
