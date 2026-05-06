# Pixel Spacebase Plan

This plan is organized by the kind of work needed next. Use it as the active backlog, then move completed work to the done section with a short note about what changed.

## Workflow

1. Add new ideas to **Suggestions** first, especially when the direction is not settled.
2. Promote accepted ideas into **Sprites**, **Tiles**, **Mechanics**, or **Cosmetic / Narrative**.
3. Implement small batches that can be tested quickly.
4. Update this plan and any relevant docs after each batch.
5. Build the core debug APK after code or asset changes.

## Suggestions

- [ ] Decide on a replacement concept for the King of Dwarves / floor 20 holodeck boss. Options to consider: rogue holodeck monarch, corrupted historical sim, glitched training tyrant, or deliberately anachronistic fantasy hologram.
- [ ] Decide on the final boss direction. Options to consider: escape-pod guardian AI, ancient ship parasite, extradimensional stowaway, corrupted evacuation protocol, or biomechanical station core.
- [ ] Decide on the floor 22-24 area name and mood. Options to consider: Bio-Containment Decks, Quarantine Habitat, Infested Habitation, Deep Life-Support, or Xenobiology Annex.
- [ ] Decide whether the floor 26 finale should force a choice: escape immediately, go back for another objective, or reclaim/seal the station.
- [ ] Decide whether the workshop should become a fixed room template before adding more workbench mechanics.

## Sprites

- [ ] Replicator Swarm sprite to be updated.
- [ ] Loader armour sprite could do with a makeover.
- [ ] Tengu: keep as an assassin but update sprite to futuristic.
- [ ] King of Dwarves / floor 20 boss sprite needs a sci-fi or holodeck-relative replacement once the boss concept is chosen.
- [ ] Skeleton sprite still needs updating to a suitably sci-fi enemy.
- [ ] Thief sprite should look like a Jawa-style scavenger.
- [ ] Guard sprite should become a Bith-style apprentice; remove or replace the chain visual with force-pull styling.
- [ ] Spinner sprite should become a little facehugger.
- [ ] Monk sprite should become a Jeda Knight.
- [ ] Senior sprite should become a Jeda Master.

## Tiles

- [ ] Prison / security block tiles should match a futuristic spaceship detention area.
- [ ] Lower Engineering / caves tiles need a lower engineering aesthetic.
- [ ] City / command-sector tiles need a Star Trek TNG-style pass.
- [ ] Ladder tiles need to be swapped around to match ascending/descending.
- [ ] VENT should visually convert to an electric charge / spent powered plate after triggering, rather than a colored vent.
- [x] INACTIVE_VENT now stays off permanently after traps/floor-lighting are spent.
- [x] SIGN renamed to terminal.
- [ ] WATER needs a sci-fi aesthetic per area while keeping current gameplay properties.

## Mechanics

- [ ] Prison / security levels should use mostly locked doors, with one super key on each level.
- [ ] Y should send the player to take down rogue holograms from the holodeck, with hologram emitters as drops.
- [ ] After the floor 10 boss is defeated, Y should pop in, trigger the level change, tease the player, then disappear after the hero moves with: "We shall meet again if you survive".
- [x] Player-dropped items in workshop rooms now carry forward with workshop stock inside the same area.
- [ ] Add an option to open and scroll the full log.
- [x] Stepping on an alien egg now briefly fades to black and shows the "what happened" infection dialog.
- [x] Xeno-booster now uses the x-port/Bionetics icon after crafting.
- [x] Floor lighting now lights up when non-flying characters stand on it, while trap/floor-lighting activation still runs through normal press handling.
- [x] Missiles and thrown items can cross unpowered floor lighting without treating it as projectile-blocking terrain.
- [ ] Turn the Maker-Bot into a workbench and anchor it to a wall.
- [ ] Make the workshop the same size and layout for each area.
- [ ] Add another workshop bench inspired by the Chief Engineer that lets the player combine items to upgrade.
- [ ] Spinner / facehugger should latch like the alien egg and spawn a stronger xeno if it succeeds.

## Cosmetic / Narrative

- [ ] Floor 5 boss: frame the Feral Shapeshifter as the shapeshifter colleague the player unlocks, driven unstable by identity issues from repeated shifting.
- [ ] Floors 6-9 should be reframed as a futuristic spaceship prisoner detention / security area.
- [ ] Floor 10 boss death line: "Free at last" should imply death.
- [ ] Floors 16-19 should clarify whether the area is habitation, command sector, or holodeck-adjacent.
- [ ] Floor 20 boss room can be the holodeck; decide whether to keep a fantasy figure as an intentional hologram or replace the boss identity entirely.
- [ ] Floors 22-24 should shift from Demon Halls to eerie space-horror, possibly an infested habitation or containment area.
- [ ] Floor 25 boss should guard the route to the escape pods.
- [ ] Floor 25 boss should evoke a familiar classic space-horror trope without being the alien queen.
- [ ] Floor 26 finale should be the route to the escape pod.
- [ ] Floor 26 may need a twist that gives the player a reason to go back.
- [x] Arp / Arp Trader player-facing name updated to Y.
- [x] Y now addresses the player directly and references inventory, badges, rankings, and quest tracking.
- [x] Old Gunsmith updated to a more futuristic quest character.
- [x] Ceremonial candle and ceremony text reframed as reactivating the core.

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
