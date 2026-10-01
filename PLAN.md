# Targeted Release: v1.0.5

Working backlog for the next release. Completed v1.0.4 work is preserved in [the archived plan](docs/PLAN_v1.0.4_ARCHIVE.md) and the in-game changelog. Keep the game version at 1.0.4 (10004) until preparing the next release.

## Mechanics

- [ ] Let the Shapeshifter quickslot any item for throwing.
- [ ] Convert all ordnance items to grenades or mines rather than consumable-use actions and update sprites.
- [ ] Remove the Plasma Stabiliser and give its plasma-clearing function to the Freeze Blaster.
- [ ] Reduce Flamethrower spread to a single directional line.
- [ ] Update the custom room and quest for the unstable holo-projector.

## Rooms and Narrative

- [ ] Use spacebase tiles for the Holodeck boss entrance.
- [ ] Make jumping platforms look like exposed bridges rather than rooms enclosed by walls.
- [ ] Revise Y's rescue dialogue: remove the advance "you'll have to find me" explanation and the option to stay; clicking rescue Y should lead to a reluctant return.
- [ ] Expand Y rescue destinations to include the next boss fight, an alien planet, and a black/white open-space exploration area with a discoverable exit.
  - [ ] Decide destination and return-travel behaviour before implementation. Boss destinations deliberately change the current non-boss rescue rule.
  - [ ] On habitat level, if there's a fire, or the hero is on fire, have sprinklers trigger after 1 turn to put it out.
- [ ] Replace Spade and Drill with something else a bit more fitting.
- [ ] Habitit bathroom - water should pool in the center.

## Art

- [ ] Reskin the Jada Knight.
- [ ] Reskin DM3000.
- [ ] Hoverpod needs making larger - also window shield
- [ ] Give all plasmids injection-style icons.
- [ ] Reskin hero Armor Kit suits.
- [ ] Enlarge the Hunter armor appearance.
- [ ] Shrink the Roman Soldier sprite.

## Carried-over Verification

These fixes are implemented in v1.0.4, but on-device confirmation remained outstanding in the previous plan. They are verification tasks, not unimplemented features.

- [x] Check popup/button text fitting and checkbox alignment in game.
- [x] Confirm falls onto boss decks land inside the arena on a usable tile.
- [ ] Confirm enemies killed by a Shapeshifter's thrown blaster finish their death animations.
- [x] Confirm revealed hull edges have no fog halos or stray exterior walls, while interiors retain fog of war.
- [x] Confirm quantum chest contents remain single and persist across transitions, revisits, bosses and save/reload.
- [x] Confirm blue item scanner pings and red trap scanner pings appear through fog under the intended Hunter suit/module conditions.
- [ ] Investigate the two Eclipse Saber JVM tests that failed during initialization; rerun the full suite after correction.

## Decisions

- [ ] Decide what should replace Yog Duza; a giant angel is one suggestion.

## Longer-term Backlog

- [ ] Download/upload save files (future paid feature).
- [ ] Custom asset skin uploads (future paid feature).
- [ ] Audit code cleanup and refactoring opportunities, then scope concrete improvements.

## Release Prep

- [ ] Complete selected 1.0.5 tasks and relevant playthrough checks.
- [ ] Preserve save compatibility when changing item classes, quests and travel state.
- [ ] Update Android version metadata, welcome summary and in-game changelog to 1.0.5.
- [ ] Run regression tests and build the APK; document any outstanding failures.
- [ ] Create the release and attach the APK, clearly identifying its signing/build type.
