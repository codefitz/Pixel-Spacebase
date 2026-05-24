# Targeted Release: v1.0.3

Use this as the working backlog for the next sprint. Put rough ideas in Suggestions first, then move them into the relevant section once the direction is clear enough to implement and test.

## Suggestions

- [x] Cat follow-up idea for the next version.
- [ ] Refactor code and suggest improvements.
- [ ] What could replace Yog Duza? Maybe a giant angel.

## Mechanics

- [ ] On defeating final boss, a pet carrier is dropped. The hero can board the escape pod, or he can return through the spacebase to try to re-find the cat on levels 1-5. If the cat was killed however - then the pet carrier will still drop but there will be no point to the hero going back.
    - [ ] On clicking on a found cat with pet carrier equipped the cat will be carried. If the pet carrier is dropped, the cat will pop out.
    - [ ] On defeating the final boss, the pet carrier item can now be found earlier in-game and equipped.
- [ ] Endings:
    - [ ] Standard escape pod
    - [ ] Escape pod with cat
    - [ ] Fix core (save NPCs, remain on station) - possible time-based challenge, work back through station levels - unstable shaking.
    - [ ] Download/Upload save files.

## Cosmetic / Narrative

- [ ] Deep containment - change this to Bridge.
- [ ] Should be made clear that Y is taking or giving something to hero for Tengu battle.
- [ ] Fix ugly HP bar
- [ ] Modal dialogs still need fixing (border size) - Start of game intro.
- [ ] Rot lasher/heart sci-fi rework
- [x] Weak forcefield shouldn't rumble the ground.
- [x] If a bridge and chasm is in a room, then space suit isn't needed.
- [x] Convert makerbot to a workdesk. No sprite, no character.
- [x] Hoverpod should also offer protection in vacuum.

## Graphical Changes

- [ ] Manual work
    - [ ] Tengu sprite
    - [ ] Alien egg
    - [ ] Broken floor tiles
    - [ ] NPC appearances
    - [ ] Terminals
    - [ ] Breaker
    - [ ] Jawar
    - [ ] Music
        - [ ] Engineering Boss
        - [ ] Habitation
        - [ ] Habitiation Boss
        - [ ] Bridge
        - [ ] Bridge Boss
- [ ] Update Bridge levels tiles.
- [x] Replace leaf particles with sparks.

## Bugs

- [x] No text found picking up medigel
- [x] Flock trigger - the log says alien: No text found
- [x] Feral shapeshifter's last words are DIE!, this shouldd be the "Thankyou" line.
- [x] Cat crash when touching the Maker Bench in the workshop.

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
- [ ] DM3000 reconfiguration
    - [ ] Cannot hold mele weapons, only missiles and blasters. Mele weapons can be melted own to bolts that can be fired.
    - [ ] Is immune to gases, doesn't require food can't use health packs, siphons water to recover. Can eat batteries.
    - [ ] Has automatic light in the darkness.
    - [ ] Is immune to the alien egg effects.
    - [ ] Jawars will disable DM3000 and steal any worn armor.
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
- [ ] The polymer plasmid, you should be able to throw this and it will instead nuetralise any gases in the air, but can still be injested for immunity.

## Release Prep

- [x] Re-enable any needed dev/test helpers deliberately behind a clear flag.
- [ ] Update release notes and version (on plan completion)
- [ ] Run `./gradlew :core:testDebugUnitTest`.
- [ ] Run `./gradlew :core:assembleDebug`.
