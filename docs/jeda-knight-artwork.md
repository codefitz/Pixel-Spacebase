# Jeda Knight artwork

The Knight now resembles a disciplined space knight: uncovered head with brown
hair, a visible human face, cream/tan robes, brown belt and boots, and a blue
energy blade. The Bith Acolyte supplied the compact robed-fighter silhouette
reference. The Master shares these robes with a green blade.

The built-in imagegen tool generated `docs/jeda-knight-source.png`. The installed
asset is `core/src/main/assets/jeda_knight.png`: two rows of seventeen 64×64
frames, Knight first and Master second. Both sprite classes now read 16×16
logical frames, preserving their existing animation indices and timings.
Poses include idle, walking, sword strikes, kicks, kneeling, falling and a final
prone body. The original sheet is preserved in
`docs/sprite-backups/jeda-knight-before-unhooded-reskin.png`.

`docs/jeda-knight-preview.png` shows idle, slash, kick and prone poses for both
variants. In-game appearance remains unconfirmed.

## Installation

```sh
java scripts/InstallJedaKnightArtwork.java docs/jeda-knight-source.png
```

## Generation prompt

Replace the current Knight artwork using the Bith Acolyte's compact upright
robed-fighter silhouette and chunky pixel-art proportions. Create an original
Jedi-like human space knight with no hood, short brown hair, readable calm eyes
and face, cream/tan layered robes, brown belt and boots, and a blue/cyan energy
sword with a white-blue core and grey hilt. Calm heroic appearance: no red eyes,
skull, black hood, red blade or intimidating black armor. Crisp low-resolution
pixel art for 16×16 logical frames, maximum 14×14 footprint, transparent margin,
consistent baseline, facing slightly right. Six columns and three rows:
three idles, two slash poses, two kick poses, kneeling/falling/collapsed/prone
death poses, six walking poses and an extra idle reference. The death animation
shows the actual Knight falling and remaining on the floor. Genuine alpha
transparency, no background, floor, shadows, labels, text or watermark.
