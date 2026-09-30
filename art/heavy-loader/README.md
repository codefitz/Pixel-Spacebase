# Orange Heavy Loader

The on-level chassis is approximately 24 pixels wide and 26 pixels tall, compared
with the normal hero's 12×15 frame and the Roman hologram's 24×24 frame. Each pose
uses a 48×28 logical frame, with transparent horizontal space for the extended claw.
The PNGs use the project's normal 4× nearest-neighbour pixel scale.

The four `loader_*.png` assets contain the existing Commander, DM3000,
Shapeshifter or Captain head in the cockpit. The naked hero's head is sampled
without torso or suit pixels. Inventory icons and Loader mechanics use their
existing assets and values. Existing saves use the new sprite when a Loader is worn.

The 4-column, 3-row atlas contains idle (0–1), walk (2–5), attack (6–7),
operate/read (8–9) and death (10–11) poses. Flying uses pose 0.

`loader-source.png` is the generated chassis sheet with alpha transparency.
`hero-loader-preview.png` shows Commander, DM3000, Shapeshifter and Captain.

Regenerate the packed assets and run the frame/alpha checks from the repository root:

```sh
java -Djava.awt.headless=true scripts/PrepareLoaderSprites.java art/heavy-loader/loader-source.png '199,90,199,90,171,90,207,90,169,95,174,96,154,104,182,99,196,83,184,89' 0.08
```

The coordinates locate each living pose's cockpit in its source cell. The punch
pose extends into the transparent gutter and is imported with an expanded crop.
All poses use the same scale and foot baseline, with per-pose cockpit placement.
