# Pixel Spacebase Tile Map Reference

This reference maps the current terrain IDs to plain-text descriptions for graphics planning. It is based on `Terrain.java`, `DungeonTilemap.java`, and the current tile names/descriptions in `levels.properties`.

## Terrain Tiles

| ID | Constant | Default visual frame | Generic visual brief | Gameplay flags / behavior |
| --- | --- | --- | --- | --- |
| 0 | `CHASM` | 0 | Open shaft, reactor drop, vacuum gap, or deep void. | Avoid + pit. Falling hazard. |
| 1 | `EMPTY` | 1 | Standard walkable floor/deck plate. | Passable. |
| 2 | `LIGHTEDVENT` | 2 | Active lit vent, moss, glowing floor plant, or powered floor growth depending on area. | Passable + flammable. |
| 3 | `EMPTY_WELL` | 3 | Powered-off terminal or empty special terminal pad. | Passable. |
| 4 | `WALL` | 4 | Standard wall/bulkhead/solid boundary. | Solid + blocks line of sight. |
| 5 | `DOOR` | 5 | Closed door or airlock-style door. | Passable, solid, blocks line of sight, flammable in inherited rules. |
| 6 | `OPEN_DOOR` | 6 | Open door/airlock panel. | Passable. |
| 7 | `ENTRANCE` | 7 | Up-depth entrance, hatch, lift, ladder, or ramp. | Passable. |
| 8 | `EXIT` | 8 | Down-depth exit, hatch, lift, ladder, or ramp. | Passable. |
| 9 | `EMBERS` | 9 | Scorched floor, embers, active residue, or hot reactor debris. | Passable. |
| 10 | `LOCKED_DOOR` | 10 | Locked security door or keyed airlock. | Solid + blocks line of sight. |
| 11 | `PEDESTAL` | 11 | Plinth, console stand, display base, ritual marker center, or item mount. | Passable. |
| 12 | `WALL_DECO` | 12 | Decorated wall variant: pipes, panels, ore vein, wiring, or damaged bulkhead. | Same as wall. |
| 13 | `BARRICADE` | 13 | Barricade, blocked passage, crate stack, or makeshift obstruction. | Solid + line-of-sight blocking + flammable. |
| 14 | `EMPTY_SP` | 14 | Special floor variant for rooms: carpet, clean deck, lab floor, bridge, or ritual floor. | Passable. |
| 15 | `OFFVENT` | 15 | Tall/off vent, growth, mushrooms, obstructing floor feature, or dense equipment. | Passable + line-of-sight blocking + flammable. |
| 16 | `SECRET_DOOR` | Wall frame | Hidden door disguised as wall. Reveals as `DOOR`. | Secret + wall behavior. |
| 17 | `HIDDEN_VENT` | Floor frame | Hidden vent disguised as normal floor. Reveals as `VENT`. | Secret + floor behavior. |
| 18 | `VENT` | Floor frame | Revealed active vent/pressure plate/trap mechanism. | Avoid. Triggers vent effect. |
| 19 | `INACTIVE_VENT` | Floor frame | Triggered or disabled vent. | Normal floor behavior. |
| 20 | `EMPTY_DECO` | 16 | Decorative floor stains, scrape marks, blood stains, missing panels, ritual marks. | Passable. |
| 21 | `LOCKED_EXIT` | 17 | Locked floor/depth exit, sealed hatch, blocked lift. | Solid. |
| 22 | `UNLOCKED_EXIT` | 18 | Opened boss/depth exit. | Passable. |
| 23 | `SIGN` | 19 | Sign, notice board, terminal notice, warning placard. | Passable + flammable. |
| 24 | `WELL` | 20 | Active terminal/special interaction tile. | Avoid. |
| 25 | `STATUE` | 21 | Old War Bot, statue, pillar, heavy machine, or fixed obstacle. | Solid. |
| 26 | `STATUE_SP` | 22 | Special statue/bot/pillar variant. | Solid. |
| 27 | `BOOKSHELF` | 23 | Bookshelf, archive rack, storage shelves, technical binders. | Same as barricade: solid, blocks line of sight, flammable. |
| 28 | `CRAFTING` | 24 | Fabricator/crafting terminal. | Passable; opens fabrication flow when available. |
| 63 | `WATER` | 63 | Water, coolant, flooded deck, cold lava, suspicious liquid by area. | Passable + liquid. |

## Area-Specific Text Hooks

| Area | Tile variations currently named in resources |
| --- | --- |
| Maintenance / Operations | Water is coolant. Decorative floors are dark stains and scrape marks. Shelves are technical binders and maintenance logs. |
| Prison / Security | Water is dark cold water. Decorative floors are old blood stains. Shelves are old prison-library remnants. |
| Caves / Lower Engineering | Lighted vent becomes fluorescent moss. Off vent becomes fluorescent mushrooms. Water is freezing cold water. Wall deco can be ore/parts. |
| City / Command-Habitation | Water is suspiciously colored. Off vent becomes high blooming flowers. Special floor can be carpet. Statues depict old war bots/dwarven figures. |
| Halls / Deep Containment | Water is cold lava. Lighted vent becomes embermoss. Off vent becomes emberfungi. Statues become skull pillars. |

## Generic ASCII Tile Legend

Use this compact legend for text mockups or tile-sheet planning:

```text
~  CHASM / reactor shaft / void
.  EMPTY floor
,  EMPTY_DECO floor detail
:  EMPTY_SP special floor
*  LIGHTEDVENT active lit vent/growth
"  OFFVENT tall vent/growth/obstruction
^  VENT revealed active vent
?  HIDDEN_VENT or SECRET_DOOR before discovery
_  INACTIVE_VENT
#  WALL
%  WALL_DECO
+  DOOR
/  OPEN_DOOR
!  LOCKED_DOOR
<  ENTRANCE
>  EXIT
X  LOCKED_EXIT
O  UNLOCKED_EXIT
=  EMBERS / hot residue
T  WELL / active terminal
t  EMPTY_WELL / powered-off terminal
F  CRAFTING / fabricator
S  SIGN
P  PEDESTAL
B  BARRICADE
R  BOOKSHELF / rack
M  STATUE / machine / pillar
W  WATER / coolant / liquid
```

## Example Room Sketch

```text
###########
#..R...S..#
#..###....#
#..#F#..W.#
#..###..W.#
#....^....#
#..P...>..#
###########
```

Interpretation:

- `#` bulkhead walls.
- `R` archive/storage rack.
- `S` warning sign or terminal notice.
- `F` fabricator.
- `W` coolant spill.
- `^` active vent hazard.
- `P` pedestal/display.
- `>` down-depth exit.

## Visual Notes for Future Tile Art

- The same terrain IDs are reused across areas, so new art should support palette/tileset swaps rather than new gameplay constants wherever possible.
- `SECRET_DOOR`, `HIDDEN_VENT`, `VENT`, and `INACTIVE_VENT` reuse ordinary floor/wall frames in `DungeonTilemap`; their visible state depends on discovery and overlay logic.
- Chasm and water have stitch behavior around neighboring tiles, so their edge frames need clean transitions.
- `EMPTY`, `EMPTY_SP`, and `EMPTY_DECO` should be visually distinct but low-noise, because they make up most walkable space.
- `OFFVENT` blocks line of sight despite being passable, so it should read as tall equipment, dense growth, or steam/vent columns rather than flat floor.
