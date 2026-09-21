# Naming and Refactor Audit

This audit separates player-facing cleanup from internal compatibility names. New internal renames must register the former fully qualified class name with `Bundle.addAlias` so existing saves remain loadable.

## Current Naming Policy

- Player-facing text should use Pixel Spacebase terminology.
- Internal class names may remain inherited only where they are still tied to broad mechanics or later package cleanup.
- When keeping an inherited internal name, document the current player-facing concept in `docs/ENTITY_BEHAVIOR.md`.
- Prefer direct renames during pre-release when tests can prove the affected flow still compiles and loads.

## Keep Internally For Now

| Internal name | Player-facing concept | Reason to keep for now |
| --- | --- | --- |
| `Level`, `depth`, `floor` | Deck/level | Deeply embedded in save and progression logic. Keep internal, use deck in UI where practical. |
| `Plasmid` | Plasmids / genetic alterations | Accepted current terminology. Package and class names now match the inventory plan. |
| `Upgrade` | Tech upgrades | Accepted current terminology. Package and class names now match the inventory plan. |
| `EMP` | Repair Blaster | Startup and player-facing text are repair-themed. Keep internal name; a wrapper would split save/message identities without enough benefit right now. |
| `Torch` | Future permanent light/battery item | Current inherited item still exists and is on the plan for redesign. Mark as legacy until the new mechanic is built. |

## Safe Cleanup Targets

These are low-risk because they are mostly resource strings, docs, or small aliases:

- Generic terrain descriptions that still mention inherited fantasy framing.
- Journal feature labels that still mention inherited NPC names.
- Entity catalogue entries that no longer match current mechanics.
- Message comments with stale TODO wording, once the actual behavior has moved on.
- Player-facing area names in docs: prefer Security Block, Lower Engineering, Habitation/Command, Deep Containment, and Evacuation Cradle.

## Higher-Risk Refactor Targets

These should be done one at a time with a build after each:

- Rename `EMP` to a repair-blaster class only if a future blaster package cleanup includes save and message-key migration. Do not add a wrapper in the current codebase.
- Rename remaining legacy mob classes only after checking bundle serialization and resource-key lookup.
- Rename package `items.plasmids` only if the design later moves away from the word "plasmid". Current decision: keep.
- Rename package `items.upgrades` only if the design later moves away from the word "upgrade". Current decision: keep.
- Rename any remaining legacy tileset constants after checking asset references and docs.

## Unused Or Legacy Mechanics To Mark Before Removing

- `Torch`: inherited light item. Keep until the battery/torch plan item replaces it.
- `XenoSkull`: explicitly retained for old quest save support.
- Legacy area message keys should be removed once runtime lookup proves they are dead.
- `WndBlacksmith`: UI class remains useful for Leonard-style rebuild/reforge mechanics; player-facing text should avoid blacksmith language.
- Inherited copyright headers and original project names should stay as license provenance, not conversion debt.

## Completed Entity Renames

| Former class | Current class |
| --- | --- |
| `Crab` / `GreatCrab` | `MaintenanceCrawler` / `ArmoredCrawler` |
| `Brute` / `Shielded` | `OuterColonyShockTrooper` / `ShieldedShockTrooper` |
| `Guard` | `BithAcolyte` |
| `Thief` / `Bandit` | `Jawar` / `JawarScavenger` |
| `Gunsmith` | `Quartermaster` |
| `Arp` / `ArpTrader` | `Y` / `YTrader` |
| `OperationsLevel` / `OperationsBossLevel` | `MaintenanceLevel` / `MaintenanceBossLevel` |
| `Bat` | Removed in favour of `SiphonDrone` |
| `Wraith` | Removed in favour of the current mobile-turret implementation |

The associated sprite classes, message prefixes, window classes, and Maintenance texture constants now use the same terminology. Compatibility aliases cover every renamed persisted mob, NPC, and level class.

## Immediate Audit Result

The biggest remaining naming debt is now the intentionally deferred broad mechanics: `Level`, `depth`, `floor`, `EMP`, `Torch`, and a few save-facing journal or quest node identifiers. Entity classes, area level classes, sprite classes, message prefixes, and the primary item families now use Pixel Spacebase terminology.
