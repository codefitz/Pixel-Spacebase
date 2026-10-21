# v1.0.5 release preparation

Staging targets the repository's default branch, `Pixel-Spacebase`. The application version is **1.0.5**, with Android version code **10005**. The welcome screen treats upgrades from 1.0.4 as a feature update, and the in-game changelog starts with v1.0.5 while preserving older entries.

## Normal gameplay restored

- New heroes start with 10 strength.
- Damage, defence, death, alive-state checks and failed-run rankings follow their normal paths; the development invulnerability flag and health-restoration helpers are removed.
- The first-floor test chest and the Darksaber/Brightsaber/random-blaster debug starting inventory are removed.
- Normal class equipment, genuine shield/revival abilities and the staged Y-rescue mechanics are retained.
- Existing save data is preserved. Saves created while testing retain their stored strength and inventory; use a new run to check the normal starting loadout. There is no automatic inventory deletion or strength migration.

## Validation

- All **96** JVM tests passed with no failures, errors or skips.
- The Hoverpod artwork test now checks the dedicated 21-by-80px animation strips.
- Eclipse Saber tests provide JVM-only preferences and logging; the rescue persistence fixture handles the saved integer arrays, and the callback check verifies delegation to the saved journey. These test adapters are not part of the app.
- Standalone class-armour checks passed for all 84 frames, fine detail, exact Shapeshifter artwork and legacy-art preservation.
- Debug and release APK assembly, including release shrinking and vital lint, passed.
- Packaged metadata was inspected: package `com.wafitz.pixelspacebase`, version name `1.0.5`, version code `10005`.
- No development invulnerability, starter chest or test-loadout references remain in application source.

Commands:

```sh
./gradlew :core:testDebugUnitTest :core:assembleDebug :core:assembleRelease --offline --no-watch-fs
java -Djava.awt.headless=true scripts/VerifyClassArmourArtwork.java
```

Disabling Gradle filesystem watching forces a fresh scan on the workspace volume.

## Artifacts and remaining release work

- `core/build/outputs/apk/debug/core-debug.apk` is signed with the debug key and can be installed for checks.
- `core/build/outputs/apk/release/core-release-unsigned.apk` is the unsigned release artifact; release signing and publication remain separate steps.
- Device/playthrough checks marked pending in `PLAN.md`, including rescue travel, saved-game transitions and new armour animation, remain unverified by this preparation.
- This preparation does not merge Staging into the default branch or create a tagged release.
