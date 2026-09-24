# Targeted Release: v1.0.4

Use this as the working backlog for the next sprint. Put rough ideas in Suggestions first, then move them into the relevant section once the direction is clear enough to implement and test.

## Suggestions

## Mechanics

- [x] Station cat waits on every level until touched, letting the player choose whether it follows and takes risks.
- [x] Following station cat bats one module, equippable module, or tech upgrade per regular room; it leaves ordinary loot and food alone.
- [x] A following station cat can scare a nearby signal siren into fleeing.
- [x] Alter torch to be equipped in quickslots to activate
- [ ] Small wormhole generator - can active and select any visible tile on the map.
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
    - [ ] Hunter suit to detect <sci-fi term> signatures (lights off, can visualise all enemies/npcs on a level in the dark)
    - [ ] Hunter can fly
    - [ ] Hoverpod will protect adventurer but has its own HP and will breakdown
    - [ ] Hoverpod can fly
    - [ ] Heavy loader high protection but can't fire weapons
    - [ ] Repair blaster can lock doors as well as open locked ones.

## Cosmetic / Narrative

- [x] Chasms/black should have a subtle starry background, with potentially a planet and nearby star.
- [ ] Fire sprites on walls should be replaced with electrical fizzes.
- [ ] Have a 'now playing' for music.
- [x] Challenges Icon update
- [ ] Custom skin tiles
- [ ] Armor Kit needs icon sci-fi upgrade
- [ ] What could replace Yog Duza? Maybe a giant angel.
- [x] Rename of icon files
- [ ] Fix swarm sprite - crap
- [ ] DM3000 reskin
- [ ] Hunter suit reskin
- [ ] Heavy loader reskin
- [x] Swap wall tiles in security
- [ ] Update common tiles across sets
- [ ] Door in security blood spot is too ubiquitous, needs to be more subtle
- [ ] Security alternate tiles are too abundant - security cameras could be joined and this should take one of those panels. The alternate tile should just have some scuff marks
- [x] Hero reacts when Y confiscates a weapon during the security maze and when it is recovered after the Masked Prisoner fight.
- [ ] Add room with no doors. These can be accessed by dropping into them, they may contain a high-value item or nothing. However there is no escape unless you have an item that can teleport you out.
- [x] Show the initial Xeno infection reaction only once per run; retain the warning and burst reactions for later infections.
- [x] Y's habitat and workshop sprites match his security boss appearance.

## Code Quality / Stability

- [ ] Code cleanup, refactor and suggest improvements.

## Release Prep

- [ ] Update release notes and version (on plan completion)
