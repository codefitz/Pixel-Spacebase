# Shared teleport pads

The pads use orange for the previous deck and blue for the next deck. Both now
have opaque dark-metal centres with subtle matching colour reflections and
concentric panel detail. Their original illuminated rims are preserved exactly;
the outside margin remains transparent so the deck floor shows around each pad.
Neither uses arrows or chevrons.

## Artwork

- Original generated rings: `orange-source.png`, `blue-source.png`.
- Filled-centre reference from built-in imagegen: `filled-centres-source.png`.
- Installed 64×64 pads: `orange-pad.png`, `blue-pad.png`.
- Live two-frame strip: `core/src/main/assets/teleport_pads.png` (128×64).
- Exact prompts: `PROMPTS.md`.
- Artwork preview on six deck backgrounds: `deck-preview.png`.

Each 64×64 frame has a 56px footprint, retaining crisp detail on a 32×32 artwork
grid. The runtime retains the existing 16×16 logical tile size. The installer
fits the generated reference, then fills only the original ring's enclosed
transparent centre. Every original rim and exterior pixel is retained, rather
than replacing the rings with a newly generated design. Deck atlases are unchanged.

Install and regenerate the preview from the repository root:

```sh
java -Djava.awt.headless=true scripts/InstallTeleportPadArtwork.java art/teleport-pads
```

## Runtime

`TeleporterPads` applies only to entrance, exit, locked-exit and unlocked-exit
terrain. Maintenance and its boss deck keep their ladders. Floors are selected
through the existing tile mapper before the pads are drawn, including Habitat
arrival-room flooring and both Holodeck phases. Their flooring remains visible
outside the circular pad. Boss pads that appear, disappear or unlock
are refreshed with map changes. Locked pads are dimmed; their movement and keycard
rules remain intact.

Ordinary pad travel runs a 0.45s beam over the hero, a 0.3s coloured signal wash,
animated loading with concentric rings and photon streaks, then a 0.45s arrival
wash and reconstruction beam. The loading animation remains active while the
existing level worker runs. Travel departing a Maintenance ladder uses the
original loading flow; arrival on Maintenance uses the original fade.

Fall, load, resurrection and Y's scripted rescue transitions retain their existing
flows. Clicking an actual exit on a rescue side level still returns to its origin
through the existing travel code; its information panel describes that return.
An automatic maze return remains automatic. The alien planet's Y interaction
remains the required return route.

The graphics, effects and travel marker are transient. No saved terrain, floor
connections, item/actor positions, level-generation rules or class identities are
changed, so the new treatment also applies to existing saves.

## Evidence

The filled pads were inspected on Security, Engineering, Habitat, Command and both
Holodeck floor textures. Pixel checks confirmed that all 1,216 orange and 1,096
blue centre pixels are now opaque, while every original rim and exterior pixel
is unchanged. Both frames retain binary alpha and the 2× artwork grid, and the
live strip matches the fitted pads exactly. Debug and minified release APK builds
passed with the filled strip packaged.

Phone playtesting of pad clicks, boss unlocks, both travel directions and the
Maintenance boundary remains pending, including visual confirmation of the new
centres. The artwork preview is not a gameplay screenshot. The fill change does
not alter travel, lock/dimming, pulse, beam or loading-effect code.
