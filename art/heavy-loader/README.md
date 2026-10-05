# Orange Heavy Loader

The source chassis is approximately 24 logical pixels wide and 26 pixels tall.
Each pose uses a 48×28 logical frame, with transparent horizontal space for the
extended claw. The PNGs use the project's normal 4× pixel scale. On-level frame
geometry is now scaled by 20/28, reducing the Loader by about 29%. The visible
chassis displays at roughly 69×74 pixels, close to the Hoverpod's
72-pixel hull while retaining its proportions and wide claw gutter.

The four `loader_*.png` assets contain the existing Commander, DM3000,
Shapeshifter or Captain head in the cockpit. The naked hero's head is sampled
without torso or suit pixels. The arm and equipment icons now match this orange
design, including grey hydraulic pistons, opposing silver claws and yellow/black
hazard markings. Compact hero portraits are copied from these class-specific
Loader poses into the corresponding armor row. Existing saves use the updated
presentation when a Loader is worn.

The 4-column, 3-row atlas contains idle (0–1), walk (2–5), attack (6–7),
operate/read (8–9) and death (10–11) poses. Flying uses pose 0.

`loader-source.png` is the generated chassis sheet with alpha transparency.
`hero-loader-preview.png` shows Commander, DM3000, Shapeshifter and Captain.

`loader-icons-source.png` is the two-icon sheet created using built-in imagegen.
`loader-icons-preview.png` shows the installed arm and equipment icons.
`loader-size-preview.png` compares the resized Loader with the Hoverpod, using
their displayed scale. In-game confirmation remains pending.

The installed icons are `core/src/main/assets/loader_arm.png` and
`loader_equip.png`, copied into the existing arm and armor slots of `items.png`.
The editable `loader_arm.svg` now matches the installed claw icon. Previous icons,
SVG and hero atlases are preserved in `docs/sprite-backups` under the
`before-loader-icons`, `before-orange-claw` and `before-loader-icon-update` names.

Regenerate the packed assets and run the frame/alpha checks from the repository root:

```sh
java -Djava.awt.headless=true scripts/PrepareLoaderSprites.java art/heavy-loader/loader-source.png '199,90,199,90,171,90,207,90,169,95,174,96,154,104,182,99,196,83,184,89' 0.08
```

The coordinates locate each living pose's cockpit in its source cell. The punch
pose extends into the transparent gutter and is imported with an expanded crop.
All poses use the same scale and foot baseline, with per-pose cockpit placement.

Install the new icons and refresh the compact portraits after repacking:

```sh
java -Djava.awt.headless=true scripts/InstallLoaderIcons.java art/heavy-loader/loader-icons-source.png
```

## Icon generation prompt

Create two equal square cells side by side on a genuine transparent background.
Use the current in-game orange Loader and its detailed chassis as references.
Left: detached orange hydraulic forearm with a chunky gunmetal piston and an
open two-jaw silver/grey gripper, marked with black/yellow hazard stripes; angle
from lower left to upper right. Right: the whole orange Loader exoskeleton with
black hydraulic shoulder tubing, grey piston limbs, matching claw grippers,
broad orange feet, tall rectangular open dark cockpit and a black-grey chest
console. Match the reference's orange/black/silver/yellow materials. Both are
crisp chunky pixel art designed for 16×16 logical cells with a 14×14 footprint
and transparent margins. No skinny cyan arm, blue windshield, old yellow-blue
design, labels, grid, background, shadows, glow or watermark.
