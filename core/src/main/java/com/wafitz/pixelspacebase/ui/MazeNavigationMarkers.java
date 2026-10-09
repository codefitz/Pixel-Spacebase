package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.SpacebaseTilemap;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.buffs.Light;
import com.wafitz.pixelspacebase.items.armor.HoverPod;
import com.wafitz.pixelspacebase.levels.DarkMazeLevel;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.sprites.HeroSprite;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Game;

/** An exit beacon and the hero remain visible above the maze fog; no route is exposed. */
public class MazeNavigationMarkers extends Group {
    private final DarkMazeLevel maze;
    private final HeroSprite source;
    private final Image hero;
    private final ColorBlock[] nearbyFloors = new ColorBlock[9];
    private final ColorBlock[] nearbyWalls = new ColorBlock[9];
    private final ColorBlock[] position = new ColorBlock[4];
    private int lastPos = -1;
    private float pulse;

    public MazeNavigationMarkers(DarkMazeLevel maze, HeroSprite sprite) {
        this.maze = maze;
        source = sprite;
        for (int i = 0; i < nearbyFloors.length; i++) {
            nearbyFloors[i] = new ColorBlock(14, 14, 0xFF54585D);
            nearbyWalls[i] = new ColorBlock(10, 10, 0xFF9B9FA3);
            nearbyFloors[i].visible = nearbyWalls[i].visible = false;
            add(nearbyFloors[i]);
            add(nearbyWalls[i]);
        }
        float x = (maze.exit % maze.width()) * SpacebaseTilemap.SIZE;
        float y = (maze.exit / maze.width()) * SpacebaseTilemap.SIZE;
        block(x + 2, y + 2, 12, 1);
        block(x + 2, y + 13, 12, 1);
        block(x + 2, y + 2, 1, 12);
        block(x + 13, y + 2, 1, 12);
        block(x + 5, y + 7, 6, 2);
        block(x + 9, y + 5, 2, 6);
        for (int i = 0; i < position.length; i++) {
            position[i] = new ColorBlock(i < 2 ? 10 : 1, i < 2 ? 1 : 10, 0xFFFFDD70);
            add(position[i]);
        }
        hero = new Image(sprite); add(hero);
    }

    private void block(float x, float y, float width, float height) {
        ColorBlock block = new ColorBlock(width, height, 0xFF72FFE7);
        block.x = x; block.y = y; add(block);
    }

    @Override public void update() {
        super.update();
        hero.flipHorizontal = source.flipHorizontal; hero.flipVertical = source.flipVertical;
        hero.copy(source);
        hero.x = source.x; hero.y = source.y;
        hero.scale.set(source.scale); hero.origin.set(source.origin);
        hero.angle = source.angle;
        hero.visible = source.alive;
        int cell = SpacebaseRun.hero.pos;
        boolean showNearby = SpacebaseRun.hero.buff(Light.class) == null
                && !HoverPod.torchActive(SpacebaseRun.hero) && SpacebaseRun.hero.isAlive();
        int cx = cell % maze.width(), cy = cell / maze.width();
        for (int i = 0; i < nearbyFloors.length; i++) {
            int tx = cx + i % 3 - 1, ty = cy + i / 3 - 1;
            boolean inside = tx >= 0 && tx < maze.width() && ty >= 0 && ty < maze.height();
            nearbyFloors[i].visible = showNearby && inside;
            nearbyWalls[i].visible = showNearby && inside && maze.map[ty * maze.width() + tx] == Terrain.WALL;
            nearbyFloors[i].x = tx * SpacebaseTilemap.SIZE + 1;
            nearbyFloors[i].y = ty * SpacebaseTilemap.SIZE + 1;
            nearbyWalls[i].x = tx * SpacebaseTilemap.SIZE + 3;
            nearbyWalls[i].y = ty * SpacebaseTilemap.SIZE + 3;
        }
        if (lastPos != cell) { lastPos = cell; pulse = .35f; }
        pulse = Math.max(0, pulse - Game.elapsed);
        float x = source.x + source.width() / 2 - 5;
        float y = source.y + source.height() - 8;
        for (int i = 0; i < position.length; i++) {
            position[i].x = x + (i == 3 ? 9 : 0);
            position[i].y = y + (i == 1 ? 9 : 0);
            position[i].alpha(.55f + .45f * pulse / .35f);
            position[i].visible = source.alive;
        }
    }
}
