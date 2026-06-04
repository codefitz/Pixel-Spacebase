# Sprite Inventory

Generated from `core/src/main/java/com/wafitz/pixelspacebase/sprites` and `core/src/main/assets`.

Column notes:

- `Sheet px` is the actual PNG size.
- `Frame px` is the frame size used by the Java sprite class.
- `Grid` and `Frames in sheet` are calculated from `Sheet px / Frame px`, rounded down.
- `Unique frame indexes used` is based on literal frame indexes referenced in the sprite class animations. Dynamic offsets are counted by their literal local frame range where possible.
- Many sheets have spare pixels because the original art sheets are wider than the exact animation frames used by the class.

## Shared / Dynamic Sheets

| Sprite use | Asset | Sheet px | Frame px | Grid | Frames in sheet | Notes |
|---|---:|---:|---:|---:|---:|---|
| `HeroSprite` commander | `commander.png` | 256x128 | 12x15 | 21x8 | 168 | Uses hero tier row offsets. |
| `HeroSprite` DM-3000 | `dm3000.png` | 256x128 | 12x15 | 21x8 | 168 | Uses hero tier row offsets. |
| `HeroSprite` shapeshifter | `shapeshifter.png` | 256x128 | 12x15 | 21x8 | 168 | Also used as the tier offset reference sheet. |
| `HeroSprite` captain | `captain.png` | 256x128 | 12x15 | 21x8 | 168 | Uses hero tier row offsets. |
| `CloneSprite` | hero class sheet | 256x128 | 12x15 | 21x8 | 168 | Uses the current hero class sheet and tier. |
| `ItemSprite` | `items.png` | 256x512 | 16x16 | 16x32 | 512 | Shared item icon sheet. |

## Character / Mob Sprites

| Sprite class | Asset | Sheet px | Frame px | Grid | Frames in sheet | Unique frame indexes used | Notes |
|---|---:|---:|---:|---:|---:|---:|---|
| `AcidicSprite` | `scorpio.png` | 256x64 | 18x17 | 14x3 | 42 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `AlbinoSprite` | `xenomorph.png` | 256x64 | 16x15 | 16x4 | 64 | 15 | Sheet dimensions are not an exact multiple of frame size. |
| `BanditSprite` | `thief.png` | 256x32 | 12x13 | 21x2 | 42 | 12 | Sheet dimensions are not an exact multiple of frame size. |
| `BruteSprite` | `brute.png` | 132x32 | 12x16 | 11x2 | 22 | 11 |  |
| `BurningFistSprite` | `burning_fist.png` | 256x32 | 24x17 | 10x1 | 10 | 7 | Sheet dimensions are not an exact multiple of frame size. |
| `ConfusedShapeshifterSprite` | `confused_shapeshifter.png` | 256x16 | 16x16 | 16x1 | 16 | 10 |  |
| `ContainmentEchoSprite` | `containment_echo.png` | 64x16 | 16x15 | 4x1 | 4 | 4 | Sheet dimensions are not an exact multiple of frame size. |
| `ContainmentMassSprite` | `containment_mass.png` | 256x32 | 20x19 | 12x1 | 12 | 10 | Sheet dimensions are not an exact multiple of frame size. |
| `CrabSprite` | `crab.png` | 256x32 | 16x16 | 16x2 | 32 | 14 |  |
| `DM300Sprite` | `dm300.png` | 256x32 | 22x20 | 11x1 | 11 | 9 | Sheet dimensions are not an exact multiple of frame size. |
| `DroneSprite` | `drone.png` | 256x16 | 16x16 | 16x1 | 16 | 11 |  |
| `ElementalSprite` | `elemental.png` | 256x32 | 12x14 | 21x2 | 42 | 14 | Sheet dimensions are not an exact multiple of frame size. |
| `EyeSprite` | `eye.png` | 256x32 | 16x18 | 16x1 | 16 | 10 | Sheet dimensions are not an exact multiple of frame size. |
| `FacehuggerSprite` | `spinner.png` | 256x16 | 16x16 | 16x1 | 16 | 10 |  |
| `FeralShapeshifterSprite` | `feral_shapeshifter.png` | 256x16 | 20x14 | 12x1 | 12 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `GreatCrabSprite` | `crab.png` | 256x32 | 16x16 | 16x2 | 32 | 14 |  |
| `GuardSprite` | `guard.png` | 256x16 | 12x16 | 21x1 | 21 | 15 | Sheet dimensions are not an exact multiple of frame size. |
| `GunsmithSprite` | `gunsmith.png` | 64x16 | 12x14 | 5x1 | 5 | 4 | Sheet dimensions are not an exact multiple of frame size. |
| `HolodeckMonarchSprite` | `holodeck_monarch.png` | 256x16 | 16x16 | 16x1 | 16 | 16 |  |
| `HologramSprite` | `hologram.png` | 128x16 | 14x15 | 9x1 | 9 | 8 | Sheet dimensions are not an exact multiple of frame size. |
| `HoodedRaiderCommanderSprite` | `outer_colony_scout.png` | 256x32 | 12x15 | 21x2 | 42 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `ImpSprite` | `npc2.png` | 72x16 | 12x14 | 6x1 | 6 | 5 | Sheet dimensions are not an exact multiple of frame size. |
| `JedaKnightSprite` | `jeda_knight.png` | 256x32 | 15x14 | 17x2 | 34 | 17 | Sheet dimensions are not an exact multiple of frame size. |
| `LarvaSprite` | `larva.png` | 128x8 | 12x8 | 10x1 | 10 | 9 | Sheet dimensions are not an exact multiple of frame size. |
| `LeonardSprite` | `leonard.png` | 64x16 | 13x16 | 4x1 | 4 | 4 | Sheet dimensions are not an exact multiple of frame size. |
| `MakerBotSprite` | `makerbot.png` | 32x16 | 14x14 | 2x1 | 2 | 1 | Sheet dimensions are not an exact multiple of frame size. |
| `MaskedPrisonerSprite` | `masked_prisoner.png` | 256x16 | 14x16 | 18x1 | 18 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `NewbornElementalSprite` | `elemental.png` | 256x32 | 12x14 | 21x2 | 42 | 14 | Sheet dimensions are not an exact multiple of frame size. |
| `OldWarBotSprite` | `war_bot.png` | 256x16 | 12x15 | 21x1 | 21 | 16 | Sheet dimensions are not an exact multiple of frame size. |
| `OuterColonyPsionSprite` | `outer_colony_psion.png` | 256x16 | 12x15 | 21x1 | 21 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `OuterColonyScoutSprite` | `outer_colony_scout.png` | 256x32 | 12x15 | 21x2 | 42 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `QueenXenoSprite` | `xqueen.png` | 128x64 | 16x17 | 8x3 | 24 | 7 | Sheet dimensions are not an exact multiple of frame size. |
| `ReplicatorSwarmSprite` | `swarm.png` | 256x16 | 16x16 | 16x1 | 16 | 15 |  |
| `RotHeartSprite` | `rot_heart.png` | 128x16 | 16x16 | 8x1 | 8 | 8 |  |
| `RotLasherSprite` | `rot_lasher.png` | 128x16 | 12x16 | 10x1 | 10 | 7 | Sheet dimensions are not an exact multiple of frame size. |
| `RottingFistSprite` | `rotting_fist.png` | 128x32 | 24x17 | 5x1 | 5 | 5 | Sheet dimensions are not an exact multiple of frame size. |
| `RupturedCrewSuitSprite` | `ruptured_crew_suit.png` | 256x16 | 12x15 | 21x1 | 21 | 17 | Sheet dimensions are not an exact multiple of frame size. |
| `ScorpioSprite` | `scorpio.png` | 256x64 | 18x17 | 14x3 | 42 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `SeniorSprite` | `jeda_knight.png` | 256x32 | 15x14 | 17x2 | 34 | 17 | Sheet dimensions are not an exact multiple of frame size. |
| `ShieldedSprite` | `brute.png` | 132x32 | 12x16 | 11x2 | 22 | 11 |  |
| `SignalLeechSprite` | `signal_leech.png` | 256x16 | 12x15 | 21x1 | 21 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `SignalSirenSprite` | `signal_siren.png` | 256x16 | 12x15 | 21x1 | 21 | 13 | Sheet dimensions are not an exact multiple of frame size. |
| `SiphonDroneSprite` | `bat.png` | 128x16 | 15x15 | 8x1 | 8 | 7 | Sheet dimensions are not an exact multiple of frame size. |
| `StationCatSprite` | `cat.png` | 80x16 | 10x16 | 8x1 | 8 | 8 |  |
| `SurvivorSprite` | `npc.png` | 104x48 | 12x14 | 8x3 | 24 | 4 | Sheet dimensions are not an exact multiple of frame size. |
| `ThiefSprite` | `thief.png` | 256x32 | 12x13 | 21x2 | 42 | 13 | Sheet dimensions are not an exact multiple of frame size. |
| `ToughXenoSprite` | `xenomorph.png` | 256x64 | 16x15 | 16x4 | 64 | 15 | Sheet dimensions are not an exact multiple of frame size. |
| `TurretSprite` | `wraith.png` | 128x16 | 14x15 | 9x1 | 9 | 8 | Sheet dimensions are not an exact multiple of frame size. |
| `YSprite` | `arp.png` | 64x16 | 12x14 | 5x1 | 5 | 5 | Sheet dimensions are not an exact multiple of frame size. |
| `UndeadSprite` | `undead.png` | 256x16 | 12x16 | 21x1 | 21 | 17 | Sheet dimensions are not an exact multiple of frame size. |
| `WarMachineSprite` | `war_machine.png` | 256x16 | 16x16 | 16x1 | 16 | 14 |  |
| `WaterThingSprite` | `water_things.png` | 256x16 | 12x16 | 21x1 | 21 | 15 | Sheet dimensions are not an exact multiple of frame size. |
| `XenomorphSprite` | `xenomorph.png` | 256x64 | 16x15 | 16x4 | 64 | 15 | Sheet dimensions are not an exact multiple of frame size. |
