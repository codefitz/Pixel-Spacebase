/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.wafitz.pixelspacebase.ui;

import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

public class Starfield extends Component {

    private static final int STAR_COUNT = 72;
    private static final float SPEED = 0.65f;

    private Star[] stars;
    private final java.util.Random random = new java.util.Random(0x5A7F13L);

    @Override
    protected void createChildren() {
        stars = new Star[STAR_COUNT];
        for (int i = 0; i < STAR_COUNT; i++) {
            stars[i] = new Star();
            stars[i].visual = new ColorBlock(1, 1, 0xFFD8FBFF);
            add(stars[i].visual);
        }
    }

    @Override
    protected void layout() {
        for (Star star : stars) {
            reset(star, random.nextFloat());
        }
    }

    @Override
    public void update() {
        super.update();

        float cx = width / 2f;
        float cy = height / 2f;
        float span = Math.max(width, height);

        for (Star star : stars) {
            star.z -= Game.elapsed * SPEED;
            if (star.z <= 0.04f) {
                reset(star, 1f);
            }

            float depth = 1f / star.z;
            float x = cx + star.dx * depth * span;
            float y = cy + star.dy * depth * span;

            if (x < -4 || x > width + 4 || y < -4 || y > height + 4) {
                reset(star, 1f);
                continue;
            }

            float size = Math.max(0.5f, 2.2f * (1f - star.z));
            star.visual.x = x;
            star.visual.y = y;
            star.visual.size(size, size);
            star.visual.alpha(0.35f + 0.65f * (1f - star.z));
        }
    }

    private void reset(Star star, float z) {
        float angle = random.nextFloat() * 6.2831855f;
        float radius = 0.035f + random.nextFloat() * 0.42f;
        star.dx = (float) Math.cos(angle) * radius;
        star.dy = (float) Math.sin(angle) * radius;
        star.z = Math.max(0.05f, z);
    }

    private static class Star {
        float dx;
        float dy;
        float z;
        ColorBlock visual;
    }
}
