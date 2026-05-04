# Pixel Spacebase Entity and Item Behavior Catalogue

This catalogue summarizes the current player-facing behavior of NPCs, enemies, bosses, and item families. Class names remain useful because many internals still preserve Shattered Pixel Dungeon compatibility.

## Playable Hero Classes

| Class | Current role and behavior |
| --- | --- |
| Commander | Starts with forcefield-oriented armor identity. Food restores some HP. Healing tech is identified from the beginning. |
| Shapeshifter | Starts with stealth/cloak identity. Better at detecting hidden doors and vents. Can go longer without food. Mapping tech is identified from the beginning. |
| DM3000 | Robot class with rocket launcher/DM3000 launcher identity. Can retrofit blasters into the launcher. Food recharges blasters. Upgrade tech is identified from the beginning. Currently locked until the floor 15 boss is defeated. |
| Captain | Missile/ranged-weapon class. Starts with hunter disc identity. Can sense nearby enemies. Mind Vision tech is identified from the beginning. Available from the start. |

## NPCs and Quest Characters

| Entity | Current behavior |
| --- | --- |
| Chief Engineer Leonard | Mid-game survivor/engineer. Offers a repair/reforge-style quest involving parts or protection. Rewards item improvement support. |
| Hologram Distress Call | Princess Pixel of Yendor projection. Gives one of several optional hunt quests: evolved xenomorph, Dark Lord of Yendor, or armored maintenance crawler. Rewards the player after completion. |
| Old Gunsmith | Requests a specific material or quest item, then offers blaster rewards. Reinforces the station reclamation/resistance theme. |
| Maker-Bot | Workshop/matter-recycling support NPC. Can break items down into parts. Appears unaware or unconcerned that the station is under attack. |
| Arp | Optional meta/anomaly quest NPC. Asks the player to kill a set number of golems or monks and rewards completion. |
| Arp Trader | Follow-up/trader variant of Arp. Acknowledges survival and provides additional interaction. |
| Queen Xenomorph | Nonstandard xenomorph personality/encounter. Implied cause of excessive xenomorph breeding on Pixel Spacebase. |
| Weak Clone | Temporary allied clone created by tech. Distracts and attacks enemies but is fragile. |
| Yog Sheep | Joke/summoned neutral creature from flock-style effects. |

## Bosses

| Boss | Floor | Current behavior |
| --- | --- | --- |
| Feral Shapeshifter | 5 | Aggressive shapeshifter boss. Signals attack buildup, enrages, and thanks the player when restored/defeated. Unlocks Shapeshifter progression. |
| Tengu | 10 | Mobile assassin boss. Uses vents, deception, repositioning, and precise attacks. Represents prison/security containment failure. |
| DM-300 | 15 | Heavy machine boss. Detects unauthorized personnel, repairs itself, and drops the boss key. Unlocks DM3000 class progression. |
| King of Dwarves | 20 | Undead ruler boss. Summons undead retainers, parries, and speaks of immortality. Still mostly inherited fantasy lore. |
| Yog-Dzewa | 25 | Final major boss. Old-god entity with fists/larvae support. Current end threat for the deepest area. |

## Standard Enemies

| Enemy | Current behavior |
| --- | --- |
| Xenomorph | Early hostile creature. Also spawned by alien egg infection. Current lore: imported pet species breeding violently due to a queen. |
| Albino | Rare xenomorph variant. Stronger/special mutation of the base xenomorph. |
| Tough Xeno | Tougher xenomorph variant with stench/ooze flavor. |
| Yendor Scout (`Gnoll`) | Fast light raider from Yendor. |
| Dark Lord of Yendor | Miniboss/quest enemy. Uses toxin and incendiary flechettes. |
| Yendor Shaman | Ranged/caster Yendor enemy with battle spells. |
| Yendor Brute | Large heavy raider that becomes more dangerous when wounded. |
| Shielded | Brute variant with added defensive behavior. |
| Crab / Maintenance Crawler | Early station maintenance crawler enemy. |
| Great Crab / Armored Crawler | Shield-claw crawler. Blocks frontal attacks unless surprised. |
| Squiddard | Aquatic/alien-style enemy with movement and combat behavior around passable spaces. |
| Skeleton | Undead enemy that explodes/disintegrates when damaged enough. |
| Thief | Steals an item and attempts to escape. |
| Bandit | Rare thief variant with enhanced stealing threat. |
| Guard | Security/prison-style enemy. Pulls or pressures the player in combat. |
| Bat | Vampire-bat style enemy that replenishes health when attacking. |
| Brute | Heavy melee enemy with low-health threat spike. |
| Spinner | Web/poison-style enemy from the deeper middle areas. |
| Elemental | Elemental enemy with special damage flavor. |
| Newborn Elemental | Quest-related elemental source for embers. |
| Monk | Fast late-mid enemy with disciplined melee behavior. |
| Senior | Rare monk variant. |
| Warlock | Caster enemy. Soul/dark magic flavor and ranged pressure. |
| Golem | Heavy mechanical/magical enemy from later areas. |
| Succubus | Charm/hypnotise-style enemy that interferes with the player's target choices. |
| Eye | Dangerous ranged enemy with charged deathgaze beam. |
| Scorpio | Late ranged enemy with crippling projectiles. |
| Acidic | Rare scorpio variant with acid theme. |
| Old War Bot | Old combat machine with useful weapon/guardian flavor. |
| Drone | Allied or controller-based mobile unit. Now attacks hostile targets and clears mines, not the hero. |
| Turret | Station defensive turret. Automated, terminal/security themed. |
| WaterThing | Invisible or water-bound threat in flooded areas. |
| Rot Heart | Station/organic hazard core tied to rotberry mine structures. |
| Rot Lasher | Immobile defensive plant/organic hazard that attacks adjacent targets. |
| Confused Shapeshifter | Special shapeshifter-related entity that can replace defeated enemies and become a static restored character. |

## Core Item Systems

| Item family | Current behavior |
| --- | --- |
| Food | Restores hunger. Class-specific effects apply: Commander heals, DM3000 recharges blasters, Shapeshifter/Captain get normal hunger value. Emergency auto-eat now triggers at 25% HP or below if valid food is in the backpack. |
| Dead Alien Bug (`AlienPod`) | Internal class remains `AlienPod`. Raw bug cannot be eaten. Can be combined in the Fabricator into xeno-booster/gene-mod outcomes. |
| Gene Mods (`ExperimentalTech`) | Former potion-like system. Single-use biological/tech effects such as healing, strength, invisibility, toxic payloads, security override, cryogenics, and other enhancements. Regen Gene Mod cures alien egg infection. |
| Tech (`Script`) | Former scroll-like system. Single-use technology effects such as identify, mapping, upgrade, recharging, teleport, cloning, terror/pheromone, fix, and other utility effects. |
| Blasters | Rechargeable ranged weapons with charge counts. Each blaster has a distinct projectile/effect and can be used by DM3000 launcher mechanics. |
| Weapons | Melee and missile weapons with tiers, upgrades, malfunctions, and enhancements. |
| Armor | Defensive equipment with strength requirements, upgrades, malfunctions, and enhancements. |
| Modules | Ring-like equipment that modifies combat or utility stats. Can malfunction. |
| Artifacts | Unique charge/progression items with special mechanics, such as stealth, gravity control, time manipulation, alien DNA, recycling, and YendorTech shield behavior. |
| Containers | Inventory sub-containers such as backpack-style storage and blaster holster. |
| Mines / Devices | Placeable or environmental charges. Can be triggered from floors, fabricated, combined, or cleared by drones. |
| Parts | Currency/material used in workshops and Maker-Bot/recycling flows. |
| Keys | Floor/depth access control for locked doors, chests, and boss exits. |
| Dewdrop / Medigel | Small healing resource from station emergency systems. Can fill air tank-style storage. |
| Air Tank | Stores medigel droplets and can heal; a full tank has stronger restorative/blessing utility. |
| Amulet of Yendor | Current inherited end-game artifact. Winning artifact is not yet converted to Pixel Spacebase lore. |

## Notable Blasters

| Blaster | Current behavior |
| --- | --- |
| Missile Blaster | Basic reliable ranged damage with multiple charges. |
| Lazer Gun | Light/damage beam that can blind, reveal/mapping-adjacent features, and damage demonic/undead targets harder. |
| Wave Blaster | Blast projectile that damages and pushes enemies away from the detonation point. |
| Disintegrator | Piercing beam that damages multiple targets and burns through terrain. |
| Flamethrower | Cone fire attack, consuming charge for area damage. |
| Freezethrower | Cold damage and chill/freeze behavior. |
| Shock Blaster | Electrical chain/bounce damage, stronger around water but risky at close range. |
| Dominator | Attempts to permanently dominate enemies; stronger against weakened targets and ineffective against bosses. |
| Bio-Siphon Blaster (`VampiricBlaster`) | Former vampiric/hypnotise blaster. Now steals life from hostile living targets, healing the hero from actual HP removed. Still transfers hero life into allies or support effects, and disrupts undead without healing. |
| Venom Blaster | Creates venom/toxic gas cloud at target area. |
| EMP | Anti-machine/anti-tech style blaster with disabling utility. |
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

## Quest and Special Items

| Item | Current behavior |
| --- | --- |
| Screwdriver | Leonard quest tool for harvesting parts from exposed components. |
| Embers | Gunsmith quest material from newborn elemental/ritual content. |
| Pickaxe | Heavy mining/tool quest item, also usable as a weapon. |
| Corpse Dust | Inherited quest item with malevolent-energy flavor; still needs spacebase conversion. |
| Rotberry / Rotberry Mine Charge | Organic/device quest ingredient tied to the gunsmith and Fabricator systems. |
| Hunter Disc | Captain starter/ranged identity weapon. |
| Darts / Hunter's Dart | Missile-weapon family still earmarked for stronger sci-fi naming/art in earlier conversion work. |
| Bomb | Explosive utility item. |
| Mobile Turret | Deployable/mobile weapon concept, now intended as station tech rather than spirit magic. |

## Environmental Interactables

| Interactable | Current behavior |
| --- | --- |
| Vents | Former traps. Hidden or visible station mechanisms that trigger gas, fire, lightning, teleport, alarm, summoning, disintegration, pitfall, and other effects. |
| Fabricator | Former alchemy/crafting terminal. Combines Dead Alien Bugs, mine charges, and gene mod/tech inputs into useful outputs. |
| Workshop | Former shop. Present every level; stock refreshes at area starts and persists within an area. |
| Terminal / Powered-Off Terminal | Former wells. Used for mapping/transmutation/healing-style interactions depending on subclass or original well logic. |
| Chasm / Reactor Shaft | Fall/vacuum hazard. Can kill or drop the player to lower depths. |
| Escape Pod / Blocked Airlock | End/escape-facing environment text. Currently blocked until end-game conditions. |
