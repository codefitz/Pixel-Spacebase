# Targeted Release: v1.0.3

Use this as the working backlog for the next sprint. Put rough ideas in Suggestions first, then move them into the relevant section once the direction is clear enough to implement and test.

## Suggestions

- [x] Cat follow-up idea for the next version.

## Mechanics

- [ ] On defeating final boss, a pet carrier is dropped. The hero can board the escape pod, or he can return through the spacebase to try to re-find the cat on levels 1-5. If the cat was killed however - then the pet carrier will still drop but there will be no point to the hero going back.
    - [ ] On clicking on a found cat with pet carrier equipped the cat will be carried. If the pet carrier is dropped, the cat will pop out.
    - [ ] On defeating the final boss, the pet carrier item can now be found earlier in-game and equipped.
- [ ] Endings:
    - [ ] Standard escape pod
    - [ ] Escape pod with cat

## Cosmetic / Narrative

- [ ] Deep containment - change this to Bridge.
- [ ] Should be made clear that Y is taking or giving something to hero for Tengu battle.

## Graphical Changes

- [ ] Manual work
    - [ ] Tengu sprite
    - [ ] Alien egg
    - [ ] Broken floor tiles
    - [ ] NPC appearances
- [ ] Update Bridge levels tiles.

## Bugs

- [ ] Placeholder.

## Refactorial Changes and Small Adjustments

- [ ] If petted, cat always follows hero to next level. before ascending to boss level, a dialogue will warn do you want to bring the cat or not? If no is selected, the cat will wander off, and will not be found if the hero returns.
- [ ] DM3000 reconfiguration
    - [ ] Cannot hold mele weapons, only missiles and blasters. Mele weapons can be melted own to bolts that can be fired.
    - [ ] Is immune to gases, doesn't require food can't use health packs, siphons water to recover. Can eat batteries.
    - [ ] Has automatic light in the darkness.
    - [ ] Is immune to the alien egg effects.
    - [ ] Jawars will disable DM3000 and steal any worn armor.
- [ ] Chestburster should have a blood effect when appearing.
- [ ] After alien infection hero should be caught in the sticky effect for 1 turn.
- [ ] Workshop same size each level.

## Release Prep

- [ ] Re-enable any needed dev/test helpers deliberately behind a clear flag.
- [ ] Update release notes and version (on plan completion)
- [ ] Run `./gradlew :core:testDebugUnitTest`.
- [ ] Run `./gradlew :core:assembleDebug`.
