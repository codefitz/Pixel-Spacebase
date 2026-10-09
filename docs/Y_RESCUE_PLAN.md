# Y rescue expansion

Status: Stages 1–5 implemented; compilation and asset packaging passed. In-game acceptance checks remain pending.

## Agreed behaviour

- The first rescue in each run always uses the existing random ordinary station-level journey. The destination varies; the rescue format stays the same.
- Later rescues can send the hero to a rare encounter with the next boss, a somewhat more likely alien planet, a dark maze, or a very rare Pixel Dungeon first-floor visit.
- Every special journey returns to the level the hero occupied before the fall that required rescue. Preserve this origin throughout the detour. This follows the current saved fall-origin ticket; do not substitute the sealed landing chamber or the destination.
- For a boss detour, Y appears after victory or lethal defeat and returns the hero. Victory leaves that campaign boss defeated. Defeat leaves the boss alive, fully reset and ready for the later normal encounter.
- The alien planet has different tiles, friendly alien NPCs and a trader selling items for parts. Find Y to return.
- The maze is blacked out except for a visible exit and generic grey shapes on the hero's tile and its eight immediate neighbours when no torch is active. Use a torch or feel the way around. An equipped Hunter suit provides a 3D mapping overlay. Reaching the exit returns the hero.
- The Pixel Dungeon visit recreates its first-floor setting with original tiles and sprites while retaining Spacebase mechanics.

## Proposed defaults to refine during implementation

These are design proposals, not additional user requirements.

| Later-rescue outcome | Initial chance |
| --- | ---: |
| Ordinary station deck | 69% |
| Alien planet | 15% |
| Dark maze | 10% |
| Next boss | 5% |
| Pixel Dungeon visit | 1% |

Roll once per journey and save the result before travelling. Ineligible or unfinished outcomes fall back to the ordinary station route. Keep unfinished destinations disabled; retain the first-rescue guarantee regardless of weights. Track completed rescue departures separately from the temporary ticket so finding Y does not reset the first-rescue rule. Older saves need an explicit migration policy because they have no historical rescue count; conservatively treat them as first-rescue-eligible unless a saved active journey proves a departure occurred.

Return at a safe entrance-connected cell on the origin level, rather than the fall square. Keep player purchases, normal loot, consumed supplies and damage. On boss defeat, restore the hero to 25% health (at least 1 HP) and clear harmful effects and paralysis. Y extraction precedes clone consumption, death records and game deletion. Boss victory keeps actual remaining health.

For the Pixel Dungeon visit, propose finding Y near the downstairs exit to return; stairs must not start a separate Dungeon campaign. For planet and maze, ordinary death rules remain in effect unless changed explicitly.

## Starting implementation and constraints (before Stage 1)

- `Y.rescueStrandedHero()` chooses only ordinary station decks through `YRescuer.rescueDestinations()`. Bosses are deliberately excluded today.
- `YRescueJourney` saves only source and destination depths. `InterlevelScene.fall()` records the pre-fall origin; finding rescue Y returns to that origin entrance and removes the ticket.
- `SpacebaseRun.saveLevel()` stores levels by campaign depth. Planet, maze and Dungeon visits need separate saved identities so they cannot overwrite station decks, alter deepest-floor progress or unlock campaign content accidentally.
- `Hero.die()` processes clones and normal death. Boss extraction must happen before these irreversible steps, only for an active boss detour.
- Existing torch light and Hunter tracking provide useful hooks, but neither currently implements this maze or a 3D map.
- Level tile textures can be selected per level. Hero, enemy and item skins need explicit visit-scoped rendering so original sprites cannot leak onto station decks.

## Stage 1 — Shared journeys and first rescue

- [x] Add a persistent rescue departure count and versioned journey data: journey identity, destination type, origin level/cell, destination identity, phase and outcome.
- [x] Add a separate save namespace for future side levels and saved departure/arrival checkpoints. Alien side-level generation and travel are connected in Stage 2.
- [x] Centralise departure and return behaviour. Keep quest Y and rescue Y distinct and retain the current reluctant-return dialogue.
- [x] Make the first rescue always take the ordinary random route; save destination choice so reloading cannot reroll it.
- [x] Preserve the active origin through falls and ordinary travel. Visiting the origin completes the journey; becoming stranded again returns through Y to the original level. Persist travelling cat and workshop cargo. Side-level travel now returns through the saved origin, including recall and attempted campaign travel.
- [x] Keep later special destinations disabled until each is complete.

### Stage 1 implementation notes

- A rescue's game save includes the hero and current deck in one atomic checkpoint. Pending travel also saves an untouched destination and landing cell before cargo is delivered. Reload resumes the pending transfer and avoids collecting cargo twice.
- Completion is saved before the ticket is detached. Return prefers an unoccupied, passable entrance and otherwise an entrance-connected floor tile. No usable landing leaves the transition pending and reports an error rather than placing the hero incorrectly.
- A legacy ticket with a destination migrates to an active visit and proves at least one departure. A save without a departure count or active destination remains first-rescue-eligible.
- Ordinary station stairs and return items retain their existing behaviour while preserving the origin. Reaching that origin finishes the rescue. Stages 2–5 enable alien planets, boss detours, dark mazes and Pixel Dungeon visits.
- The existing rescue persistence fixture was adjusted for the new active-origin rule. No tests were added or executed. Java compilation passed; save/reload, cargo and follower behaviour still require the acceptance checks below.

Acceptance: a first rescue, a normal later rescue, and reloads before departure, during a visit and after return all preserve the source deck and a single journey. Normal station travel remains intact. Review old active tickets and saves without new fields.

## Stage 2 — Alien planet

- [x] Build a compact traversable planet with a dedicated tile atlas, recognisable landing site and friendly aliens.
- [x] Add a trader using parts, with saved finite stock, clear prices and normal inventory-capacity handling. Reuse existing purchase mechanics where possible.
- [x] Place Y somewhere reachable from the landing site. Give aliens short environmental dialogue and optional hints.
- [x] Disable ordinary station stair progression and return through Y.
- [x] Enable the planet outcome at 15% of later rescue departures. First rescues remain ordinary station journeys. In-game acceptance review is pending.

### Stage 2 implementation notes

- The colony uses its own saved identity (`alien_<journeyId>`), with the origin depth retained only as campaign context. Generating the planet bypasses normal deck generation, deepest-floor changes and limited progression drops.
- A 28×22 colony has violet outcrops, turquoise vegetation, a landing beacon, two friendly residents, a trader and Y to the east. The central route is kept clear. There are no hostile spawns or campaign stairs.
- The trader has one healing plasmid (140 parts), one torch battery (80), one food ration (90), and one polymer plasmid (110). Successful purchases save stock, inventory and parts together; insufficient parts or inventory space leave stock and currency unchanged.
- Planet saves do not receive items falling between station decks. Workshop cargo remains in its saved transfer buffer until there is a station workshop to receive it. Cat transfers use the existing saved journey flow.
- Recall and attempted interlevel travel from the colony use the original return ticket. The departure helper supports selecting the planet for development review after the first rescue, without changing release probabilities or overriding the first-rescue rule.
- [Artwork and generation prompts](artwork/alien-planet/README.md) are retained with the fitted assets. Compilation passed. No tests or in-game sessions were run; this is implementation evidence, not runtime confirmation.

Acceptance: every generated map has a route to Y; no accidental hostile aliens; purchases deduct parts once, survive reload and remain in inventory on return. Planet travel leaves campaign depth and station files intact.

This is the first new destination because it exercises shared side-level travel, persistence, NPCs and custom art without boss rollback or unusual visibility rules.

## Stage 3 — Next-boss detour

- [x] Select the next undefeated campaign boss ahead of the origin level, including the core boss. Skip defeated bosses and the powered-down holodeck; use an ordinary rescue if none is eligible or the next fight has already started.
- [x] Copy or generate the real campaign arena in a separate rescue save and preserve an untouched pre-fight snapshot. Existing partial encounters are left intact and cause an ordinary-rescue fallback.
- [x] On victory, commit the defeated arena and its remaining rewards, show Y and return. Prevent stair progression and ending transitions during the visit.
- [x] On lethal defeat, intercept normal death, restore 25% health and clear harmful effects, show Y and return. Restore the arena snapshot, including boss health, phases, allies, doors and hazards.
- [x] Track collected starting-arena items and remove those quantities from the restored snapshot. Retain the hero's inventory and experience; consumed supplies remain spent.
- [x] Save the resolved outcome after the current action finishes, before Y's extraction dialogue. Persist the return and arena commit so reloading can resume them.

### Stage 3 implementation notes

- Stage 3 enabled 80% ordinary station, 15% alien planet and 5% boss. Stage 4 reallocates 10% from ordinary station to the maze. The first rescue remains ordinary.
- The next undefeated boss is selected from depths 5, 10, 15, 20 and 25. Existing arena metadata is read without constructing actors. A started next encounter causes fallback rather than skipping ahead to another undefeated boss.
- The fight runs under `boss_<journeyId>`. Campaign files remain untouched during combat. Arrival enters the arena's normal encounter logic without advancing deepest-floor progress or consuming stored bones.
- Victory writes the resolved arena to its campaign depth. Rewards already collected remain with the hero; remaining drops wait in that arena for a normal visit. Y appears after the current action finishes, including the security encounter's final drops.
- Defeat protects clones and normal death records. The hero recovers to a quarter of maximum health, clears harmful buffs and paralysis, and returns. Recall or other early withdrawal also restores the pre-fight arena.
- Collected starting-arena loot is tracked by saved item identities, including partial quantities. Rollback removes those quantities from the arena, preventing restored copies while retaining player inventory. Newly generated combat loot and experience remain with the hero; retries can therefore provide additional combat rewards.
- Travelling cats and workshop cargo are removed before committing the arena. Return prefers the origin entrance; if a sealed arena has removed it, the saved origin area supplies a usable floor landing.
- Resolution pauses further actor actions, saves the hero and completed arena action together, and then displays Y. Reloads resume extraction or the saved return. Compilation passed; no tests or in-game sessions were run.

Acceptance: win and lose against each supported boss, then revisit normally. Victory remains permanent; defeat produces a fresh ready encounter. Review clones, lingering damage, multiple boss phases, rewards, reload during resolution and already visited arenas.

## Stage 4 — Dark maze and Hunter mapping

- [x] Generate a bounded maze with a guaranteed route from spawn to exit, without campaign stairs or unreachable exit.
- [x] Keep an exit beacon visible above darkness and provide a direction indicator when it is outside the view. Unlit terrain, including previously explored cells, stays black.
- [x] Reveal local terrain with the existing active torch light, duration and batteries. Provide individual cardinal movement and wall feedback for travel without a torch.
- [x] Interpret map taps as one cardinal step toward the selected tile, including distant and diagonal taps with or without light. Keep item targeting separate; never compute an automatic route through the maze.
- [x] Prevent automatic hero pathfinding, ordinary mapping, awareness and sensor overlays from revealing hidden topology.
- [x] With Hunter capability equipped, show a projected 3D wireframe of nearby scanned geometry. Use the shared armor capability so upgraded suits retaining Hunter tracking qualify too.
- [x] Trigger the saved return flow on reaching the exit, after the current actor action and movement finish.

### Stage 4 implementation notes

- Stage 4 enabled 70% ordinary station, 15% alien planet, 10% dark maze and 5% boss. Stage 5 reallocates 1% from ordinary station to Pixel Dungeon. Development selection accepts `DARK_MAZE` after the first rescue.
- A 25×19 maze is carved as a connected tree of narrow corridors. The exit is the farthest floor cell from the entrance by cardinal traversal. Its map and scans use a separate `maze_<journeyId>` save. Creation bypasses limited station drops, bones, enemy spawning and campaign progress.
- The maze is breathable and has no generated enemies or traps. Hunger, supplies, carried companions and normal death/clone rules still apply. A torch is useful but is not required to move or reach the exit.
- Fog ignores explored terrain, mapping flags, brightness overrides and the normal station hull reveal. Without active light, the hero, exit beacon and a moving 3×3 patch of generic grey floors/walls are drawn above it. These nearby shapes use no station artwork and do not retain explored terrain or change targeting sight. The patch hides when a utility torch or Hoverpod torch is active. The exit direction indicator provides a bearing, without a route or distance.
- Torch light reveals the normal four-cell radius with line of sight. A carried utility torch can be switched on from inventory or quickslot without equipping it; the active Hoverpod torch also illuminates the maze. Terrain goes black again outside that radius or when light expires; torch detachment now removes the light buff before refreshing sight. Innate robot vision, room lighting, awareness and mind vision do not illuminate the maze.
- Movement uses map taps for single north/south/east/west steps, including under torchlight. Distant taps take one step toward the selected tile without calculating a hidden route; diagonal taps select one cardinal direction and cannot cut corridor corners. A status line above the toolbar records successful movement and wall collisions, and a small pulsing marker keeps the hero legible over black terrain. Hitting a wall gives tactile feedback. Normal station movement is unchanged.
- The Hunter display projects a local isometric floor grid and raised wall outlines, with the hero in yellow at the centre. Its four-cell scan radius can penetrate nearby walls. Scan history is saved separately from terrain visibility and retained when equipment is removed; the overlay hides immediately, then restores when qualifying armor is equipped again. The viewport shows retained cells up to five cells from the hero, without a whole-maze overview or automatic navigation.
- Ordinary mapping upgrades report scrambled signals and consume their usual read; the Surveyor's active scan reports the interference without spending its charge. Ordinary item/trap markers and signature overlays are disabled during the maze visit. Hidden cells cannot be examined remotely.
- The exit works while walking, flying or being repositioned. It pauses further actor actions and returns through the original journey ticket once movement finishes. Saved exit arrivals resume the same return after reload. No rescue Y is spawned inside the maze; recall retains the shared early-return behaviour.
- Java compilation passed. No tests or in-game sessions were run; torch expiry, equip/unequip, overlay readability, exit travel and reload behaviour still require the acceptance review below.

Acceptance: exit visible without torch, no hidden route leaks, maze solvable without equipment, torch on/off and battery exhaustion behave correctly. Hunter equip/unequip and reload preserve the agreed map rules. Inspect the overlay in game for readability.

## Stage 5 — Pixel Dungeon easter egg

- [x] Pin the upstream revision and retain original asset provenance, file hashes and license with the imported art.
- [x] Generate a dedicated first-floor sewer setting with connected rooms, doors, mossy floors and water.
- [x] Map original terrain, all four hero appearances, compatible creatures and displayed items. Document consistent fallbacks for Spacebase-only content.
- [x] Apply the original art only during the saved visit, including after reload. Keep Spacebase combat, inventory, controls, equipment capabilities and death rules.
- [x] Place Y beside the downstairs exit and return through the shared journey. Enable at 1% of later rescues; first rescues remain ordinary.

### Stage 5 implementation notes

- Later rescues now use the full proposed weights: 69% ordinary station, 15% alien planet, 10% dark maze, 5% next boss and 1% Pixel Dungeon. The destination is saved before travel, and development selection accepts `PIXEL_DUNGEON` after the first rescue.
- Imported 13 unchanged PNGs from upstream revision `ca458a28f053612973d5d6059dae5f6f2ca4fcb7`. [Artwork mappings, provenance and license](artwork/pixel-dungeon/README.md) accompany the assets, including the original 8-column item atlas and explicit animation frame sizes.
- The 32×32 sewer visit has nine random rooms joined by a guaranteed connected network, original green water and grass artwork, six low-deck Spacebase enemies rendered as rats and gnolls, and three normal supplies. It recreates the recognisable first-floor setting; upstream first-floor maps are generated rather than one fixed layout.
- The separate `dungeon_<journeyId>` namespace preserves the origin. Generation bypasses campaign limited drops, bones and deepest-floor progress. No Dungeon campaign, quests or enemy respawner are introduced.
- Hero skins map Commander to Warrior, DM3000 to Mage, Shapeshifter to Rogue and Captain to Huntress. Armor rows, status avatars and item forms use original art. Loader/HoverPod and upgraded suit capabilities retain their Spacebase behaviour while using the visit appearance.
- Creature rendering follows original animation layouts while retaining Spacebase actor classes, AI, statistics and rewards. Rescue Y uses the shopkeeper; the travelling cat uses the original ending pet. Compatible other friendlies use sheep and hostiles use gnolls. Specialised boss/quest animation APIs keep native compatible sprites; those actors are not generated here.
- Item rendering maps native image identities to original weapons, armor, wands, rings, potions, scrolls, supplies and containers. Unmapped Spacebase items use the original unknown-item icon. Glows and combat effects retain their native behaviour. No saved item identity or global artwork constant is rewritten.
- Y waits adjacent to the downstairs stair and uses a nostalgic reluctant-return line. The stairs cannot start a Dungeon campaign; ordinary side-level recall and attempted interlevel travel return to the saved origin. Equipment, loot, damage and consumed supplies persist.
- Terrain, hero, creature, item and feature rendering select visit art from the actual saved level type and side namespace. Returning creates the normal station scene with its normal art. Java compilation and Android asset merging passed. No tests or in-game sessions were run.

Acceptance: visit and reload with each hero appearance, fight and collect items using Spacebase behaviour, then return with station art restored. Check that leaving no longer retains any Dungeon texture selections.

## Delivery boundaries

### Skipped campaign boss decks (5 October 2026)

- Device save inspection found floor 5 with no boss and an unlocked exit, despite no first-boss victory in the current run. Rescue travel's file-existence check could reuse decks from an abandoned earlier run.
- New games clear deck files for their own hero slot. Newly saved decks and committed boss detours include the run seed; gap travel and boss-detour selection reject decks belonging to another run.
- Legacy completed boss decks at floors 5–20 are accepted only with their corresponding local victory badge. An empty legacy floor-5 arena without that badge is regenerated on the next visit. Legacy final-boss victories are preserved because that encounter has no equivalent local boss badge. Legacy ordinary decks and unfinished arenas retain their existing state.
- Entering an unfinished boss deck from a later floor uses its normal entrance, with a safe arena landing if the entrance is sealed. Completed decks still use their exit for upward travel.
- `:core:assembleDebug` passed. Device saves were read without modification; no tests or in-game recovery/travel checks were run.
- Follow-up ownership audit added the same seed validation to direct reloads, including side-deck files. New runs persist `allowLegacyDecks=false` and reject unmarked files even if old-file cleanup failed. Existing older games persist compatibility mode so their unmarked explored decks remain loadable; ownership of those legacy files cannot be proven retroactively.
- Four focused `DeckOwnershipTest` cases passed, covering same-run revisits, other-run rejection in both modes, strict rejection of unmarked files, and legacy compatibility. The APK also built successfully. This verifies the ownership policy, not an in-game loot/reload session. Missing items on previously explored reused decks remain a plausible consequence; already contaminated legacy ordinary decks are not automatically reset.

### Rescue travel crash correction (5 October 2026)

- The connected device reported a crash after closing Y's rescue dialogue. The destination checkpoint serialized `YRescueJourney.arrivalLevel`, and `Blob.storeInBundle()` dereferenced the active level while travel had cleared it.
- Environmental effects now serialize using their own cell-array length. This also handles saved destination levels whose dimensions differ from the active level, without changing the save format. Empty effect arrays are omitted safely.
- `:core:assembleDebug` passed. No tests or in-game rescue/reload checks were run for this correction; recovery of the interrupted journey remains unverified.
- A second device crash occurred during destination scene creation: `Tilemap` could not find frame zero. `TextureCache` was applying the station's four-pixels-per-unit scale to the native 32×32 alien terrain atlas, reducing its logical dimensions below a single 16×16 tile. Native alien terrain, the colonist sprite and original `pixel_dungeon/` art now retain scale one. The existing enlarged Security arena artwork retains scale four.
- The APK containing both corrections built successfully with `:core:assembleDebug`. The second correction has not been checked in game.
- The player subsequently confirmed that the rescue crashes are resolved.
- Stranded-room timing now counts positive-time hero actions rather than periodic world ticks, preventing an immediate landing count and multiple counts for slow actions. The saved `turns` field is retained. In-game timing confirmation remains pending.

Ship and review one stage at a time. Each destination should be force-selectable through development tooling for review without changing release probabilities. Gameplay checks above are planned acceptance work, not checks already executed. Update release scope after each stage; all five stages need not ship in 1.0.5.

**Next:** in-game acceptance review across all five rescue stages, especially appearance changes, save/reload, purchases, boss win/loss rollback, torch/scan behaviour and station return.
