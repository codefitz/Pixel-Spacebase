# Sprite Inventory

Generated from `core/src/main/java/com/wafitz/pixelspacebase/sprites` and `core/src/main/assets`.

Column notes:

- `Sheet px` is the actual PNG size.
- `Frame px` is the physical PNG frame size. Java sprite classes still use the corresponding 1/4-sized logical dimensions.
- `Grid` and `Frames in sheet` are calculated from `Sheet px / Frame px`, rounded down.
- `Unique frame indexes used` is based on literal frame indexes referenced in the sprite class animations. Dynamic offsets are counted by their literal local frame range where possible.
- Many sheets have spare pixels because the original art sheets are wider than the exact animation frames used by the class.

## Shared / Dynamic Sheets

| Sprite use | Asset | Sheet px | Frame px | Grid | Frames in sheet | Notes |
|---|---:|---:|---:|---:|---:|---|
| `HeroSprite` commander | `commander.png` | 1024x512 | 48x60 | 21x8 | 168 | Uses hero tier row offsets. |
| `HeroSprite` DM-3000 | `dm3000.png` | 1024x512 | 48x60 | 21x8 | 168 | Uses hero tier row offsets. |
| `HeroSprite` shapeshifter | `shapeshifter.png` | 1024x512 | 48x60 | 21x8 | 168 | Also used as the tier offset reference sheet. |
| `HeroSprite` captain | `captain.png` | 1024x512 | 48x60 | 21x8 | 168 | Uses hero tier row offsets. |
| `CloneSprite` | hero class sheet | 1024x512 | 48x60 | 21x8 | 168 | Uses the current hero class sheet and tier. |
| `ItemSprite` | `items.png` | 1024x2048 | 64x64 | 16x32 | 512 | Shared item icon sheet. |

## Character / Mob Sprites

| Sprite class | Asset | Sheet px | Frame px | Grid | Frames in sheet | Unique frame indexes used | Notes |
|---|---:|---:|---:|---:|---:|---:|---|
| `AcidicSprite` | `scorpio.png` | 1024x256 | 72x68 | 14x3 | 42 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `AlbinoSprite` | `xenomorph.png` | 1024x256 | 64x60 | 16x4 | 64 | 15 | Sheet dimensions are not an exact multiple of frame size. |
| `JawarScavengerSprite` | `jawar.png` | 624x312 | 48x52 | 13x6 | 78 | 12 | Sheet dimensions are not an exact multiple of frame size. |
| `OuterColonyShockTrooperSprite` | `outer_colony_shock_trooper.png` | 528x128 | 48x64 | 11x2 | 22 | 11 |  |
| `BurningFistSprite` | `burning_fist.png` | 1024x128 | 96x68 | 10x1 | 10 | 7 | Sheet dimensions are not an exact multiple of frame size. |
| `ConfusedShapeshifterSprite` | `confused_shapeshifter.png` | 1024x64 | 64x64 | 16x1 | 16 | 10 |  |
| `ContainmentEchoSprite` | `containment_echo.png` | 256x64 | 64x60 | 4x1 | 4 | 4 | Sheet dimensions are not an exact multiple of frame size. |
| `ContainmentMassSprite` | `containment_mass.png` | 1024x128 | 80x76 | 12x1 | 12 | 10 | Sheet dimensions are not an exact multiple of frame size. |
| `MaintenanceCrawlerSprite` | `maintenance_crawler.png` | 1024x128 | 64x64 | 16x2 | 32 | 14 |  |
| `DM300Sprite` | `dm300.png` | 1024x128 | 88x80 | 11x1 | 11 | 9 | Sheet dimensions are not an exact multiple of frame size. |
| `DroneSprite` | `drone.png` | 1024x64 | 64x64 | 16x1 | 16 | 11 |  |
| `ElementalSprite` | `elemental.png` | 1024x128 | 48x56 | 21x2 | 42 | 14 | Sheet dimensions are not an exact multiple of frame size. |
| `EyeSprite` | `eye.png` | 1024x128 | 64x72 | 16x1 | 16 | 10 | Sheet dimensions are not an exact multiple of frame size. |
| `FacehuggerSprite` | `facehugger.png` | 1024x64 | 64x64 | 16x1 | 16 | 10 |  |
| `FeralShapeshifterSprite` | `feral_shapeshifter.png` | 1024x64 | 80x56 | 12x1 | 12 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `ArmoredCrawlerSprite` | `maintenance_crawler.png` | 1024x128 | 64x64 | 16x2 | 32 | 14 |  |
| `BithAcolyteSprite` | `bith_acolyte.png` | 1024x64 | 48x64 | 21x1 | 21 | 15 | Sheet dimensions are not an exact multiple of frame size. |
| `QuartermasterSprite` | `quartermaster.png` | 256x64 | 48x56 | 5x1 | 5 | 4 | Sheet dimensions are not an exact multiple of frame size. |
| `HolodeckMonarchSprite` | `holodeck_monarch.png` | 1024x64 | 64x64 | 16x1 | 16 | 16 |  |
| `HolodeckLegionarySprite` | `holodeck_legionary.png` | 288x192 | 96x96 | 3x2 | 6 | 6 | Roman-style hard-light projection; 24x24 logical frames at 4x pixel scale. |
| `HologramSprite` | `hologram.png` | 512x64 | 56x60 | 9x1 | 9 | 8 | Sheet dimensions are not an exact multiple of frame size. |
| `HoodedRaiderCommanderSprite` | `outer_colony_scout.png` | 1024x128 | 48x60 | 21x2 | 42 | 11 | Shared sheet with `OuterColonyScoutSprite`. |
| `ImpSprite` | `npc2.png` | 288x64 | 48x56 | 6x1 | 6 | 5 | Sheet dimensions are not an exact multiple of frame size. |
| `JedaKnightSprite` | `jeda_knight.png` | 1024x128 | 60x56 | 17x2 | 34 | 17 | Sheet dimensions are not an exact multiple of frame size. |
| `LarvaSprite` | `larva.png` | 512x32 | 48x32 | 10x1 | 10 | 9 | Sheet dimensions are not an exact multiple of frame size. |
| `LeonardSprite` | `leonard.png` | 256x64 | 52x64 | 4x1 | 4 | 4 | Sheet dimensions are not an exact multiple of frame size. |
| `MakerBotSprite` | `maker_bot.png` | 128x64 | 56x56 | 2x1 | 2 | 1 | Sheet dimensions are not an exact multiple of frame size. |
| `MaskedPrisonerSprite` | `masked_prisoner.png` | 1024x64 | 56x64 | 18x1 | 18 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `NewbornElementalSprite` | `elemental.png` | 1024x128 | 48x56 | 21x2 | 42 | 14 | Sheet dimensions are not an exact multiple of frame size. |
| `OldWarBotSprite` | `old_war_bot.png` | 864x180 | 48x60 | 18x3 | 54 | 16 | Sheet dimensions are not an exact multiple of frame size. |
| `OuterColonyPsionSprite` | `outer_colony_psion.png` | 1024x64 | 48x60 | 21x1 | 21 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `OuterColonyScoutSprite` | `outer_colony_scout.png` | 1024x128 | 48x60 | 21x2 | 42 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `QueenXenoSprite` | `queen_xeno.png` | 512x256 | 64x68 | 8x3 | 24 | 7 | Sheet dimensions are not an exact multiple of frame size. |
| `ReplicatorSwarmSprite` | `replicator_swarm.png` | 1024x64 | 64x64 | 16x1 | 16 | 15 |  |
| `RotHeartSprite` | `rot_heart.png` | 512x64 | 64x64 | 8x1 | 8 | 8 |  |
| `RotLasherSprite` | `rot_lasher.png` | 512x64 | 48x64 | 10x1 | 10 | 7 | Sheet dimensions are not an exact multiple of frame size. |
| `RottingFistSprite` | `rotting_fist.png` | 512x128 | 96x68 | 5x1 | 5 | 5 | Sheet dimensions are not an exact multiple of frame size. |
| `RupturedCrewSuitSprite` | `ruptured_crew_suit.png` | 1024x64 | 48x60 | 21x1 | 21 | 17 | Sheet dimensions are not an exact multiple of frame size. |
| `ScorpioSprite` | `scorpio.png` | 1024x256 | 72x68 | 14x3 | 42 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `SeniorSprite` | `jeda_knight.png` | 1024x128 | 60x56 | 17x2 | 34 | 17 | Sheet dimensions are not an exact multiple of frame size. |
| `ShieldedShockTrooperSprite` | `outer_colony_shock_trooper.png` | 528x128 | 48x64 | 11x2 | 22 | 11 | Shared sheet with `OuterColonyShockTrooperSprite`. |
| `SignalLeechSprite` | `signal_leech.png` | 1024x64 | 48x60 | 21x1 | 21 | 11 | Sheet dimensions are not an exact multiple of frame size. |
| `SignalSirenSprite` | `signal_siren.png` | 1024x64 | 48x60 | 21x1 | 21 | 13 | Sheet dimensions are not an exact multiple of frame size. |
| `SiphonDroneSprite` | `siphon_drone.png` | 512x64 | 60x60 | 8x1 | 8 | 7 | Sheet dimensions are not an exact multiple of frame size. |
| `StationCatSprite` | `station_cat.png` | 320x64 | 40x64 | 8x1 | 8 | 8 |  |
| `SurvivorSprite` | `npc.png` | 416x192 | 48x56 | 8x3 | 24 | 4 | Sheet dimensions are not an exact multiple of frame size. |
| `JawarSprite` | `jawar.png` | 624x312 | 48x52 | 13x6 | 78 | 13 | Sheet dimensions are not an exact multiple of frame size. |
| `ToughXenoSprite` | `xenomorph.png` | 1024x256 | 64x60 | 16x4 | 64 | 15 | Sheet dimensions are not an exact multiple of frame size. |
| `TurretSprite` | `turret.png` | 512x64 | 56x60 | 9x1 | 9 | 8 | Sheet dimensions are not an exact multiple of frame size. |
| `YSprite` | `y.png` | 256x64 | 48x56 | 5x1 | 5 | 5 |  |
| `UndeadSprite` | `undead.png` | 1024x64 | 48x64 | 21x1 | 21 | 17 | Sheet dimensions are not an exact multiple of frame size. |
| `WarMachineSprite` | `war_machine.png` | 1024x64 | 64x64 | 16x1 | 16 | 14 |  |
| `WaterThingSprite` | `water_thing.png` | 1024x64 | 48x64 | 21x1 | 21 | 15 | Sheet dimensions are not an exact multiple of frame size. |
| `XenomorphSprite` | `xenomorph.png` | 1024x256 | 64x60 | 16x4 | 64 | 15 | Sheet dimensions are not an exact multiple of frame size. |
