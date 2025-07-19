Pixel Spacebase
===============

This is a fork of Shattered Pixel Dungeon v4.3, based on Pixel Dungeon. I'm new to coding in a statically typed language like Java - this is primarily a hobby/learning project but I hope it might grow into a nice spin on the endless PD forks.

* Original by Watabou: https://github.com/watabou/pixel-dungeon
* Shattered by Evan D: https://github.com/00-Evan/shattered-pixel-dungeon

I started out with the original build of PD but ran into unending complications with the random level size generation. Since I decided that I wanted this to be a core feature (but also wanted to get on with my game changes), I opted for Shattered, but I'm not keen on the newer perspective, so forked the previous version with the original graphics.

Still, massive credit to Evan for reworking level generation - I stand on the shoulders of tiny pixel giants.

![alt tag](https://github.com/codefitz/Pixel-Spacebase/tree/master/app/src/main/assets/loader1.png)

## What's Different?

The aim is to convert Pixel Dungeon into a fully fledged Spacebase Exploration/Escape RPG.

### Major Changes:

* 4 new characters
* Smoother gameplay
* Item tweaks
* Level size generation completely random
* Workshop generation

### Changes in progress:

* Reskin/Names of all textures and sprites
* Game tweaks
* Bug fixing
* Cached auto-aim pathfinding to reduce lag

### Changes planned:

* Oxygen mechanics
* Armor Perma-buffs
* Modules and upgrades
* Story elements

## Development Setup

Run `scripts/setup.sh` to install the Android SDK and NDK and create the required `local.properties` file. The script assumes a Debian-based system with `apt` available.

### macOS

macOS users can run `scripts/setup-macos.sh` instead. It uses Homebrew to
install OpenJDK 8 and downloads the Android SDK and NDK to
`$HOME/android-sdk`. After running the script, build the project with:

```bash
./gradlew build
```

To install the debug build on an emulator or connected device, run:

```bash
./gradlew installDebug
```

