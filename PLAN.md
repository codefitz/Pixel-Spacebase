# Targeted Release: v1.0.2

## Cosmetic Changes

- [x] Update build number to v1.0.2
- [x] Update about (give credit to Codex), recent changes
- [x] Update the Dark Lord of Yendor theme to have stronger "Darth Sidious" vibes.
  - [x] Review actor and item message strings for tone.
  - [x] Review encounter presentation and boss text.
- [x] Replace the chain/restraint theme with "the force".
  - [x] Rename the Bith pull effect and descriptions so it reads as being pulled by the force.
  - [x] Remove or replace text that refers to chains.
- [x] Add "Do not pet the cat" warning sign text.
- [x] Alien egg - screen still needs to fade to black and return before "Ugh what happened".
- [x] Stimulantts need to be updated to a stimulant type of sprite. Also should be renamed "Stims".
- [x] Dialogue boxes could do  with a smaller border and make the background semi-transparent
- [x] The pickaxe has generic text and NO TEXT FOUND. This should be replaced with some other sci-fi item.
- [x] The surveyor module has NO TEXT FOUND for it's action button.
- [x] Backpack extensions should feature in every workshop, but only be available once the previous one has been built.
- [ ] Quaretermaster lazer quest tiles need to be switched to something like a holodeck backdrop. When the hero enters it should switch to a holodeck program like ancient rome or something.
- [x] Tech Library and Icon need to be updated to 'Mods'
- [x] Alien Egg item (pick up and plant) should be Black Goo.
- [ ] Bionetics naming - rather than call the items 'Mods' they should be called 'Plasmids'
- [ ] Mods naming - rather than call the items 'Tech' call them 'Upgrade'
- [ ] Swap the Mods and Bionetics backpack extensions icons.
- [ ] Vaccuum warning should be a popup, rather than log.

## Mechanic Changes

- [x] Torch becomes a permanent item with battery pickups.
  - [x] Add permanent torch item behavior.
  - [x] Add battery pickup generation and recharge behavior.
  - [x] Balance torch charge use against dark-level encounters.
- [x] Spacesuit is required for spacewalk areas.
  - [x] Gate captain spacewalk access behind spacesuit ownership/equipment.
  - [x] Gate commander spacewalk access behind spacesuit ownership/equipment.
  - [x] Add failure messaging when the player tries to enter without a spacesuit.
- [ ] Rework shapeshifter progression.
  - [ ] Prevent shapeshifter from using blaster weapons.
  - [ ] Scale shapeshifter strength as level depth increases.
  - [ ] Let shapeshifter transform into items.
  - [ ] Make item form apply camouflage-like concealment.
  - [ ] Give item form a boosted surprise attack.
- [ ] Rework shapeshifter healing.
  - [ ] Debuff health pack effectiveness for shapeshifter.
  - [ ] Add small shapeshifter recovery while standing in water.
- [ ] Replace the shapeshifter mirror boss path.
  - [ ] Prevent shapeshifter from battling himself at the first boss.
  - [ ] Add Y encounter when shapeshifter reaches the first boss.
  - [ ] Create arena challenge against 50 enemies.
- [ ] Add room and breaker lighting mechanics.
  - [ ] Light rooms on entry.
  - [ ] Remove gameplay dependency on floor lights.
  - [ ] Add random dark levels.
  - [ ] Place a breaker on dark levels that restores lights.
  - [ ] Place a breaker on every level so lights can be toggled off later.
- [x] Add generic storage/reveal behavior so the container opens before the tech/biogenetic item identity is known.
- [ ] Add protectable NPC rescue flow.
  - [ ] Add NPCs that need protection.
  - [ ] Add escape pod ending where the player leaves.
  - [ ] Add return-to-stabilize ending where NPCs can be rescued.
- [ ] Add cat follower NPC.
  - [ ] Implement cat following behavior.
  - [ ] Decide whether petting is blocked, punished, or only warned against.
- [ ] Replace garden with a one-square healing tank booth.
  - [ ] Remove or disable garden room generation.
  - [ ] Add one-square healing tank booth placement.
  - [ ] Define healing tank interaction behavior.
- [x] Security levels should start with the piercing alarm sound from the trap.
- [x] Positive-effect mines should become automatically applied stimulants.
- [x] Fix chest state after lower-level warp.
  - [x] Reproduce the case where being warped back down empties chests irrecoverably.
  - [x] Preserve unopened chest contents across lower-level warps.
- [x] Rework makerbot workshop progression.
  - [x] Limit early makerbot stock to essential items.
  - [x] Keep a small chance for rare or high-strength stock in lower-tier workshops.
  - [x] Add makerbot upgrades that unlock better items over time.
  - [x] Increase basic item level by zone regardless of makerbot upgrade state.
- [x] Persist player-owned items across workshops in the same area.
- [ ] Add alien egg step event.
  - [x] Fade the screen briefly to black when stepping on an alien egg.
  - [x] Show a "what happened" dialog when the screen returns.
- [x] Falling needs to be fixed so it falls to the level below - not up. The locked room needs to appear on the (next) level above.
- [x] Y should appear 3 times in the maze before Tengu main battle (hero to seek him out). If hero finds Y all 3 times, he will look in heros backpack and compare against Tengus stats. If the hero seems to be under equipped - Y even the odds and will provide an offensive ranged weapon with at least 3 charges. If the hero seems to be overequipped - Y will even the odds and remove one random weapon and place it to be found in one of the cells after the battle.
  - [x] Place three Y maze encounters with mischievous ambiguous dialogue.
  - [x] Track whether the hero found all three encounters.
  - [x] Compare the backpack against Tengu's stats before the arena.
  - [x] Give or remove a ranged weapon based on that comparison.
- [x] facehugger should die on infection. Also remove the sticky web effects.

## Refactorial Changes and Small Adjustments

- [ ] Rename legacy fantasy concepts to spacebase equivalents.
  - [ ] Rename code/messages/assets that still expose `Dungeon`.
  - [ ] Rename code/messages/assets that still expose `King`.
  - [ ] Rename code/messages/assets that still expose `Skeleton`.
  - [ ] Rename code/messages/assets that still expose `DarkLordGnoll`.
  - [ ] Rename code/messages/assets that still expose `PrisonLevel`.
  - [ ] Rename code/messages/assets that still expose `CavesLevel`.
- [ ] Reorganize tech and biogenetics by use.
  - [ ] Classify defensive tech/biogenetics as effects applied to the player.
  - [ ] Classify offensive tech/biogenetics as weapon/action items.
  - [ ] Convert offensive biogenetics such as fire into grenades.
- [x] Rename Xeno-booster to align with the x-port concept.
- [x] Add an option to open and scroll the full log.
- [ ] Floor lighting overhall
  - [ ] Lighting that contains a trap, should be invisible - so it's a real trap
  - [ ] Traps shouldn't ever look like a light - they should look like a blown panel after being set off.
  - [ ] Adjust terrain features so that only one type appears on each zone/area
  - [ ] Adjust terrain features to look less 'dungeon' and more like different types of lighting.
  - [ ] Set mines should look a bit more obvious
  - [ ] Shuriken need to be updated to something suitably sci-fi 

## Graphical Changes

- [x] Remove the 3D effect/overlay from floor lights.
- [x] Replace the chain graphic with a force-themed visual.
- [x] Add a generic storage icon for unrevealed tech/biogenetics.
- [x] Add or update grenade icons for offensive biogenetics.
- [ ] Add healing tank booth tile/art.
- [ ] Add cat sprite or reuse/update an existing follower sprite.
- [ ] Add visual treatment for dark levels and breaker-restored lighting.
- [x] Change Xeno-booster to an x-port icon.
- [ ] Tengu battle arena needs to be more like an alien planet (think Star Trek TOS).
- [ ] Siphon drone sprite needs updating.

## Bugs

- [x] Target sometimes doesn't dissapear after certain actions.
- [x] Guardian Floor Lighting causes a game crash when walking over it.
- [x] One of the modules is just called 'Item 1'
- [ ] Fix the backpack tab icon (currenty is a battery)
- [ ] Internal bridge (room filled with water) warns about the vacuum, this is not needed.
- [ ] Stepping on a falling trap still sends me up, rather than down
