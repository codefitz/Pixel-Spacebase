# Naming and Refactor Audit

This audit separates player-facing cleanup from internal compatibility names. The pre-release internal rename pass intentionally broke old save compatibility so central classes, resource keys, and asset names could move toward Pixel Spacebase terminology.

## Current Naming Policy

- Player-facing text should use Pixel Spacebase terminology.
- Internal class names may remain inherited only where they are still tied to broad mechanics or later package cleanup.
- When keeping an inherited internal name, document the current player-facing concept in `docs/ENTITY_BEHAVIOR.md`.
- Prefer direct renames during pre-release when tests can prove the affected flow still compiles and loads.

## Keep Internally For Now

| Internal name | Player-facing concept | Reason to keep for now |
| --- | --- | --- |
| `Level`, `depth`, `floor` | Deck/level | Deeply embedded in save and progression logic. Keep internal, use deck in UI where practical. |
| `HabitationCommandLevel`, `DeepContainmentLevel` | Habitation/Command, Deep Containment | These classes still map inherited tilesets and level-gen bands. Rename in a later isolated pass with resource cleanup. |
| `Spinner` | Facehugger | Mechanics have been converted to xeno infection latch. Sprite/class can be renamed later. |
| `Bat` | Siphon Drone | Player-facing text is converted; repair blaster machine behavior now matches. |
| `Squiddard` | Replicator Swarm | Player-facing naming exists, but class/sprite need later cleanup. |
| `Plasmid` | Gene Mods | Package name is awkward but mechanically broad. Keep as an internal compatibility name; add local comments instead of migration now. |
| `Upgrade` | Tech | Same as above; many generators, containers, and UI paths use this package. Keep as an internal compatibility name for now. |
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
- Rename mob classes such as `Spinner`, `Bat`, and `Squiddard` after checking bundle serialization and resource key lookup.
- Rename package `items.plasmids` to `items.genemods`; the uppercase package name is nonstandard Java style but widely referenced. Current decision: leave as internal compatibility naming.
- Rename package `items.upgrades` to `items.tech`; broad but conceptually clean. Current decision: leave as internal compatibility naming.
- Rename tileset asset constants from `PRISON/CAVES/CITY/HALLS` to current area names. This touches assets, level classes, and docs.

## Unused Or Legacy Mechanics To Mark Before Removing

- `Torch`: inherited light item. Keep until the battery/torch plan item replaces it.
- `XenoSkull`: explicitly retained for old quest save support.
- `SewerLevel` message keys: no `SewerLevel` class remains. They are legacy fallback strings next to Operations resources; keep marked as legacy unless runtime lookup proves they are dead.
- `WndBlacksmith`: UI class remains useful for Leonard-style rebuild/reforge mechanics; player-facing text should avoid blacksmith language.
- Inherited copyright headers and original project names should stay as license provenance, not conversion debt.

## Immediate Audit Result

The biggest remaining naming debt is now the long-tail inherited systems: broad package names such as `Plasmid` and `items.upgrades`, legacy enemy classes such as `Spinner`, `Bat`, and `Squiddard`, and area classes/assets for `HabitationCommandLevel` and `DeepContainmentLevel`. The central run class, tilemap, seed helper, Security/Engineering level classes, major bosses, several enemies, the escape-pod override, medigel droplet, training manual, hologram emitter, master keycard, and equippable-module package have been renamed.
