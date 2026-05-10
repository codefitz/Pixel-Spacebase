# Pixel Spacebase Entity and Item Behavior Catalogue

This catalogue summarizes the current player-facing behavior of NPCs, enemies, bosses, and item families. Class names remain useful because many internals still preserve Shattered Pixel Dungeon compatibility.

## Playable Hero Classes

| Class | Current role and behavior |
| --- | --- |
| Commander | Starts with forcefield-oriented armor identity. Food restores some HP. Healing tech is identified from the beginning. |
| Shapeshifter | Starts with stealth/cloak identity. Better at detecting hidden doors and floor lighting. Can go longer without food. Mapping tech is identified from the beginning. |
| DM-3000 | Robot class with rocket launcher identity. Can retrofit blasters into the launcher. Does not need food, has a permanent utility light, and can consume energy packs to recharge blasters. Upgrade tech is identified from the beginning. Currently locked until the floor 15 boss is defeated. |
| Captain | Missile/ranged-weapon class. Starts with hunter disc identity. Can sense nearby enemies. Mind Vision tech is identified from the beginning. Available from the start. |

## NPCs and Quest Characters

| Entity | Current behavior |
| --- | --- |
| Chief Engineer Leonard | Mid-game survivor/engineer. Offers a repair/reforge-style quest involving parts or protection. Rewards item improvement support. |
| Hologram Distress Call | Princess Pixel of Yendor projection. Gives one of several optional hunt quests: evolved xenomorph, Dark Lord of Yendor, or armored maintenance crawler. Rewards the player after completion. |
| Quartermaster | Formerly Old Gunsmith internally. Requests a specific material or quest item, then offers blaster rewards. Reinforces the station reclamation/resistance theme. |
| Maker Bench | Wall-mounted workshop matter-recycling station. Can break items down into parts. Internally still uses the MakerBot interaction flow. |
| Y | Optional meta/anomaly quest NPC. Formerly Arp internally. Addresses the player directly, references game systems, asks the player to recover hologram emitters from the Holodeck Monarch's hard-light retainers, and trades them for a reward. Also appears as a brief floor 10 interlude after Tengu is defeated, then vanishes once the hero moves. |
| Y Trader | Follow-up/trader variant of Y. Formerly Arp Trader internally. Acknowledges survival with the same game-aware tone. |
| Queen Xenomorph | Nonstandard xenomorph personality/encounter. Implied cause of excessive xenomorph breeding on Pixel Spacebase. |
| Weak Clone | Temporary allied clone created by tech. Distracts and attacks enemies but is fragile. |
| Yog Sheep | Joke/summoned neutral creature from flock-style effects. |

## Bosses

| Boss | Floor | Current behavior |
| --- | --- | --- |
| Feral Shapeshifter | 5 | Shapeshifter crew colleague destabilized by repeated emergency shifts. Signals attack buildup, enrages, and thanks the player when restored/defeated. Unlocks Shapeshifter progression. |
| Tengu | 10 | Mobile assassin boss. Uses floor lighting systems, deception, repositioning, and precise attacks. Represents prison/security containment failure. |
| DM-300 | 15 | Heavy station defense/construction platform with an emotionally unstable command module. Detects unauthorized personnel, babbles about protection and anger, repairs itself, and drops the boss key. Unlocks DM-3000 class progression. |
| Holodeck Monarch (`King`) | 20 | Rogue hard-light training sim. Summons hologram retainers, parries, and speaks as a corrupted monarch program. |
| Yog-Dzewa | 25 | Final major boss. Bio-containment parasite fused around the evacuation spine, with appendages and larvae defending the escape route. |

## Standard Enemies

| Enemy | Current behavior |
| --- | --- |
| Xenomorph | Early hostile creature. Also spawned by alien egg infection. Current lore: imported pet species breeding violently due to a queen. |
| Albino | Rare xenomorph variant. Stronger/special mutation of the base xenomorph. |
| Tough Xeno | Tougher xenomorph variant with stench/ooze flavor. |
| Yendor Scout (`Gnoll`) | Fast light raider from Yendor. |
| Dark Lord of Yendor | Miniboss/quest enemy. Uses toxin and incendiary flechettes. |
| Yendor Shaman | Ranged/caster Yendor enemy with battle spells. |
| Yendor Shock Trooper | Large heavy raider with a combat rig that becomes more dangerous when wounded. |
| Shielded Shock Trooper | Shock trooper variant with added defensive behavior. |
| Crab / Maintenance Crawler | Early station maintenance crawler enemy. Drops parts instead of meat. |
| Great Crab / Armored Crawler | Shield-claw crawler. Blocks frontal attacks unless surprised. Drops a larger parts bundle instead of meat. |
| Squiddard | Aquatic/alien-style enemy with movement and combat behavior around passable spaces. |
| Ruptured Crew Suit (`Skeleton`) | Former skeleton. Corrupted emergency suit/servo enemy that ruptures into shrapnel when damaged enough. |
| Jawar (`Thief`) | Former thief. Steals an item and attempts to escape. |
| Jawar Scavenger (`Bandit`) | Rare Jawar variant with enhanced stealing threat. |
| Bith Acolyte (`Guard`) | Former prison guard. Security adept with a force-pull rig; mechanically still pulls or pressures the player in combat. Sprite/chain visual still needs a later pass. |
| Siphon Drone | Flying medical-drain drone that replenishes health when attacking. |
| Yendor Shock Trooper | Heavy melee enemy with low-health combat-rig overclock. |
| Facehugger (`Spinner`) | Former cave spinner. Uses web behavior as adhesive biofilament. Successful latches infect the hero like an alien egg; if the infection runs its course it spawns a tougher xenomorph. |
| Plasma Anomaly (`Elemental`) | Former fire elemental. Reactor/containment leak enemy with special damage flavor. |
| Plasma Spark (`Newborn Elemental`) | Quest-related discharge source for a faulty core module ejected by the restart sequence. |
| Jeda Knight (`Monk`) | Fast late-mid disciplined melee enemy. Text conversion is done; sprite remains future work. |
| Jeda Master (`Senior`) | Rare Jeda Knight variant with stronger strikes and brief paralysis. Text conversion is done; sprite remains future work. |
| Signal Witch (`Warlock`) | Caster enemy that weaponizes corrupted telemetry and neural feedback. |
| War Machine (`Golem`) | Heavy corridor-suppression machine from command-sector defense systems. |
| Signal Siren (`Succubus`) | Charm/hypnotise-style intrusion organism that interferes with the player's target choices. |
| Observer Horror (`Eye`) | Sci-fi horror ranged enemy with a charged focused beam. |
| Skitter Cannon (`Scorpio`) | Late containment predator with long-range crippling projectiles. |
| Acid-spitter Cannon | Rare skitter-cannon variant with acid blood and corrosive spines. |
| Old War Bot | Old combat machine with useful weapon/guardian flavor. |
| Drone | Allied or controller-based mobile unit. Now attacks hostile targets and clears mines, not the hero. |
| Turret | Station defensive turret. Automated, terminal/security themed. |
| Coolant Phantom (`WaterThing`) | Invisible or water-bound threat in flooded/coolant areas. |
| Bio-Charge Heart (`RotHeart`) | Station/organic hazard core tied to bio-charge mine structures. |
| Bio-Charge Tendril (`RotLasher`) | Immobile defensive organic hazard that attacks adjacent targets. |
| Confused Shapeshifter | Special shapeshifter-related entity affected by the same identity collapse; can replace defeated enemies and become a static restored character. |

## Core Item Systems

| Item family | Current behavior |
| --- | --- |
| Food | Restores hunger for biological classes. Class-specific effects apply: Commander heals, DM-3000 can consume energy packs to recharge blasters without needing food, Shapeshifter/Captain get normal hunger value. Emergency auto-eat now triggers at 25% HP or below for non-DM-3000 heroes if valid food is in the backpack. |
| Alien Egg / Dead Alien Bug (`AlienPod`) | Internal class remains `AlienPod` for the dropped bug. The alien egg device uses the alien pod/egg item icon, plants as an egg mine, briefly fades to black and shows the infection dialog when triggered by the hero, then drops a raw bug. Raw bug cannot be eaten. Can be combined in the Fabricator into xeno-booster/gene-mod outcomes; crafted xeno-boosters use the x-port/Bionetics icon. |
| Gene Mods (`ExperimentalTech`) | Former potion-like system. Single-use biological/tech effects such as healing, strength, invisibility, toxic payloads, security override, cryogenics, and other enhancements. Regen Gene Mod cures alien egg infection. |
| Tech (`Script`) | Former scroll-like system. Single-use technology effects such as identify, mapping, upgrade, recharging, teleport, cloning, terror/pheromone, fix, and other utility effects. |
| Blasters | Rechargeable ranged weapons with charge counts. Each blaster has a distinct projectile/effect and can be used by DM-3000 launcher mechanics. |
| Floor lighting | Unpowered floor lighting activates when any non-flying character stands on it; it still uses normal press handling for traps and mines. Spent trap/floor-lighting tiles become inactive, stay off permanently, and remain visible as spent powered plates. |
| Projectile pathing | Missiles and thrown items can cross unpowered floor lighting even though it still blocks sight until activated. |
| Weapons | Melee and missile weapons with tiers, upgrades, malfunctions, and enhancements. |
| Armor | Defensive equipment with strength requirements, upgrades, malfunctions, and enhancements. |
| Modules | Ring-like equipment that modifies combat or utility stats. Can malfunction. |
| Artifacts | Unique charge/progression items with special mechanics, such as stealth, gravity control, time manipulation, alien DNA, recycling, and YendorTech shield behavior. |
| Workshops | Ordinary workshop stock is random per floor and stays on that floor. Special support items such as backpack-modifier containers and Time Folder batteries carry forward within the same area if left in the workshop. Player-dropped normal heaps inside the workshop room carry forward into reusable workshop storage chests instead of being left behind on the old floor. Generated workshop rooms paint a fixed interior template with a Maker Bench, storage chests, and a Chief Engineer-style upgrade bench for merging two compatible upgradeable items. |
| Containers | Inventory sub-containers such as backpack-style storage, Bionetics gene-mod storage, and blaster holster. |
| Mines / Devices | Placeable or environmental charges. Can be triggered from floors, fabricated, combined, or cleared by drones. |
| Parts | Currency/material used in workshops and Maker-Bot/recycling flows. |
| Keys | Floor/depth access control for locked doors, chests, and boss exits. Security Block floors also place a reusable max-security override keycard in the entrance room; it opens normal locked detention doors on that floor without being consumed. |
| Dewdrop / Medigel | Small healing resource from station emergency systems. Can fill air tank-style storage. |
| Air Tank | Stores medigel droplets and can heal; a full tank has stronger restorative/blessing utility. |
| Escape Pod Override (`Amulet`) | Internal class remains `Amulet`, but player-facing text now frames the winning artifact as the command override needed to access sealed evacuation systems. It can launch the final pod immediately, or justify returning to reopen sealed rescue cradles for survivors. |

## Notable Blasters

| Blaster | Current behavior |
| --- | --- |
| Missile Blaster | Basic reliable ranged damage with multiple charges. |
| Lazer Gun | Light/damage beam that can blind, reveal/mapping-adjacent features, and damage hard-light, corrupted, or unstable targets harder. |
| Wave Blaster | Blast projectile that damages and pushes enemies away from the detonation point. |
| Disintegrator | Piercing beam that damages multiple targets and burns through terrain. |
| Flamethrower | Cone fire attack, consuming charge for area damage. |
| Freezethrower | Cold damage and chill/freeze behavior. |
| Shock Blaster | Electrical chain/bounce damage, stronger around water but risky at close range. |
| Dominator | Attempts to permanently dominate enemies; stronger against weakened targets and ineffective against bosses. |
| Bio-Siphon Blaster (`VampiricBlaster`) | Former vampiric/hypnotise blaster. Now steals life from hostile living targets, healing the hero from actual HP removed. Still transfers hero life into allies or support effects, and disrupts unliving or projected targets without healing. |
| Venom Blaster | Creates venom/toxic gas cloud at target area. |
| Repair Blaster (`EMP`) | Internal class remains `EMP`. Repair beam fixes compatible terrain, forces locks with an open-or-jam roll, repairs allied machines, and damages hostile machine enemies. |
| Malfunctioning Blaster | Chaotic blaster that can randomly produce many effects, including harmful ones. |

## Notable Artifacts and Utility Items

| Item | Current behavior |
| --- | --- |
| Alien DNA | Risk/reward self-modification artifact. Repeated use damages or can kill the hero, but grants enhanced lifeforce progression. |
| Stealth Module | Cloak/camouflage artifact. Grants invisibility until cancelled, charge expires, or the player attacks/uses obvious actions. |
| HoloPad | Hologram/companion-flavored artifact with holobattery progression and quest flavor. |
| Gravity Gun | Pulls or moves enemies/player through space, with boss-arena limitations. |
| Time Folder | Stasis and time-freeze utility. Can make the hero invulnerable while time passes or allow actions while time is frozen. |
| Portable Maker / Portal Tunneler | Stores a coordinate and returns/relocates using spacetime tunneling behavior. |
| Matter Recycler (`PortableRender`) | One-time compact reclamation rig for breaking selected items into parts. |
| YendorTech Shield | Absorbs mine charges/devices to improve protection; Yendor-compatible tech artifact. |
| Buggy Compiler | Bug-themed artifact that consumes tech/scripts for unusual utility and dependency flavor. |
| Tech Toolkit | Item-improvement/crafting support artifact. |
| Survival Module | Survival-oriented artifact tied to hunger/food/resource behavior. |
| Impact Gauntlet | Former Knuckleduster. Fast melee weapon presented as a powered maintenance impact tool. |
| Mastery Datacore (`TomeOfMastery`) | Former Tome of Mastery. Player-facing text now presents subclass selection as synchronizing with an advanced combat protocol. |

## Quest and Special Items

| Item | Current behavior |
| --- | --- |
| Screwdriver | Leonard quest tool for harvesting parts from exposed components. |
| Core Stabilizer (`CeremonialCandle`) | Former ceremonial candle. Used with the restart marker to focus energy for a damaged core restart. |
| Faulty Core Module (`Embers`) | Quartermaster quest material recovered from the failed core restart sequence. |
| Ore Cutter (`Pickaxe`) | Heavy industrial cutter quest item, also usable as a weapon. |
| Containment Ash (`CorpseDust`) | Sealed hostile residue quest item with residual anomaly flavor. |
| Bio-Charge Core / Rotberry Mine Charge | Organic/device quest ingredient tied to the Quartermaster and Fabricator systems. |
| Hunter Disc | Captain starter/ranged identity weapon. |
| Hologram Emitter (`DwarfToken`) | Internal class remains `DwarfToken` for save compatibility. Drops from the Holodeck Monarch's hard-light retainers after Y gives the objective, and can be traded back to Y for the quest reward. |
| Darts / Hunter's Dart | Missile-weapon family still earmarked for stronger sci-fi naming/art in earlier conversion work. |
| Bomb | Explosive utility item. |
| Mobile Turret | Deployable/mobile weapon concept, now intended as station tech rather than spirit magic. |

## Environmental Interactables

| Interactable | Current behavior |
| --- | --- |
| Floor Lighting | Former vents/traps. Hidden or visible station floor systems that trigger gas, fire, lightning, teleport, alarm, summoning, disintegration, pitfall, and other effects. Behavior is still inherited from the vent system. |
| Fabricator | Former alchemy/crafting terminal. Combines Dead Alien Bugs, mine charges, and gene mod/tech inputs into useful outputs. |
| Workshop | Former shop. Present every level; stock refreshes at area starts and persists within an area. |
| Terminal / Powered-Off Terminal | Former wells. Used for mapping/transmutation/healing-style interactions depending on subclass or original well logic. |
| Chasm / Reactor Shaft | Fall/vacuum hazard. Can kill or drop the player to lower depths. |
| Escape Pod / Blocked Airlock | End/escape-facing environment text. Currently blocked until end-game conditions. |
