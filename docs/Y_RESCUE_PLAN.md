# Y rescue expansion

Status: Stages 1–3 implemented; compilation passed. In-game acceptance checks remain pending. Stages 4–5 are planned.

## Agreed behaviour

- The first rescue in each run always uses the existing random ordinary station-level journey. The destination varies; the rescue format stays the same.
- Later rescues can send the hero to a rare encounter with the next boss, a somewhat more likely alien planet, a dark maze, or a very rare Pixel Dungeon first-floor visit.
- Every special journey returns to the level the hero occupied before the fall that required rescue. Preserve this origin throughout the detour. This follows the current saved fall-origin ticket; do not substitute the sealed landing chamber or the destination.
- For a boss detour, Y appears after victory or lethal defeat and returns the hero. Victory leaves that campaign boss defeated. Defeat leaves the boss alive, fully reset and ready for the later normal encounter.
- The alien planet has different tiles, friendly alien NPCs and a trader selling items for parts. Find Y to return.
- The maze is blacked out except for a visible exit. Use a torch or feel the way around. An equipped Hunter suit provides a 3D mapping overlay. Reaching the exit returns the hero.
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
- Ordinary station stairs and return items retain their existing behaviour while preserving the origin. Reaching that origin finishes the rescue. Stage 2 enabled the alien planet weight; Stage 3 also enables boss detours. Maze and Dungeon visits remain disabled.
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

- Later rescues now roll 80% ordinary station, 15% alien planet and 5% boss. The first rescue remains ordinary. The eventual proposed weights above still include the two unfinished destinations.
- The next undefeated boss is selected from depths 5, 10, 15, 20 and 25. Existing arena metadata is read without constructing actors. A started next encounter causes fallback rather than skipping ahead to another undefeated boss.
- The fight runs under `boss_<journeyId>`. Campaign files remain untouched during combat. Arrival enters the arena's normal encounter logic without advancing deepest-floor progress or consuming stored bones.
- Victory writes the resolved arena to its campaign depth. Rewards already collected remain with the hero; remaining drops wait in that arena for a normal visit. Y appears after the current action finishes, including the security encounter's final drops.
- Defeat protects clones and normal death records. The hero recovers to a quarter of maximum health, clears harmful buffs and paralysis, and returns. Recall or other early withdrawal also restores the pre-fight arena.
- Collected starting-arena loot is tracked by saved item identities, including partial quantities. Rollback removes those quantities from the arena, preventing restored copies while retaining player inventory. Newly generated combat loot and experience remain with the hero; retries can therefore provide additional combat rewards.
- Travelling cats and workshop cargo are removed before committing the arena. Return prefers the origin entrance; if a sealed arena has removed it, the saved origin area supplies a usable floor landing.
- Resolution pauses further actor actions, saves the hero and completed arena action together, and then displays Y. Reloads resume extraction or the saved return. Compilation passed; no tests or in-game sessions were run.

Acceptance: win and lose against each supported boss, then revisit normally. Victory remains permanent; defeat produces a fresh ready encounter. Review clones, lingering damage, multiple boss phases, rewards, reload during resolution and already visited arenas.

## Stage 4 — Dark maze and Hunter mapping

- [ ] Generate a bounded maze with a guaranteed route from spawn to exit. No ordinary stairs or unreachable exit.
- [ ] Mark the exit through darkness without revealing the route. Keep unexplored walls and corridors black; decide whether explored cells remain visible or fade again and document it.
- [ ] Let active torch light reveal local terrain using existing light duration and battery mechanics. Preserve tactile movement feedback so the maze remains completable without a torch.
- [ ] Prevent auto-pathfinding, minimaps and normal sensor overlays from exposing hidden maze topology unintentionally.
- [ ] With Hunter capability equipped, render a projected 3D wireframe of scanned nearby maze geometry, aligned with movement and walls. This is a visual navigation aid over the existing grid. Define scan radius and accumulated-map behaviour; extend existing upgraded-suit Hunter capability handling consistently.
- [ ] Trigger return on the exit tile, with the same saved return flow as other journeys.

Acceptance: exit visible without torch, no hidden route leaks, maze solvable without equipment, torch on/off and battery exhaustion behave correctly. Hunter equip/unequip and reload preserve the agreed map rules. Inspect the overlay in game for readability.

## Stage 5 — Pixel Dungeon easter egg

- [ ] Pin an upstream revision and inventory the first-floor art actually needed. Keep original asset provenance and attribution with imported files.
- [ ] Create a dedicated first-floor/sewer-style map. Pixel Dungeon's first floor is generated, so recreate its recognisable setting rather than assuming one fixed canonical layout.
- [ ] Map original terrain cells and sprite animations into this game's formats. Maintain a list of mappings for hero, creatures and displayed items; provide consistent fallbacks for Spacebase-only equipment and effects.
- [ ] Apply the original art only while on this visit, including after reload. Preserve Spacebase combat, inventory and controls.
- [ ] Put Y near the exit and return through the shared journey flow. Enable only after all earlier stages are stable.

Upstream source: [watabou/pixel-dungeon](https://github.com/watabou/pixel-dungeon). Its [Assets.java](https://github.com/watabou/pixel-dungeon/blob/master/src/com/watabou/pixeldungeon/Assets.java) names the sewer atlas `tiles0.png`, water `water0.png`, and separate hero, creature and item sheets. This confirms a useful art starting point; atlas indices and animation compatibility still require inspection before importing.

Acceptance: visit and reload with each hero appearance, fight and collect items using Spacebase behaviour, then return with station art restored. Check that leaving no longer retains any Dungeon texture selections.

## Delivery boundaries

Ship and review one stage at a time. Each destination should be force-selectable through development tooling for review without changing release probabilities. Gameplay checks above are planned acceptance work, not checks already executed. Update release scope after each stage; all five stages need not ship in 1.0.5.

**Next:** Stage 4 — the dark maze and Hunter mapping overlay. The implemented stages still need their in-game acceptance checks.
