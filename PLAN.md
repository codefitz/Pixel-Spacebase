# Targeted Release: v1.0.5

Working backlog for the next release. Completed v1.0.4 work is preserved in [the archived plan](docs/PLAN_v1.0.4_ARCHIVE.md) and the in-game changelog. Keep the game version at 1.0.4 (10004) until preparing the next release.

## Mechanics

- [x] Make Y's dark maze usable without a torch: show the hero's tile and eight immediate neighbours as generic grey floor/wall shapes, with no deck artwork or retained exploration. Use map taps for movement, successful-step and wall feedback, and a yellow movement pulse beside the feedback above the toolbar. Keep farther corridors black; allow single steps into darkness and normal routes through torch-visible tiles, including diagonal movement. Torch activation does not require equipment. In-game confirmation pending.
- [x] Add a Hoverpod TORCH ON/OFF action and worn-pod quickslot toggle. Its integrated light uses normal torch range without batteries, works in Y's dark maze, persists across saves/travel and switches off on removal/destruction. Old saves default to off. In-game confirmation pending.
- [x] Give DM-3000 an EXTRACT WATER food action that repairs up to 5 HP per serving without restoring hunger, charging blasters or applying eating effects. Batteries retain their power role. Hide and block Medigel HEAL for DM-3000; quickslots extinguish instead, and droplets fill its container even while damaged. Update class/item messaging. In-game confirmation pending.
- [x] Match Hunter detection colours to the modules: green biological signatures, blue item silhouettes and red trap silhouettes using each trap's own artwork. Keep darkness/module requirements and fog/activation state intact. In-game confirmation pending.
- [x] Route chasm falls onto boss decks into their sealed doorless room, enabling the usual twenty-turn Y rescue. Recreate missing rooms on saved phase-swapped boss maps; create a chamber on demand if a fall reaches the final boss deck. Keep deliberate Y boss detours at the arena approach. In-game confirmation pending.
- [x] Standardise non-Maintenance entrances and exits as circular teleport pads: orange for the previous deck, blue for the next, with transparent centres showing the current floor artwork and dimmed locked departures. Add a brief hero beam effect, coloured signal transition during loading and arrival reconstruction. Keep Maintenance ladders and existing travel/save rules. In-game confirmation pending.
- [x] Let the Shapeshifter quickslot any throwable item and use the quickslot to throw it.
- [x] Convert all ordnance items to thrown grenades or planted mines and update sprites. Keep grenade self-use; Black Goo stays in the main inventory.
- [x] Remove the Plasma Stabiliser and give its plasma-clearing function to the Freeze Blaster.
- [x] Reduce Flamethrower spread to a single directional line.
- [x] Use deactivated Holodeck grid tiles for the unstable holo-projector quest room, independently of the boss Holodeck's power state. Applies to saved rooms too; update the room's inspection text. In-game confirmation pending.
- [x] Shapeshifter quickslots retain special-item activation and equipment actions instead of forcing throws. Dynamic cache actions refresh first; ordinary weapons and blasters remain throwable. In-game confirmation pending.
- [x] Opening an unknown cache applies its effect to the hero immediately, including both grenade families. Consume one item and the normal use time; remaining revealed grenades sort into ordnance. In-game confirmation pending.
- [x] Add reusable emergency decontamination stations near the entrance of Command decks, including restored saves. A one-turn rinse removes acid and fire and leaves water on ordinary floors. Medigel now offers separate HEAL and EXTINGUISH actions; extinguishing spends one charge without healing. In-game confirmation pending.

## Rooms and Narrative

- [x] Use spacebase tiles for the Holodeck boss entrance.
- [x] Extend normal Habitat artwork around the Holodeck arrival chamber's complete wall surround and arena doorway in both power states. Keep the existing custom boss layout and apply the visual treatment to saved floors. In-game confirmation pending.
- [x] Make jumping platforms look like exposed bridges rather than rooms enclosed by walls.
- [x] Shorten newly generated exterior platforms to leave two tiles of open space between the tip and the map edge, avoiding unrevealable fog at the end. Existing saved layouts are retained. In-game confirmation pending.
- [x] Revise Y's rescue dialogue: remove the advance "you'll have to find me" explanation and the option to stay; clicking rescue Y should lead to a reluctant return.
- [x] Expand Y rescues using [the staged rescue plan](docs/Y_RESCUE_PLAN.md); all stages implemented, in-game acceptance review pending.
  - [x] Stage 1: saved journey state, first-rescue guarantee, and return recovery implemented; in-game reload checks pending.
  - [x] Stage 2: alien planet, friendly NPCs, and a parts trader implemented; in-game visit and purchase checks pending.
  - [x] Stage 3: next-boss detour, persistent victory, and reset after defeat implemented; in-game win, loss, and reload checks pending.
  - [x] Stage 4: dark maze, visible exit, torch exploration, and Hunter mapping implemented; in-game visibility, equipment, and reload checks pending.
  - [x] Stage 5: 1% Pixel Dungeon first-floor visit with original artwork implemented; in-game appearance, combat, and reload checks pending.
- [x] On habitat level, if there's a fire, or the hero is on fire, have sprinklers trigger after 1 turn to put it out.
- [x] Replace Spade with the accurate Phase Cutter and Drill with the defensive Repulsor Baton, with new sprites and save compatibility.
- [x] In Habitat changing rooms, water always pools in the center.
- [x] Make the Hunter blue item indicator use the detected heap item's sprite silhouette, including unidentified cache and Pixel Dungeon visit artwork. In-game confirmation pending.

## Issues

- [x] Restore normal tap-to-move routes through currently visible tiles in Y's dark maze and allow diagonal steps. Remove the N/W/E/S buttons; retain step/wall feedback and single steps toward taps into darkness. Recalculate routes as sight changes so they cannot cross hidden corridors or continue after torchlight expires. Immediate tile taps confirmed by the player; longer routes and diagonal movement need in-game confirmation.
- [x] Warn before a Repair Blaster beam hits a door, including doors along the beam's path. Explain the risk of trapping the hero and the need for fire, explosives or another escape option; offer Cancel shot or Fire anyway without ending the run. Cancellation spends no charge or turn. In-game confirmation pending.
- [x] Count 20 hero actions in the stranded chamber before Y rescues; corrected world-tick counting and immediate landing tick. In-game confirmation pending.
- [x] Keep skipped boss decks ready after Y rescues to later floors. Deck saves now identify their run, new games clear their own old decks, and travel from below enters unfinished arenas through their normal approach. Legacy completed decks at floors 5–20 require a victory badge from the current run. In-game confirmation pending.
- [x] Items aren't appearing normally on level 6 and above. Reused explored decks from earlier games are a suspected cause. New runs now reject other-run and unmarked deck files; four save-ownership checks passed. Confirm loot in game; older unmarked ordinary decks are retained to preserve progress.
- [x] Fix missing escape pod ending text: dialogue, cat variant, launch and stay buttons now use the renamed EscapePodScene resource keys. In-game confirmation pending.
- [x] Quantum chest duplication resolved; confirmed by the player.
- [x] Correct hull fog coverage: keep interior floors covered at shared wall corners, reveal only hull walls, and exclude enclosed wall gaps from exterior space. Reviewed the supplied screenshots; in-game confirmation pending.
- [x] Show only current HP on the life bar, removing the shield calculation and maximum-HP fraction. In-game confirmation pending.
- [x] Keep level-5 queen eggs and xenomorph spawns on walkable ground. Removed chasm/avoid-cell eligibility and excluded the sealed rescue chamber. In-game confirmation pending.
- [x] Remove black exterior rectangles on Engineering decks (the supplied screenshot shows floor 11). Removed the tiles2 exception that drew opaque chasm tiles outside the station. In-game confirmation pending.
- [x] Sort revealed grenades into ordnance immediately when their cache type becomes known. Preserve quantities and quickslots, merge matching stacks, and leave items in place when the kit is full. Three focused sorting checks passed; in-game confirmation pending.

## Art

- [x] Create distinct bio, item and trap detector icons with green life-sign, blue cargo and red hazard displays. Assign the item and trap icons to their Hunter modules; preserve existing saves and Pixel Dungeon visit artwork. Bio icon assignment awaits clarification because there is no separate bio-detector item. In-game confirmation pending.
- [x] Match the Loader arm and equipment icons to the orange hydraulic chassis, refresh its cockpit portraits, and reduce the on-level sprite by about 29% to a similar overall size as the Hoverpod while preserving proportions and claw clearance. In-game confirmation pending.
- [x] Reskin the Jeda Knight with an uncovered head, cream/tan robes and blue energy blade; give the shared Master variant a green blade. Repacked both into 64×64 animation frames. In-game confirmation pending.
- [ ] Reskin DM3000.
- [x] Enlarge the Hoverpod with dedicated 80×80 animation frames, a broad blue glass canopy and each hero visible behind the screen. Refreshed portraits and inventory icon. In-game confirmation pending.
- [x] Give all 12 plasmid colours matching injector icons with a shared silhouette and coloured liquid reservoir. Updated splash colour sampling for the new artwork. In-game confirmation pending.
- [x] Make Medigel, Adrenal and Forcefield Stims use the same injector silhouette as plasmids, with green, blue and purple reservoirs respectively. Includes items restored from existing saves. In-game confirmation pending.
- [ ] Reskin hero class armors.
- [x] Enlarge the Hunter suit proportionally to a 64px-tall frame, trimming transparent padding when displayed and preserving animation alignment. In-game confirmation pending.
- [x] Shrink Roman Soldiers from 96×96 to a displayed 64×64 frame, preserving the artwork and animations. In-game confirmation pending.

## Carried-over Verification

These fixes are implemented in v1.0.4, but on-device confirmation remained outstanding in the previous plan. They are verification tasks, not unimplemented features.

- [x] Check popup/button text fitting and checkbox alignment in game.
- [x] Previous arena landing was confirmed; superseded by the v1.0.5 doorless-room routing above. In-game confirmation of the new behavior pending.
- [ ] Confirm enemies killed by a Shapeshifter's thrown blaster finish their death animations.
- [x] Confirm revealed hull edges have no fog halos or stray exterior walls, while interiors retain fog of war.
- [x] Confirm quantum chest contents remain single and persist across transitions, revisits, bosses and save/reload.
- [x] Confirm blue item scanner pings and red trap scanner pings appear through fog under the intended Hunter suit/module conditions.
- [ ] Investigate the two Eclipse Saber JVM tests that failed during initialization; rerun the full suite after correction.

## V1.0.6

- [ ] Decide what should replace Yog Duza; a giant angel is one suggestion.
- [ ] Map sprite directions (walking, standing, fighting)
- [ ] Hoverpad should be able to activate traps/tiles with extra click whilst on top.
- [ ] Fill the inside of entrance and exit pads
- [ ] Enemies should go after the cat when it's active (not sitting waiting to be petted), however bosses will kill the cat if it's in the same room regardless of status.
- [ ] Inactive holodeck needs distinguishing walls.
- [ ] Have a room in some levels that is only reachable by jetpack
- [ ] A blind enemy but takes 50% health when it finds you
- [ ] DM3000 should be able to use medigel to wipe acid off
- [ ] On the prison boss, if you bring the cat then she is lost once the boss is defeated. Ideally she should just wander back to the previous level.
- [ ] If you zap Y, he sends you to the blackout level and remarks "This will teach you a lesson". He should only do this once.
- [ ] Randomly, 1% chance, Y will appear when you're trapped in the doorless room and simply tell you sorry but this is it, I'm not rescuing you. This should only activate after he's already done 1 rescue. This signifies the end unless the hero has some way of teleporting.
- [ ] DM3000 and Shapeshifter should be immune to eggs and facehugger
- [ ] Sparks in engineering should only be visible on the level with Leonard
- [ ] Bug - an clicking on melt and then click on my the blaster actaully melts it down!

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
