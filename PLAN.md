# Targeted Release: v1.0.6

Active backlog for v1.0.6. The previous working plan is preserved unchanged in [the v1.0.5 archive](docs/PLAN_v1.0.5_ARCHIVE.md), including implementation notes and completed checks. Earlier work remains in [the v1.0.4 archive](docs/PLAN_v1.0.4_ARCHIVE.md).

The Android manifest currently declares **1.0.4 (10004)**. Keep version changes for release preparation; the archived v1.0.5 work forms the implementation baseline for this plan, not evidence of a published release.

Work through release stability and reported bugs first, then equipment, companions, rooms and narrative. Scope the decisions before implementing them. Unchecked work below is outstanding; the verification section contains implemented changes awaiting in-game acceptance, not features to rebuild. Mark those checks complete only when the stated behaviour has been confirmed.

## Release Stability and Bug Fixes

- [x] **Fix release shrinking.** Corrected the game-package keep rule in `core/proguard-rules.pro`, preserving reflective construction, class-name-based text and saved objects while leaving release minification and resource shrinking enabled. `:core:assembleRelease --offline` passed; R8 mapping and packaged DEX checks retained all 1,223 compiled game classes and 93 engine classes under their original names, scene constructors and all eight message bundles. Signed the same APK with a temporary test key and verified its content was unchanged. In a read-only Android 37 emulator, confirmed welcome/title text, a new Commander run, save/load after backgrounding and a cold process restart, normal descent to deck 2 and ascent to deck 1 with terrain and HP retained, and restoration of an upgraded legacy Spade as a Phase Cutter. No reflection or crash errors appeared in the checked output. This is a targeted release smoke test; broader save-compatibility/playthrough checks and production signing remain under Release Preparation.
- [ ] **Investigate accidental MELT destruction.** Reproduce the reported sequence of choosing MELT and then clicking the blaster. Trace the item picker and launcher interactions, and prevent the blaster or integrated launcher from being consumed. Preserve deliberate melting of eligible ordinary melee weapons; cancelling or rejecting an item must consume neither an item nor a turn.
- [ ] **Keep the cat after the prison boss.** Fix the cat disappearing when that boss is defeated. Have her wander back to the previous level, and confirm that revisiting and save/reload preserve her state without duplication.
- [ ] **Let DM3000 wipe off acid with Medigel.** Extend its non-healing Medigel use to clear acid even when it is not burning. Keep the existing restriction on Medigel healing and check charge use, action text and quickslots.
- [ ] **Give DM3000 and Shapeshifter immunity to alien eggs and facehuggers.** Cover both egg-triggered and direct facehugger encounters; check that other hero classes retain their existing behaviour.
- [ ] **Restrict Engineering sparks to Leonard's level.** Use Leonard's actual quest/deck state rather than assuming a fixed floor. Check newly generated and restored Engineering decks, including levels without Leonard.

## Equipment and Companions

- [ ] **Let the Hoverpod activate a trap or tile beneath it with an extra click.** Keep normal movement and hovering behaviour; make deliberate activation possible while already on the cell. Check traps and interactive terrain separately.
- [ ] **Make enemies target the active cat.** Ordinary enemies should pursue her while she is active, but leave her alone while she is sitting and waiting to be petted. Bosses should kill her when she is in the same room regardless of that status. Check activation, waiting, room boundaries and level transitions alongside the prison-boss fix.

## Rooms and Artwork

- [x] **Archive unused packaged assets.** Moved 18 unreferenced soundtrack, HUD, terrain and alternate-sprite files into [the asset archive](art/archive/v1.0.6-unused-assets/README.md), preserving their bytes, original paths and SHA-256 checksums. Removed four unused `Assets` constants. Retained live assets, Pixel Dungeon rescue art and artwork-tool/test files. Debug and minified release builds passed; both APKs retain the same 200 asset files byte for byte and exclude the archive. The release APK is 37.86 MiB smaller (27.08%). Gameplay checks were not repeated for this change.
- [x] **Fill the interiors of entrance and exit pads.** Added opaque metal centres to both frames of `teleport_pads.png`, preserving every original rim and exterior pixel, the orange/blue directions and transparent outer margins. Updated the artwork installer, generated source and six-background preview in [the pad artwork folder](art/teleport-pads/README.md). Pixel/grid checks and debug/minified release builds passed. Travel, locked-pad dimming, pulses and transition effects retain their existing behaviour; in-game visual confirmation remains under verification below.
- [ ] **Give the inactive Holodeck distinguishing walls.** Make its powered-down walls readable alongside the grid floor, including the arena doorway and entrance surround. Preserve the independent inactive treatment of the unstable holo-projector quest room.
- [ ] **Add an optional room reachable only by jetpack on some levels.** Keep the main route, mandatory quest items and level progression reachable without it. Check flight access, landing and return before choosing generation frequency.
- [ ] **Map sprite directions and animation frames.** Document standing, walking and fighting directions for the relevant sprite sheets, including frame sizes and code mappings, to guide further art changes and avoid reversed or misaligned poses.

## Y Encounters and Narrative

Use [the staged rescue design](docs/Y_RESCUE_PLAN.md) for the existing journey and save rules. The changes below extend that behaviour when implemented; they are not part of the completed stages in the archived plan.

- [ ] **Have Y punish being zapped once per run.** Send the hero to the blackout/dark-maze level with the remark, "This will teach you a lesson". Save the one-time flag and preserve the return journey so reloads cannot repeat the punishment or lose the origin.
- [ ] **Add a 1% chance that Y refuses a later doorless-room rescue.** Enable this only after Y has already performed at least one rescue, preserving the first-rescue guarantee. He appears and tells the hero he will not rescue them; the hero remains stranded unless they have another escape, such as teleportation. Save the outcome so reloading cannot reroll the refusal. Keep this chamber event separate from the existing rescue-destination probabilities.
- [ ] Do a test of all Y scenarios - alter logic to cycle through them.

## Decisions and Features to Scope

- [ ] **Choose a replacement for Yog Duza.** A giant angel is one suggestion; settle the concept, name, artwork and any encounter changes before implementation.
- [ ] **Define the blind enemy.** It should take 50% of the hero's health when it finds them. Decide whether that means current or maximum HP, how it detects the hero, and where it belongs before implementing or balancing it.
- [ ] **Scope the pixel-art overhaul.** List the assets to replace, agree the style and frame conventions, and split the work into reviewable batches using the sprite-direction map. Retain the original tribute artwork already archived for possible Easter eggs.
- [ ] **Resolve the biological detector icon assignment.** The v1.0.5 artwork includes a biological icon, but there is no separate bio-detector item. Decide where it belongs before assigning it; item and trap module icons are already implemented.

## Carried-over In-game Verification

These changes are implemented in the v1.0.5 baseline but explicitly still awaited acceptance in that plan. Previously completed checks, including the confirmed quantum-chest fix and checked-off v1.0.4 verification, remain in the archive.

### Equipment and Combat

- [ ] Confirm the Hoverpod torch toggles through its action and worn-pod quickslot, uses normal torch range without batteries, survives saves/travel, and switches off on removal or destruction. Old saves should default to off.
- [ ] Confirm DM3000's EXTRACT WATER repairs up to 5 HP per food serving without hunger, blaster charging or eating effects. Medigel must not heal it; quickslots extinguish and droplets fill the container even while damaged. Check the new acid action separately when implemented.
- [ ] Confirm Shapeshifter quickslots preserve special-item and equipment actions while ordinary weapons/blasters remain throwable. Unknown caches must apply their effect immediately, consume one item and normal use time, and sort revealed grenades into ordnance without losing quantities, quickslots or stacks when the kit is full.
- [ ] Confirm Command decontamination stations appear near the entrance on new and restored decks. Rinsing takes one turn, clears acid/fire and leaves water on ordinary floors; Medigel extinguishing uses one charge without healing.
- [ ] Confirm Repair Blaster door warnings cover doors along the beam path. Cancel must spend no charge or turn; Fire anyway must remain available.
- [ ] Confirm the life bar shows current HP only, and level-5 queen eggs/xenomorph spawns stay on walkable ground outside the sealed rescue chamber.

### Travel, Rescues and Saved Games

- [ ] Confirm maze routes through visible cells, diagonal movement, single steps into darkness, wall/step feedback and the yellow pulse beside the toolbar. Immediate tile taps were already confirmed; check that routes stop when visibility changes or torchlight expires, unlit terrain retains no exploration, and torch activation does not require equipment.
- [ ] Confirm boss-deck falls land in the sealed doorless chamber and Y arrives after 20 hero actions. Include restored phase-swapped boss maps, an on-demand final-boss chamber and deliberate boss detours entering through the normal arena approach.
- [ ] Complete the [staged Y rescue acceptance checks](docs/Y_RESCUE_PLAN.md): first-rescue guarantee and origin recovery; alien-planet visits and purchases; boss victory/defeat/reset; maze visibility and Hunter mapping; and the 1% Pixel Dungeon visit. Include save/reload, cargo and cat transfers.
- [ ] Confirm new runs reject unrelated deck files and produce normal loot from level 6 onward. Check legacy unmarked-deck handling, skipped unfinished bosses approached from below and current-run victory requirements for completed legacy boss decks.
- [ ] Confirm teleport-pad departure/arrival effects, locked departures, colours and travel/save behaviour across deck types, with Maintenance ladders retained. Confirm the implemented v1.0.6 filled centres in game, including dimmed locked pads and both Holodeck phases.
- [ ] Confirm the escape-pod ending text, cat variant, launch and stay buttons resolve correctly.

### Rooms and Visuals

- [ ] Confirm the Holodeck entrance surround and arena doorway use Habitat artwork in both power states, including restored floors. The unstable holo-projector quest room should use its own inactive grid and inspection text regardless of boss Holodeck power.
- [ ] Confirm exterior platforms look like exposed bridges and new layouts leave two tiles of open space at the tip; existing saved layouts should remain intact.
- [ ] Confirm shared hull corners retain interior fog while revealing only exterior hull walls; enclosed gaps stay covered. Check that Engineering decks, including floor 11, have no black exterior rectangles.
- [ ] Confirm Hunter detection uses green biological signatures, blue heap-item silhouettes and red trap silhouettes with each trap's artwork. Include unidentified caches, Pixel Dungeon artwork and the intended darkness/module/activation conditions.
- [ ] Review the refreshed Loader, Hoverpod, Jeda/Master, Hunter and Roman Soldier art in game: scale, canopy/occupant visibility, portraits, icons, animation alignment and weapon/claw clearance.
- [ ] Review DM3000's unarmoured and suit variants, integrated launcher arm, muzzle placement and descriptions; review the Bulwark Exosuit, Boarding Rig, Reactor Chassis and retained Shapeshifter art, including clone appearances.
- [ ] Confirm all 12 plasmid injectors and the green Medigel, blue Adrenal and purple Forcefield Stim icons, including splash colours and items restored from saves.

## Longer-term Backlog

- [ ] Download/upload save files (future paid feature).
- [ ] Custom asset skin uploads (future paid feature).
- [ ] Audit code cleanup and refactoring opportunities, then scope concrete improvements.

## Release Preparation

- [ ] Finish the selected v1.0.6 scope and relevant acceptance checks above; explicitly defer any remaining work rather than marking it complete.
- [ ] Remove the temporary debug starting loadout (identified Darksaber, Brightsaber and random working blaster) after item testing. Release builds already use their normal starting gear.
- [ ] Review save compatibility for changed items, immunity rules, cat state, Y flags and travel. Retain serialized aliases and provide safe defaults for older saves where needed.
- [ ] Update Android metadata to **1.0.6 (10006)**, the welcome summary and in-game changelog. Cover the archived v1.0.5 changes as well as new work if no intervening release shipped.
- [ ] Run relevant regression tests and build the APKs; record actual results and outstanding failures separately from gameplay/visual confirmation. Complete the signed minified release checks under Release Stability before shipping.
- [ ] Create the release and attach the tested APK, clearly identifying version, signing/build type and any remaining limitations.
