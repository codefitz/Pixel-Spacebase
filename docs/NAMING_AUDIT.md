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
- Rename remaining legacy mob classes after checking bundle serialization and resource key lookup.
- Rename package `items.plasmids` only if the design later moves away from the word "plasmid". Current decision: keep.
- Rename package `items.upgrades` only if the design later moves away from the word "upgrade". Current decision: keep.
- Rename any remaining legacy tileset constants after checking asset references and docs.

## Unused Or Legacy Mechanics To Mark Before Removing

- `Torch`: inherited light item. Keep until the battery/torch plan item replaces it.
- `XenoSkull`: explicitly retained for old quest save support.
- Legacy area message keys should be removed once runtime lookup proves they are dead.
- `WndBlacksmith`: UI class remains useful for Leonard-style rebuild/reforge mechanics; player-facing text should avoid blacksmith language.
- Inherited copyright headers and original project names should stay as license provenance, not conversion debt.

## Immediate Audit Result

The biggest remaining naming debt is now the long-tail inherited systems that are still intentionally deferred: broad mechanics such as `Level`, `depth`, `floor`, `EMP`, and `Torch`, plus any remaining legacy enemy classes not yet worth isolating. The backpack extension classes, main plasmid/upgrade classes, story chapter IDs, area level classes, and the Siphon Drone / Facehugger / Replicator Swarm classes are now internally consistent.
