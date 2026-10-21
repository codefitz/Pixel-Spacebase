# DM3000's integrated launcher arm

The original plain launcher icon is replaced by an articulated gold/brass and gunmetal forearm with an elbow pivot, hydraulic wrist, reinforced closed fist and recessed rocket muzzle behind the knuckles. The fist communicates the item's existing melee role; the built-in launcher communicates its ranged role.

The built-in image-generation tool created `launcher-arm-source.png` from the prompt in `PROMPT.md`, using the DM3000 armour preview as a palette reference. `before.png` preserves the old icon. `launcher-arm-icon.png` is the packed 64x64 inventory icon; `preview.png` compares old and new, including a native-size copy.

Regenerate from the repository root:

```sh
java -Djava.awt.headless=true scripts/InstallDM3000LauncherArm.java
```

The installer fits the source within a transparent 14x14-game-unit footprint using crisp 2x2 physical-pixel clusters, matching the new DM3000 suits. It replaces only item slot 101 (`ItemSpriteSheet.DM3000_ROCKET_LAUNCHER`) in `core/src/main/assets/items.png` and verifies all other pixels. The full input atlas is backed up as `docs/sprite-backups/items-before-dm3000-launcher-arm.png`, retaining the preceding armour work.

The launcher particle emitter now sits at (9.5, 3.5) game units, near the forearm muzzle. Loaded-blaster names retain "Launcher Arm", and item/class descriptions explain punching and ranged fire. The `DM3000Launcher` class, item slot, saved blaster key, damage, charging, upgrades and actions retain their existing behaviour.

Visual inspection covers the packed icon and preview. The focused Hunter artwork test, Java compilation and debug APK build passed. In-game icon and particle alignment confirmation remains pending.
