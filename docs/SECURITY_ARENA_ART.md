# Security boss alien-planet arena

The arena keeps its collision map, boss behavior, and door interactions. During `FIGHT_ARENA` it switches from the Security Block tile atlas to `core/src/main/assets/tiles_security_arena.png`, then switches back for the ending. The former orange surface overlay is removed, including when loading a save that still contains one.

The arena atlas changes only the terrain frames needed by the fight:

| Terrain | Atlas frames | Visual |
| --- | --- | --- |
| Open ground | 1, 38, 47 | Red-violet sand |
| Solid rock | 4, 40 | Jagged plum rock |
| Rock detail | 12, 42 | Striated boulder |
| Closed passage | 5 | Rockfall blocking the path |
| Open passage | 6 | Clear sand path between rocks |

Standalone 64 x 64 tiles, a contact sheet, and a whole-arena layout preview are in `art/security-arena/`.

The five tile designs were made with built-in image generation. Prompts requested a top-down early-television-style alien landscape with muted red-purple sand, dark rocky outcrops, a blocked stone passage, and a matching open passage. They explicitly excluded prison metal, lava, water, text, borders, gradients and blur. The generated images were reduced to 64 x 64 using nearest-neighbour sampling; alternate ground and rock frames were mirrored from their matching design.
