# Assets archived during v1.0.6

This directory preserves 18 unused files moved out of `core/src/main/assets/` on 21 October 2026. The files total 39,698,787 bytes (37.86 MiB) before APK compression. Their relative paths are retained, so each file can be restored to the original asset directory without renaming it. This folder is outside the Android asset source sets and is not packaged in the APK.

[manifest.json](manifest.json) records the original and archived paths, byte sizes, SHA-256 checksums and reason for every move. File contents were preserved exactly.

## Archived files

| Former asset path | Evidence for archiving |
| --- | --- |
| `custom_tiles/holodeck_halls.png` | Unused former Holodeck custom sheet; Assets.HOLODECK_RETAINER_TILES has no caller. |
| `exp_bar.png` | Superseded HUD strip; StatusPane draws the current experience bar directly and Assets.XP_BAR has no caller. |
| `hp_bar.png` | Superseded HUD strip; StatusPane draws the current HP bar directly and Assets.HP_BAR has no caller. |
| `items_old.png` | Unreferenced backup of the item atlas; runtime uses items.png. |
| `shield_bar.png` | Superseded HUD strip; no caller of Assets.SHLD_BAR. |
| `soundtrack/air_supply.mp4` | Not selected by Assets or any runtime/music loader; no artwork-tool reference. |
| `soundtrack/commons_in_ruin.mp4` | Not selected by Assets or any runtime/music loader; no artwork-tool reference. |
| `soundtrack/game.ogg` | Not selected by Assets or any runtime/music loader; no artwork-tool reference. |
| `soundtrack/habitation_deck_echoes.mp4` | Not selected by Assets or any runtime/music loader; no artwork-tool reference. |
| `soundtrack/iron_gate_slam.mp4` | Not selected by Assets or any runtime/music loader; no artwork-tool reference. |
| `soundtrack/sector_9_breach.mp4` | Not selected by Assets or any runtime/music loader; no artwork-tool reference. |
| `soundtrack/sleeping_sample.mp4` | Not selected by Assets or any runtime/music loader; no artwork-tool reference. |
| `soundtrack/surface.ogg` | Not selected by Assets or any runtime/music loader; no artwork-tool reference. |
| `soundtrack/terminal_override.mp4` | Not selected by Assets or any runtime/music loader; no artwork-tool reference. |
| `soundtrack/theme.ogg` | Not selected by Assets or any runtime/music loader; no artwork-tool reference. |
| `terrain_features_old.png` | Unreferenced backup of the terrain feature atlas; runtime uses terrain_features.png. |
| `tiles_decorators.png` | Unreferenced decorator sheet; no runtime or artwork-tool filename reference. |
| `xeno_queen_festive.png` | Unreferenced alternate queen sheet; current queen sprite uses queen_xeno.png. |

## Audit scope

The audit checked the game and engine Java sources, `Assets` constant callers, the texture/bitmap/font/audio loaders, Gradle asset source sets, tests, artwork installers and tracked documentation. Texture loading uses explicit asset names; the Pixel Dungeon rescue also builds names from `Assets.PD_ART`, so its entire active directory is retained. The four unused HUD/custom-tile constants were removed from `Assets.java` alongside their files.

Standalone icons and animation strips that are still used by artwork installers or tests remain in their current locations. This includes the detector icons, hard-light emitter, Hoverpod and Hunter source copies, Loader icons/SVG, boarding-weapon icons and plasmid injector strip. They are authoring/test inputs or outputs rather than obsolete artwork. The detector bio icon also remains available for the unresolved assignment in `PLAN.md`.

`asset_pixel_scale.txt` is retained because `UpscalePngAssets.java` uses it to prevent a second destructive upscale of the existing sheets. No saved class names, live atlas pixels or runtime asset paths were changed.

## Restoring a file

Copy a file back to `core/src/main/assets/` using its relative path in this archive. If the file previously had an `Assets` constant, restore that declaration and add its intended runtime caller as part of reactivating it. Keep the archived original available for reference.

## Validation

`./gradlew :core:assembleDebug :core:assembleRelease --offline` passed. Both APKs contain all 200 retained asset files byte for byte as before, exclude the 18 archived files, and do not package this archive. All 178 remaining `Assets` constants resolve to existing files or directories. Archived-file SHA-256 checks passed.

The unsigned release APK decreased from 146,626,503 to 106,925,130 bytes, a reduction of 37.86 MiB (27.08%). Gameplay was not repeated for this change; these are source, archive-integrity and packaged-asset checks.
