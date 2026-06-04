# Targeted Release: v1.0.3

Use this as the working backlog for the next sprint. Put rough ideas in Suggestions first, then move them into the relevant section once the direction is clear enough to implement and test.

## Suggestions

- [x] Cat follow-up idea for the next version.
- [ ] Refactor code and suggest improvements.
- [ ] What could replace Yog Duza? Maybe a giant angel.
- [ ] Need to replace chief's pickaxe (screwdriver) with a suitable tool icon.
- [ ] Need a new reward for chief since reforging redundant.

## Mechanics

- [ ] Cat rescue route after the final boss.
    - [x] Add a pet carrier item with placeholder icon, equip behavior, save data, and text.
    - [x] On defeating the final boss, drop the pet carrier.
    - [x] Track whether the station cat is following, dead, gone, or carried in the pet carrier.
    - [x] On clicking a found cat with an equipped empty pet carrier, put the cat in the carrier.
    - [x] If a carrier holding the cat is dropped, the cat pops out.
    - [x] Cat moves between levels only when following, then waits to be petted again before continuing.
    - [x] Allow returning through Operations decks to re-find the cat when the carrier is available and the cat is alive.
    - [x] Allow the pet carrier item to be found earlier in-game and equipped before the final boss.
- [ ] Endings:
    - [x] Standard escape pod
    - [x] Escape pod with cat
    - [x] Fix core (save NPCs, remain on station) - possible time-based challenge, work back through station levels - unstable shaking.
    - [ ] Download/Upload save files.

## Cosmetic / Narrative

- [x] Deep containment - change this to Bridge.
- [x] Should be made clear that Y is taking or giving something to hero for Tengu battle.
- [x] Fix ugly HP bar
- [x] Modal dialogs still need fixing (border size) - Start of game intro.
- [x] Rot lasher/heart sci-fi rework
- [x] Weak forcefield shouldn't rumble the ground.
- [x] If a bridge and chasm is in a room, then space suit isn't needed.
- [x] Convert makerbot to a workdesk. No sprite, no character.
- [x] Hoverpod should also offer protection in vacuum.

## Graphical Changes

- [ ] Manual work (Not for codex)
    - [ ] Pet carrier (needs new sprite and codex for mapping)
    - [ ] Tengu sprite
    - [x] Alien egg
    - [ ] Broken floor tiles
    - [x] NPC appearances
    - [ ] Terminals
    - [ ] Breaker
    - [ ] Jawar
    - [x] Music
        - [x] Engineering Boss
        - [x] Habitation
        - [x] Habitation Boss
        - [x] Bridge
        - [x] Bridge Boss
        - [x] Completion/credits
- [ ] Update Bridge levels tiles.
- [x] Replace leaf particles with sparks.
- [ ] Armor Kit needs icon sci-fi upgrade

## Bugs

- [x] No text found picking up medigel
- [x] Flock trigger - the log says alien: No text found
- [x] Feral shapeshifter's last words are DIE!, this shouldd be the "Thankyou" line.
- [x] Cat crash when touching the Maker Bench in the workshop.
- [x] Enhancement chip says You enhanced your %s
- [x] Hologram says thankyou %s
- [x] Shield is just called item - no texts found in description.
- [x] The mastery scripts book has no text found for all buttons, and description

## Code Quality / Stability

- [x] #22 Save failures can be silently accepted.
- [x] #23 Bound randomRespawnCell to avoid infinite loops.
- [x] #24 Fix Android lint failure for vibrator permission in pd-classes.
- [x] #25 Replace sniper DR unit test with production-code coverage.
- [x] #26 Make optional NDK build task portable without local.properties.
- [x] #27 Reduce static Android context and activity leaks.
- [x] #28 Clean up low-risk Android lint warnings.

## Refactorial Changes and Small Adjustments

- [x] If petted, cat always follows hero to next level. before ascending to boss level, a dialogue will warn do you want to bring the cat or not? If no is selected, the cat will wander off, and will not be found if the hero returns.
- [x] DM3000 reconfiguration
    - [x] Weapon handling
        - [x] Cannot equip ordinary melee weapons.
        - [x] Melee weapons can be melted down to missile bolts.
    - [x] Core body rules
        - [x] Is immune to gases.
        - [x] Doesn't require food.
        - [x] Can't use health packs.
        - [x] Siphons water to recover.
        - [x] Can eat batteries.
    - [x] Has automatic light in the darkness.
    - [x] Is immune to the alien egg effects.
    - [x] Jawars will disable DM3000 and steal any worn armor.
- [x] Chestburster should have a blood effect when appearing.
- [x] After alien infection hero should be caught in the sticky effect for 1 turn.
- [x] Workshop same size each level.
- [ ] Renames (classes)
    - [ ] crab
    - [ ] bat
    - [ ] brute
    - [ ] guard
    - [ ] gunsmith
    - [ ] thief
    - [ ] arp
    - [ ] Operations Level = Maintenance
    - [ ] Wraith
- [x] Stims, plasmids - should stay in effect whilst hero moving.
- [x] Lights breaker turns lights on and off for any level.
- [x] Torch needs a switch off option when it is on.
- [x] Yendor sheild has no text found
- [x] Turning on the breaker doesn't actually make the level back to normal brightness.
- [x] Alien Queen doesn't actually attack, also should not have 2 alien queens on the level (queen should only attack shapeshifter hero)
- [x] The healing booth should be a permanent healing feature (like the previous garden) - can return any time and slowly restore health.
- [x] The polymer plasmid, you should be able to throw this and it will instead nuetralise any gases in the air, but can still be injested for immunity.
- [x] If the cat dies, there should be a log indicator, and a sad meow.
- [x] If the shapeshifter equips a non-consumable item (e.g. blaster) it should act like a missile when clicked (option to throw)
- [x] Shapeshifter throws need to be better the more strength he has (like Hunters disk)
- [x] Falling trap should send you to the floor below.
- [x] Default action with fire grenade when quickslotted should be throw.
- [x] Shapeshifter should be able to quickslot weapons for throwing.
- [x] Restore specific sprites for Y, Quartermaster and Leonard.
- [x] Falling down a chasm should send you down a level (already visited), not up (unexplored)

## Release Prep

- [x] Re-enable any needed dev/test helpers deliberately behind a clear flag.
- [ ] Update release notes and version (on plan completion)
- [ ] Run `./gradlew :core:testDebugUnitTest`.
- [ ] Run `./gradlew :core:assembleDebug`.

## v1.0.4

- [ ] Cat should always go to eat meat (restores cat health)
- [ ] Cat is playful and should throw/bounce items the hero comes across (apart from pet carrier)
- [ ] Code cleanup and refactor
- [ ] Alter torch to be equipped in quickslots to activate
- [ ] Hunter suit to detect <sci-fi term> signatures (lights off, can visualise all enemies/npcs on a level in the dark)
- [ ] Workbench icon/sprite
- [ ] Chasms/black should have a subtle starry background, with potentially a planet and nearby star.
- [ ] Small wormhole generator - can active and select any visible tile on the map.
- [ ] Fire sprites on walls should be replaced with electrical fizzes.
- [ ] A battery doused with medigel can explode (locked doors, blockage)
