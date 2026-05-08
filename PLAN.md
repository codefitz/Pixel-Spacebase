# Pixel Spacebase Plan
# Targeted Release: v1.0.1

This plan is organized by the kind of work needed next. Use it as the active backlog, then move completed work to the done section with a short note about what changed.

## Workflow

1. Add new ideas to **Suggestions** first, especially when the direction is not settled.
2. Promote accepted ideas into **Sprites**, **Tiles**, **Mechanics**, or **Cosmetic / Narrative**.
3. Implement small batches that can be tested quickly.
4. Update this plan and any relevant docs after each batch.
5. Build the core debug APK after code or asset changes.

## Refactor / Naming Audit

- [x] Add naming audit separating player-facing cleanup from inherited internal compatibility names.
- [x] Add compatibility wrappers or comments for intentionally retained legacy class names.
- [x] Decide whether to introduce a `RepairBlaster` wrapper around internal `EMP`: no wrapper for now; keep `EMP` as internal compatibility name.
- [x] Decide whether `ExperimentalTech` and `Script` package names should be migrated or left as internal compatibility names: leave as compatibility names for now.
- [x] Audit old `SewerLevel`/Operations fallback resource keys and mark them as legacy fallback strings.

## Suggestions

- [x] Decide on a replacement concept for the King of Dwarves / floor 20 holodeck boss: rogue holodeck monarch with lethal hard-light retainers.
- [x] Decide on the final boss direction: Yog-Dzewa is a sealed bio-containment parasite fused around the evacuation spine.
- [x] Decide on the floor 22-24 area name and mood: Deep Containment with space-horror bio-containment imagery.
- [x] Decide floor 26 finale choice: launch the escape pod immediately, or stay on the station with the override for optional future objectives.
- [x] Decide whether the workshop should become a fixed room template before adding more workbench mechanics: yes, use a fixed interior template inside generated workshop rooms before adding upgrade/storage benches.

## Next Recommended Batches

- [x] Workshop foundation: add the Chief Engineer-style upgrade bench to the fixed workshop layout.
- [x] Y / Holodeck quest: have Y send the player after rogue holograms, then add hologram emitter drops as the quest/reward loop.
- [x] Lighting and powered-floor pass: spent floor-lighting now leaves a visible powered-plate overlay; keep room lighting and breaker resets as a separate future mechanic rather than replacing most floor lights now.
- [ ] Area visual pass: batch sprites and terrain tiles by area so security, lower engineering, command-sector, and Deep Containment art changes stay coherent.
- [ ] Endgame rescue pass: connect the floor 26 override twist to protectable NPCs, sealed rescue cradles, and the optional stabilize-the-ship route.

## Sprites

- [ ] Replicator Swarm sprite to be updated.
- [ ] Loader armour sprite could do with a makeover.
- [x] Tengu: keep as an assassin but update sprite to futuristic.
- [ ] Holodeck Monarch / floor 20 boss sprite needs a sci-fi or holodeck-relative replacement.
- [ ] Ruptured crew suit / old Skeleton sprite still needs updating to a suitably sci-fi enemy.
- [ ] Thief sprite should look like a Jawa-style scavenger.
- [x] Guard sprite should become a Bith-style apprentice; remove or replace the chain visual with force-pull styling.
- [ ] Spinner sprite should become a little facehugger.
- [ ] Jeda Knight / old Monk sprite should become a Jeda Knight.
- [ ] Senior sprite should become a Jeda Master.

## Tiles

- [x] Security Block tiles should match a futuristic spaceship detention area.
- [ ] Lower Engineering tiles need a lower engineering aesthetic.
- [ ] Habitation / Command Sector tiles need a Star Trek TNG-style pass.
- [x] Ladder tiles swapped so the previous-deck entrance and next-deck exit use the correct visual frames.
- [x] VENT should visually convert to a spent powered plate after triggering, rather than disappearing into plain floor.
- [x] INACTIVE_VENT now stays off permanently after traps/floor-lighting are spent.
- [x] SIGN renamed to terminal.
- [ ] WATER needs a sci-fi aesthetic per area while keeping current gameplay properties.

## Mechanics

- [x] Security Block levels should use mostly locked doors, with one super key on each level.
- [x] Y should send the player to take down rogue holograms from the holodeck, with hologram emitters as drops.
- [x] After the floor 10 boss is defeated, Y should pop in, trigger the level change, tease the player, then disappear after the hero moves with: "We shall meet again if you survive".
- [x] Player-dropped items in workshop rooms now carry forward with workshop stock inside the same area.
- [x] Add an option to open and scroll the full log.
- [x] Stepping on an alien egg now briefly fades to black and shows the "what happened" infection dialog.
- [x] Xeno-booster now uses the x-port/Bionetics icon after crafting.
- [x] Floor lighting now lights up when non-flying characters stand on it, while trap/floor-lighting activation still runs through normal press handling.
- [x] Missiles and thrown items can cross unpowered floor lighting without treating it as projectile-blocking terrain.
- [x] Turn the Maker-Bot into a workbench and anchor it to a wall.
- [x] Make the workshop the same size and layout for each area.
- [x] Add reusable storage chests to the fixed workshop layout.
- [x] Add another workshop bench inspired by the Chief Engineer that lets the player combine items to upgrade.
- [x] Spinner / facehugger should latch like the alien egg and spawn a stronger xeno if it succeeds.
- [x] Search options include a 'search' button, so if you're near something of interest but are checking walls, you can still just check the wall.
- [x] McGyver should just break the item if it fails, not dissapear everything in the workshop.
- [x] Replace EMP blaster with a repair blaster, the repair blaster can transform traps to light, fix broken doors/blocked doors, has high hit points on robots and machines but does nothing to beings.
- [x] Repair blaster has a chance to unlock chests and doors, but also has a chance to break the lock permenantly (warn player with % chance - higher skill greater chance).
- [x] Loader armor should be fireproof
- [x] Repair blaster attempts door locks with a warned percent chance to open or jam them.
- [x] Repair blaster works on siphon drones.
- [x] Repair blaster works on replicator swarms.
- [x] Golden drone uses its own short-range sensor instead of hero field-of-view when pursuing targets.
- [x] Alien facehugger latch no longer applies the old poison/paralysis-style effect.

## Cosmetic / Narrative

- [x] All defined signs/terminals updated with sci-fi story/hint elements.
- [x] Floor 5 boss framed as a shapeshifter crew colleague, driven unstable by identity loss from repeated emergency shifting.
- [x] Floors 6-9 reframed as a futuristic spaceship prisoner detention / security area.
- [x] Floor 10 boss death line now implies death rather than escape.
- [x] Floors 16-19 clarified as habitation and command-sector decks leading toward the holodeck.
- [x] Floor 20 boss room is Holodeck Control, with the fantasy figure kept as an intentional hard-light scenario.
- [x] Floors 22-24 shifted from Demon Halls wording to Deep Containment space-horror text.
- [x] Floor 25 boss now guards the route to the escape pods.
- [x] Floor 25 boss now evokes a sealed bio-containment parasite rather than the alien queen.
- [x] Floor 26 finale is the route to the escape pod.
- [x] Floor 26 may need a twist that gives the player a reason to go back.
- [x] Arp / Arp Trader player-facing name updated to Y.
- [x] Y now addresses the player directly and references inventory, badges, rankings, and quest tracking.
- [x] Old Gunsmith updated to a more futuristic quest character.
- [x] Ceremonial candle and ceremony text reframed as reactivating the core.
- [x] The target for blasters is in the shape of a gun, it should just be a crosshair.
- [x] Text slightly overlaps dialogue boxes; quick layout padding added while a future graphic pass can make the frame look like futuristic floating touchscreen glass.
- [x] Maker Bench is wall-anchored and uses static bench behavior/sprite animation instead of NPC-facing idle motion.
- [x] Add track lockdown.mp4 to security levels.
- [x] Where tengu says let's make this interesting - change this to the disembodied voice of Y.
- [x] "We shall meet again..." should be Y, not alien.
- [x] After DM300 (why did they may me so angry), hero can say "We can rebuild him, we have the technology..."
- [x] Have DM300 spout cliche LLM lines e.g. "You are absolutely right..." etc...

## Completed Conversion Notes

- [x] Shapeshifter boss says thank you when defeated/restored.
- [x] Alien egg inventory/device icon now uses the alien pod/egg icon so it resembles the planted egg outcome.
- [x] X-Port renamed to Bionetics in player-facing text.
- [x] Knuckleduster updated to something suitably sci-fi.
- [x] Tome of mastery updated to sci-fi wording and effects as a mastery datacore/protocol sync.
- [x] Rather than "descending", the hero is ascending.
- [x] Maintenance crawlers now drop parts instead of meat.
- [x] Rename visible vent text to floor lighting.
- [x] After DM-300 is beaten, the hero says a "we can rebuild him" style line.
- [x] Skeleton description updated to ruptured crew suit.
- [x] Thief renamed/described as Jawar.
- [x] Bandit renamed/described as Jawar scavenger.
- [x] Guard text frames them as Bith acolytes with a force-pull rig; sprite/graphic pass remains.
- [x] Bat updated to siphon drone text.
- [x] Brute updated to Yendor shock trooper text.
- [x] Spinner text names/describes facehugger; mechanic/sprite remain.
- [x] Elemental updated to sci-fi text.
- [x] Newborn Elemental updated to sci-fi text.
- [x] Monk text renamed to Jeda Knight; sprite remains.
- [x] Senior text renamed to Jeda Master; sprite remains.
- [x] Warlock updated to sci-fi text.
- [x] Golem updated to a large war machine.
- [x] Succubus updated to sci-fi text.
- [x] Eye updated to sci-fi horror text.
- [x] Scorpio updated to sci-fi horror text.
- [x] Acidic updated to acid-spitter cannon text.
- [x] WaterThing updated to sci-fi text.
- [x] Rot Heart updated to sci-fi text.
- [x] Rot Lasher updated to sci-fi text.
- [x] Amulet of Yendor player-facing item, badge, and victory scene text updated to escape pod override.
- [x] Embers updated to sci-fi text.
- [x] Pickaxe updated to sci-fi text.
- [x] Corpse Dust updated to sci-fi text.
- [x] Rotberry / Rotberry Mine Charge updated to sci-fi text.
- [x] Prison, caves, city, and halls chapter text updated to sci-fi framing.
- [x] DM-300 lore updated to station defense / war-bot framing.
- [x] DM-300/DM-3000 naming split cleaned up in player-facing text and docs.

## Bug Fixes

- [x] Hologram quest highlight markup showed `_evolved xenomorph_` as plain text because punctuation sat outside the closing underscore.
- [x] The drone does not wander; it moves like the clones.
- [x] Wave blaster crash at maximum length fixed.
- [x] WaveBlaster immediate crash note captured in `docs/BUG_NOTES.md`; `BlastWave` must stay public for reflective effect recycling.
- [x] Entrance/exit ladder visual frames swapped and documented for the station's upward progression.
- [x] Tengu's defeat line clarified: "Free at last" is death as release from confinement.
- [x] Floor 6-9 signs and prison-level tile text reframed around the Security Block detention sector.
- [x] Floor 16-19 signs and city-level tile text reframed around habitation, command offices, civic decks, and the holodeck approach.
- [x] Floor 20 terminal reframed the boss room as Holodeck Control with a failed monarch hard-light scenario.
- [x] Remaining lower-engineering and final-workshop terminal tips converted from inherited dungeon hints into station notices.
- [x] Floor 26 evacuation cradle, escape pod override action, and victory scene now frame the finale as launching the last working escape pod.
- [x] Siphon drones and replicator swarms tagged as machine enemies for repair blaster damage.
- [x] Facehugger attacks now use the xeno infection latch without applying the old poison effect.
- [x] Repair blaster includes repairable blocked terrain just beyond the beam collision point, so locked doors receive the override roll.
- [x] Golden drone targeting no longer depends on whether the hero can see the drone's target.
- [x] Naming audit added; stale player-facing blacksmith/bookshelf/stairs/caves/oldWarBot wording cleaned up where safe.
- [x] Legacy compatibility comments added for `EMP`, `ExperimentalTech`, and `Script`; no wrapper/package migration for now.
- [x] DM-3000 no longer accrues hunger or auto-eats, while manual food use still recharges blasters.
- [x] DM-3000 keeps at least the utility-light view radius, including after temporary light buffs end.
- [x] Security Block floors now convert most ordinary doors into locked detention doors and place a reusable max-security override keycard near the entrance.
- [x] Maker Bench no longer turns to face the hero and its sprite animation is static.
- [x] Floor 10 boss victory now summons a brief Y interlude that vanishes after the hero moves and delivers the survival tease.
- [x] Tengu's phase-change tease is now attributed to Y, the Y interlude has a Y name key, and DM-300 has expanded corrupted assistant-style combat barks.
- [x] Message, quest, and story windows now use slightly safer text padding to reduce border overlap.
- [x] Badge message aliases added for current gene-mod badge enum names, preventing "No Text Found" when gene-mod badges unlock.
- [x] DM-3000 now has class-level resistance to fire/toxic/poison sources and immunity to poison, fear, paralysis, and vertigo without changing armor strength.
- [x] Floor 26 escape pod override text now explains that staying behind could reopen sealed rescue cradles and evacuation routes for survivors.
- [x] Floor 20 boss player-facing text reframed as a rogue holodeck monarch with hologram retainers.
- [x] Deep Containment terrain descriptions converted from lava/skulls/books to containment fluid, growths, specimen pillars, and experiment logs.
- [x] Yog-Dzewa player-facing text reframed as a bio-containment parasite blocking the evacuation spine.
- [x] Feral Shapeshifter player-facing text reframed as a restored crew colleague suffering shapeshifter identity collapse.
- [x] Full message log can be opened from the game menu and scrolled.
- [x] Journal showing 'No Text Found'
- [x] Just went to step on vent and the game crashed.
- [x] Journal feature enum names now have matching message keys.
- [x] Vent stepping crash hardened by making log history merging tolerant of old HUD entries and clearing disarmed vent objects from the active vent map.
- [x] Nearby scanner results now include Search as an option instead of forcing examine over wall checks.
- [x] Stepping on the egg doesn't black out before "ugh, what happened"
- [x] One of the badges comes up "No Text Found" after applying a gene mod

# v1.0.2 Ideas (not to implement yet)

- [ ] Torch - permanent item with battery pickups
- [ ] Spacesuit needed for spacewalk (captain, commander)
- [ ] Shapeshifter doesn't use weapons, but gets stronger as levels increase, throws objects which will act as single use projectiles
- [x] DM3000 resistant to most elements and traps but no improvement in armor strength - increases slowly with level increase but weaker than other characters
- [x] DM3000 has permanent light
- [x] DM3000 doesn't require food
- [ ] Rooms light up on entry, do away with floor lights. Some levels need to reset the breaker to activate lights
- [x] Workshop has reusable chests where items can be stored.
- [ ] Tech and biogenetics reorganised to defensive (apply to player) vs offensive (used as weapon/action) - have generic storage icon that is opened up and then reveals what it is.
- [ ] Add NPCs that need protection. End game player leaves on escape pod or goes back to stablise ship so NPCs can be rescued
- [ ] A cat that will follow the adventurer. Sign warning "Do not pet the cat".
- [ ] Renaming plan: Dungeon, King, Skeleton, DdarkLordGnoll, PrisonLevel, CavesLevel etc...
- [ ] Replace garden with one-square booth - healing tank sort of thing.
