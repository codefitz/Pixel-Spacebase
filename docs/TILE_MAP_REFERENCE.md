# Pixel Spacebase Tile Map Reference

This reference maps the current terrain IDs to plain-text descriptions for graphics planning. It is based on `Terrain.java`, `DungeonTilemap.java`, and the current tile names/descriptions in `levels.properties`.

## Terrain Tiles

| ID | Constant | Default visual frame | Generic visual brief | Gameplay flags / behavior |
| --- | --- | --- | --- | --- |
| 0 | `CHASM` | 0 | Open shaft, reactor drop, vacuum gap, or deep void. | Avoid + pit. Falling hazard. |
| 1 | `EMPTY` | 1 | Standard walkable floor/deck plate. | Passable. |
| 2 | `LIGHTEDVENT` | 2 | Active floor lighting, moss, glowing floor plant, or powered floor growth depending on area. | Passable + flammable. |
| 3 | `EMPTY_WELL` | 3 | Powered-off terminal or empty special terminal pad. | Passable. |
| 4 | `WALL` | 4 | Standard wall/bulkhead/solid boundary. | Solid + blocks line of sight. |
| 5 | `DOOR` | 5 | Closed door or airlock-style door. | Passable, solid, blocks line of sight, flammable in inherited rules. |
| 6 | `OPEN_DOOR` | 6 | Open door/airlock panel. | Passable. |
| 7 | `ENTRANCE` | 8 | Previous-deck entrance, hatch, lift, ladder, or ramp. | Passable. |
| 8 | `EXIT` | 7 | Next-deck exit, hatch, lift, ladder, or ramp. | Passable. |
| 9 | `EMBERS` | 9 | Plasma residue, scorched floor, active residue, or hot reactor debris. | Passable. |
| 10 | `LOCKED_DOOR` | 10 | Locked security door or keyed airlock. | Solid + blocks line of sight. |
| 11 | `PEDESTAL` | 11 | Plinth, console stand, display base, ritual marker center, or item mount. | Passable. |
| 12 | `WALL_DECO` | 12 | Decorated wall variant: pipes, panels, ore vein, wiring, or damaged bulkhead. | Same as wall. |
| 13 | `BARRICADE` | 13 | Barricade, blocked passage, crate stack, or makeshift obstruction. | Solid + line-of-sight blocking + flammable. |
| 14 | `EMPTY_SP` | 14 | Special floor variant for rooms: carpet, clean deck, lab floor, bridge, or ritual floor. | Passable. |
| 15 | `OFFVENT` | 15 | Unpowered floor lighting, growth, mushrooms, obstructing floor feature, or dense equipment. | Passable + line-of-sight blocking + flammable. |
| 16 | `SECRET_DOOR` | Wall frame | Hidden door disguised as wall. Reveals as `DOOR`. | Secret + wall behavior. |
| 17 | `HIDDEN_VENT` | Floor frame | Hidden floor lighting trigger disguised as normal floor. Reveals as `VENT`. | Secret + floor behavior. |
| 18 | `VENT` | Floor frame | Revealed active floor lighting/pressure plate/trap mechanism. | Avoid. Triggers floor lighting effect. |
| 19 | `INACTIVE_VENT` | Floor frame | Spent or disabled floor lighting. | Normal floor behavior. |
| 20 | `EMPTY_DECO` | 16 | Decorative floor stains, scrape marks, blood stains, missing panels, ritual marks. | Passable. |
| 21 | `LOCKED_EXIT` | 17 | Locked floor/depth exit, sealed hatch, blocked lift. | Solid. |
| 22 | `UNLOCKED_EXIT` | 18 | Opened boss/depth exit. | Passable. |
| 23 | `SIGN` | 19 | Terminal, notice board, warning display, or wall console. | Passable + flammable. |
| 24 | `WELL` | 20 | Active terminal/special interaction tile. | Avoid. |
| 25 | `STATUE` | 21 | Old War Bot, statue, pillar, heavy machine, or fixed obstacle. | Solid. |
| 26 | `STATUE_SP` | 22 | Special statue/bot/pillar variant. | Solid. |
| 27 | `BOOKSHELF` | 23 | Bookshelf, archive rack, storage shelves, technical binders. | Same as barricade: solid, blocks line of sight, flammable. |
| 28 | `CRAFTING` | 24 | Fabricator/crafting terminal. | Passable; opens fabrication flow when available. |
| 63 | `WATER` | 63 | Water, coolant, flooded deck, cold lava, suspicious liquid by area. | Passable + liquid. |

## Area-Specific Text Hooks

| Area | Tile variations currently named in resources |
| --- | --- |
| Maintenance / Operations | Water is maintenance coolant. Decorative floors are dark stains and scrape marks. Shelves are technical binders and maintenance logs. |
| Security Block | Water is security runoff from detention washdown systems. Decorative floors have old blood and scuffed restraint marks. Shelves are security binders, intake records, and confiscation logs. |
| Lower Engineering | Tiles use a colder steel/teal palette with amber service markings. Floor lighting can still appear as fluorescent conduit growth. Off floor lighting becomes dormant conduit growth. Water is freezing coolant. Wall deco can be ore/parts. |
| Habitation / Command Sector | Water is recycled civic fountain water. Off floor lighting becomes dormant atrium planters. Special floor is command-suite carpet. Statues depict old war bots and training-sim figures. |
| Deep Containment | Water is containment fluid. Floor lighting can appear as bioluminescent growth. Off floor lighting becomes dormant spore columns. Statues become sealed specimen pillars. |

## Generic ASCII Tile Legend

Use this compact legend for text mockups or tile-sheet planning:

```text
~  CHASM / reactor shaft / void
.  EMPTY floor
,  EMPTY_DECO floor detail
:  EMPTY_SP special floor
*  LIGHTEDVENT active floor lighting/growth
"  OFFVENT unpowered floor lighting/growth/obstruction
^  VENT revealed active floor lighting trigger
?  HIDDEN_VENT or SECRET_DOOR before discovery
_  INACTIVE_VENT spent floor lighting
#  WALL
%  WALL_DECO
+  DOOR
/  OPEN_DOOR
!  LOCKED_DOOR
<  ENTRANCE / previous-deck route
>  EXIT / next-deck route
X  LOCKED_EXIT
O  UNLOCKED_EXIT
=  EMBERS / plasma residue
T  WELL / active terminal
t  EMPTY_WELL / powered-off terminal
F  CRAFTING / fabricator
S  SIGN / terminal
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
- `S` warning terminal or notice display.
- `F` fabricator.
- `W` coolant spill.
- `^` active floor lighting hazard.
- `P` pedestal/display.
- `>` down-depth exit.

## Visual Notes for Future Tile Art

- The same terrain IDs are reused across areas, so new art should support palette/tileset swaps rather than new gameplay constants wherever possible.
- `SECRET_DOOR`, `HIDDEN_VENT`, `VENT`, and `INACTIVE_VENT` reuse ordinary floor/wall frames in `DungeonTilemap`; their visible state depends on discovery and overlay logic.
- Chasm and water have stitch behavior around neighboring tiles, so their edge frames need clean transitions.
- `EMPTY`, `EMPTY_SP`, and `EMPTY_DECO` should be visually distinct but low-noise, because they make up most walkable space.
- `OFFVENT` blocks line of sight despite being passable, so it should read as tall equipment, dense growth, or unpowered floor-light columns rather than flat floor.
