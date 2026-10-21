# Shared teleport pads

The built-in image-generation tool produced two circular, transparent floor
overlays: orange for the previous deck and blue for the next deck. Neither uses
arrows or chevrons. The open centre and outside margin preserve each environment's
floor texture.

## Artwork

- Generated originals: `orange-source.png`, `blue-source.png`.
- Installed 64×64 overlays: `orange-pad.png`, `blue-pad.png`.
- Live two-frame strip: `core/src/main/assets/teleport_pads.png` (128×64).
- Exact prompts: `PROMPTS.md`.
- Artwork preview on six deck backgrounds: `deck-preview.png`.

Each 64×64 frame has a 56px footprint, retaining crisp detail on a 32×32 artwork
grid. The runtime retains the existing 16×16 logical tile size. Existing atlases
are unchanged.

Install and regenerate the preview from the repository root:

```sh
java -Djava.awt.headless=true scripts/InstallTeleportPadArtwork.java art/teleport-pads
```

## Runtime

`TeleporterPads` applies only to entrance, exit, locked-exit and unlocked-exit
terrain. Maintenance and its boss deck keep their ladders. Floors are selected
through the existing tile mapper before the rings are drawn, including Habitat
arrival-room flooring and both Holodeck phases. The Holodeck arrival overlay also
uses its floor beneath the new pads. Boss pads that appear, disappear or unlock
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

The installed artwork preview was inspected, and a debug APK build completed.
Phone playtesting of pad clicks, boss unlocks, both travel directions and the
Maintenance boundary remains pending. The artwork preview is not a gameplay
screenshot.
