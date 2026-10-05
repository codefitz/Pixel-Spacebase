package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.SpacebaseTilemap;
import com.wafitz.pixelspacebase.levels.DarkMazeLevel;
import com.wafitz.pixelspacebase.sprites.HeroSprite;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;

/** An exit beacon and the hero remain visible above the maze fog; no route is exposed. */
public class MazeNavigationMarkers extends Group {
    private final HeroSprite source;
    private final Image hero;

    public MazeNavigationMarkers(DarkMazeLevel maze, HeroSprite sprite) {
        source = sprite;
        float x = (maze.exit % maze.width()) * SpacebaseTilemap.SIZE;
        float y = (maze.exit / maze.width()) * SpacebaseTilemap.SIZE;
        block(x + 2, y + 2, 12, 1);
        block(x + 2, y + 13, 12, 1);
        block(x + 2, y + 2, 1, 12);
        block(x + 13, y + 2, 1, 12);
        block(x + 5, y + 7, 6, 2);
        block(x + 9, y + 5, 2, 6);
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
    }
}
