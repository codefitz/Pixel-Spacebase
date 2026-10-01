# Signal Siren floating alien

Created using the built-in image-generation tool. Installed asset: `core/src/main/assets/signal_siren.png`, 240x180 RGBA. `scripts/PrepareSignalSirenSprites.java` fits a generated 5x3 sheet to 12x15 logical frames at exact 4x scale, validates nonempty artwork and transparency in all thirteen used frames, and produces `docs/signal-siren-preview.png`. Original artwork is preserved in `docs/sprite-backups/signal-siren-before-floating-alien.png`.

Frames 0–2 idle bob, 3–8 drifting, 9–11 attack pulse, 12 collapse; 13–14 unused. Class names, asset paths and save aliases are retained. Newly created and restored Sirens fly. Existing blink approach, stats, loot, sleep immunity and cat scare-away state are unchanged.

Successful hits have a one-in-three chance to teleport a surviving hero within their current room, replacing Hypnotise. Room interior candidates include plasma and chasms but exclude walls, occupied cells, room borders and the hero's current cell. No room means no teleport. Damage resolves before displacement; the existing teleport helper calls Hero.move(), which handles terrain pressure exactly once. Flight protects against falling; plasma uses existing hazard/suit protections. Hoverpods block the hit and its teleport effect, including the hit that breaks the pod. No room-spanning reroute or global teleport is added.

## Generation prompt

Use case: stylized-concept. Asset: tiny pixel-art alien enemy animation sheet for Pixel Spacebase. Subject: floating signal siren, a small alien jellyfish with a rounded indigo/violet bell, glowing cyan central eye, three short curling tendrils beneath, bright magenta signal rim. Nonhumanoid, no legs, clothing, wings or weapons. Crisp chunky pixel clusters, designed for 12x15 logical pixels per frame, clear silhouette and high contrast cyan highlights. Transparent background. EXACT 5 columns by 3 rows of equal cells, zero gutters and margins. Every complete creature fits within its cell with one logical pixel clear border, identical scale and front/slight-right viewpoint. Row-major indices 0 idle, 1 idle bob up, 2 idle bob down, 3 drift tendrils left, 4 drift tendrils straight, 5 drift tendrils right, 6 drift recovery, 7 drift curl, 8 drift extension, 9 bell charges magenta, 10 eye flashes bright cyan teleport pulse, 11 pulse recovery, 12 collapsing dark bell with short tendrils, last two cells 13 and14 completely empty. No external particle effects crossing cells, no grid lines, labels, text, shadows, floor, background, watermark or other entities. Whole-sheet aspect ratio60:45. Simple readable small-scale game sprites rather than detailed concept art.

## Verification

Targeted tests check room bounds, plasma/chasm eligibility, occupied-cell exclusion, no-room fallback and the PNG frame grid. In-game animation, teleport/fall transitions and cat interaction require playthrough verification.
