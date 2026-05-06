# Naming and Refactor Audit

This audit separates player-facing cleanup from internal compatibility names. The codebase still inherits many Shattered Pixel Dungeon class names, resource keys, and save identifiers. Some should be renamed eventually, but broad package/class renames should be staged behind compatibility checks because saved games, bundles, reflection, assets, and message keys can depend on them.

## Current Naming Policy

- Player-facing text should use Pixel Spacebase terminology.
- Internal class names may remain inherited when they are tied to saves, reflection, resource lookup, or large mechanics.
- When keeping an inherited internal name, document the current player-facing concept in `docs/ENTITY_BEHAVIOR.md`.
- Prefer wrapper aliases, comments, and new docs before renaming deeply connected classes.
- Rename internal classes only when the save/resource migration path is clear and the diff can be tested in isolation.

## Keep Internally For Now

| Internal name | Player-facing concept | Reason to keep for now |
| --- | --- | --- |
| `Dungeon` | Pixel Spacebase run/session state | Extremely central API; renaming would touch nearly every system. |
| `Level`, `depth`, `floor` | Deck/level | Deeply embedded in save and progression logic. Keep internal, use deck in UI where practical. |
| `PrisonLevel`, `CavesLevel`, `CityLevel`, `HallsLevel` | Security Block, Lower Engineering, Habitation/Command, Deep Containment | These classes map inherited tilesets and level-gen bands. Rename later only with asset/resource cleanup. |
| `Amulet`, `AmuletScene` | Escape Pod Override / launch scene | Save identity and victory flow. Player-facing text is converted. |
| `King` | Holodeck Monarch | Boss mechanics and summon flow are inherited. Player-facing conversion is done. |
| `Skeleton` | Ruptured Crew Suit | Mechanics and sprite still inherited. Player-facing text is converted. |
| `Spinner` | Facehugger | Mechanics have been converted to xeno infection latch. Sprite/class can be renamed later. |
| `Bat` | Siphon Drone | Player-facing text is converted; repair blaster machine behavior now matches. |
| `Squiddard` | Replicator Swarm | Player-facing naming exists, but class/sprite need later cleanup. |
| `ExperimentalTech` | Gene Mods | Package name is awkward but mechanically broad. Keep as an internal compatibility name; add local comments instead of migration now. |
| `Script` | Tech | Same as above; many generators, containers, and UI paths use this package. Keep as an internal compatibility name for now. |
| `EMP` | Repair Blaster | Startup and player-facing text are repair-themed. Keep internal name; a wrapper would split save/message identities without enough benefit right now. |
| `Torch` | Future permanent light/battery item | Current inherited item still exists and is on the plan for redesign. Mark as legacy until the new mechanic is built. |

## Safe Cleanup Targets

These are low-risk because they are mostly resource strings, docs, or small aliases:

- Generic terrain descriptions that still mention stairs, wooden barricades, or bookshelves.
- Journal feature labels that still mention inherited NPC names.
- Entity catalogue entries that no longer match current mechanics.
- Message comments with stale TODO wording, once the actual behavior has moved on.
- Player-facing area names in docs: prefer Security Block, Lower Engineering, Habitation/Command, Deep Containment, and Evacuation Cradle.

## Higher-Risk Refactor Targets

These should be done one at a time with a build after each:

- Rename `EMP` to a repair-blaster class only if a future blaster package cleanup includes save and message-key migration. Do not add a wrapper in the current codebase.
- Rename `Amulet`/`AmuletScene` only if old save class-name compatibility is handled.
- Rename mob classes such as `King`, `Spinner`, `Bat`, `Squiddard`, and `Skeleton` only after checking bundle serialization and resource key lookup.
- Rename package `items.ExperimentalTech` to `items.genemods`; the uppercase package name is nonstandard Java style but widely referenced. Current decision: leave as internal compatibility naming.
- Rename package `items.scripts` to `items.tech`; broad but conceptually clean. Current decision: leave as internal compatibility naming.
- Rename tileset asset constants from `PRISON/CAVES/CITY/HALLS` to current area names. This touches assets, level classes, and docs.

## Unused Or Legacy Mechanics To Mark Before Removing

- `Torch`: inherited light item. Keep until the battery/torch plan item replaces it.
- `XenoSkull`: explicitly retained for old quest save support.
- `SewerLevel` message keys: no `SewerLevel` class remains. They are legacy fallback strings next to Operations resources; keep marked as legacy unless runtime lookup proves they are dead.
- `WndBlacksmith`: UI class remains useful for Leonard-style rebuild/reforge mechanics; player-facing text should avoid blacksmith language.
- Inherited copyright headers and original project names should stay as license provenance, not conversion debt.

## Immediate Audit Result

The biggest remaining naming debt is not player-facing text; it is internal compatibility naming. The safest next refactor is to add explicit compatibility notes and small wrapper names around high-value concepts, then slowly migrate isolated systems.
