# Targeted Release: v1.0.4

Use this as the working backlog for the next sprint. Put rough ideas in Suggestions first, then move them into the relevant section once the direction is clear enough to implement and test.

## Suggestions

## Mechanics

- [x] Station cat waits on every level until touched, letting the player choose whether it follows and takes risks.
- [x] Following station cat bats one module, equippable module, or tech upgrade per regular room; it leaves ordinary loot and food alone.
- [x] A following station cat can scare a nearby signal siren into fleeing.
- [x] Alter torch to be equipped in quickslots to activate
- [x] Wormhole generator: equip it and spend one utility battery to teleport to a random unoccupied location on the current level; the device has no recharge.
- [x] A battery doused with medigel can explode (locked doors, blockage)
- [ ] Holodeck / Lazer quest shutdown sequence:
    - [x] Use the attached legacy `old_tiles/tiles3.png` as the active boss-level holodeck atlas, enlarged 4x with nearest-neighbour scaling.
    - [x] Re-theme the current corpse-room bodies and Lazer quest prop as convincing holodeck projections/equipment while preserving the room's reward and pickup flow.
    - [x] Add Roman-style holographic patrol mobs to eligible habitation-ring levels while the holodeck remains active.
    - [x] Treat collection of the sixth `HardLightEmitter` (Y's existing requirement) as the shutdown moment, independently of handing the emitters to Y.
    - [x] At shutdown, stop future patrol spawns, remove patrol projections when their saved floors are revisited, and switch the holodeck to a black field with yellow grid boxes.
    - [x] Persist the shutdown state across saves and make newly generated and already-generated holodeck areas show the correct phase.
    - [x] Keep Y's existing six-emitter reward exchange available after shutdown.
    - [x] Let roaming Roman holograms drop emitters on normal habitat levels.
    - [x] Open Y's workshop floor only when six emitters were collected before the boss.
    - [x] Give the boss a Caesar sprite and its summons Roman legionary sprites.
    - [ ] Verify the 5-to-6 emitter transition, save/restore in both phases, projection cleanup, reward exchange, and both visual states.
- [x] Be able to render the drone controller.
- [ ] Download/Upload save files.
- [ ] New Suit Mechanics:
    - [x] Hunter Suit signature scanner shows moving red silhouettes for enemies and NPCs while floor lights are off, without revealing terrain or extending targeting sight.
    - [x] Hunter Suit jetpack can be switched on or off for unlimited flight while equipped.
    - [x] Hunter has +1 accuracy with ranged weapons.
    - [x] Hoverpod absorbs hits and non-explosive traps, has visible integrity, and breaks down after 15 hits. Repair Blaster restores it; combining two strengthens it by one hit.
    - [ ] Heavy loader high protection, smash locked/damaged doors, can fire weapon but whilst wearing weapon slot is equipped with loader arm (upgradeable).
    - [x] Space suit and Hunter Suit are immune to harmful gases.
    - [x] Being unarmored grants 50% faster movement and 25% greater dodge.
    - [x] Unprovoked robots ignore a hero in uniform; each remembers a hero attack across saves.
    - [ ] Only space suit, hunter, hoverpod and the special suit allow breathing in space.
      - [ ] Exception: DM3000 doesn't need to breath so can wear anything.
  - [ ] Repair blaster can lock doors as well as open locked ones.
  - [ ] Fix: Cat is not going or meat.
  - [ ] Have a rare item (x2 per game) that will upgrade everything currently carried or equipped.

## Cosmetic / Narrative

- [x] Chasms/black should have a subtle starry background, with potentially a planet and nearby star.
- [x] Give Maintenance wall lamps a subtle flicker.
- [x] Refresh Engineering's salvageable wall panels.
- [x] Upgrade the Habitation Ring terrain tileset.
- [x] Name the later area Command in documentation and player-facing chapter copy.
- [ ] Have a 'now playing' for music.
- [x] Challenges Icon update
- [ ] Custom skin tiles
- [x] Armor Kit needs icon sci-fi upgrade
- [x] Rename of icon files
- [ ] Fix swarm sprite - crap
- [x] Swap wall tiles in security
- [x] Hero reacts when Y confiscates a weapon during the security maze and when it is recovered after the Masked Prisoner fight.
- [ ] Add room with no doors. These can be accessed by dropping into them, they may contain a high-value item or nothing. However there is no escape unless you have an item that can teleport you out.
- [x] Show the initial Xeno infection reaction only once per run; retain the warning and burst reactions for later infections.
- [x] Y's habitat and workshop sprites match his security boss appearance.
- [x] A Shapeshifter throwing a blaster discharges an energy beam at every hostile target in the impact room, reducing each to 0 HP. The blaster is consumed.
- [x] Torch battery indicator.

## Art

- [ ] DM3000 reskin
- [ ] Hunter suit reskin
- [ ] Heavy loader reskin
- [ ] Security alternate tiles are too abundant - security cameras could be joined and this should take one of those panels. The alternate tile should just have some scuff marks
- [ ] Update common tiles across sets
- [ ] Door in security blood spot is too ubiquitous, needs to be more subtle
- [ ] Habitation graphic overhaul
- [ ] Hologram emitter token reskin

## Decisions

- [ ] Decide on Command's lava and fire-particle replacement.
- [ ] What could replace Yog Duza? Maybe a giant angel.

## Code Quality / Stability

- [ ] Code cleanup, refactor and suggest improvements.

## Release Prep

- [ ] Update release notes and version (on plan completion)
