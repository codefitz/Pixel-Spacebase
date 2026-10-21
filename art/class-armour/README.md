# Class armour: dedicated 64x64 animation frames

The Commander wears the crimson/white **Bulwark Exosuit**, with broad shoulders, a cyan field emitter and propulsion boots. The Captain wears the white/purple **Boarding Rig**, with an agile silhouette and tall blade-launcher pods. DM3000 uses a brass/steel **Reactor Chassis**, with an orange furnace core, cooling stacks and a heavy integrated launcher arm. The Shapeshifter retains its existing Cthulhu-inspired Eldridge form.

The built-in image tool generated the three `*-source.png` files using the exact prompts in `PROMPTS.md`. Each generated source is a seven-column, three-row grid. The installer preserves fine physical-pixel detail rather than reducing the new designs to the old 12x15 game-unit artwork. Nearest-neighbour sampling and binary alpha keep the installed edges sharp.

Run from the repository root:

```sh
java -Djava.awt.headless=true scripts/InstallClassArmourArtwork.java
java -Djava.awt.headless=true scripts/VerifyClassArmourArtwork.java
```

The runtime assets are `core/src/main/assets/class_armor_{commander,captain,dm3000,shapeshifter}.png`. Each is a 1344x64 strip containing **21 square 64x64 poses**. TextureCache's existing 4x asset scale makes each frame 16x16 game units. One scale per generated character and a shared foot baseline preserve proportions through movement and collapse. Transparent borders prevent neighbouring-frame bleed. The Bulwark source omits one walking pose and adds a reading pose; an explicit source-index map restores the game's expected animation sequence.

The animation order is two idle, six movement, five death, three attack, two operate, one flight and two reading poses. `HeroSprite` uses these dedicated textures for class armour and its portraits. `CloneSprite` uses the same textures for tier-six clones. `StatusPane` recentres portraits when their dimensions change. Pixel Dungeon rescue presentation keeps its original rendering path. Existing ability logic, class names, save keys, item slots, inherited suit properties and version metadata remain intact; only display names/descriptions change.

The three replacement inventory icons occupy the existing slots: Commander 165, Captain 168 and DM3000 166. Compact copies of their poses replace only their tier-six rows in the existing hero atlases for any remaining consumers. The Shapeshifter's original hero atlas and inventory icon are unchanged; its existing 48x60 poses are copied exactly into 64x64 frames with an 8px horizontal and 2px vertical offset. It has not been redrawn or artificially given new detail.

## Original tribute artwork

`legacy/` contains the exact original **Vader/Commander**, **Metroid/Captain** and **RoboCop/DM3000** animation rows and inventory icons, ready for possible future Easter eggs. It also includes a reference copy of the retained Cthulhu design. Full original atlases are backed up in `docs/sprite-backups/*-before-class-armour.png`. No Easter-egg unlock or cosmetic-selection mechanism is introduced in this change.

The installer verifies the frame grid, nonempty poses, transparent margins and pixel preservation outside the three changed class-armour rows and item slots. `VerifyClassArmourArtwork.java` additionally checks every dedicated frame, retained fine detail, exact Shapeshifter pixels and recoverable legacy sprites/icons. These pixel-level checks run with desktop Java because the Android test compiler excludes desktop image libraries. Sources and originals remain available for regeneration.

`preview.png` compares all four final designs, enlarged and at native size. The four `*-poses.png` images show all 21 installed poses. These previews were visually inspected. The three standalone pixel checks, the existing Hunter artwork check, compilation and debug APK build passed. In-game animation, equipment switching, clone appearance and portrait confirmation remain pending.
