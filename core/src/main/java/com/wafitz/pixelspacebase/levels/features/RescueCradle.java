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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.wafitz.pixelspacebase.levels.features;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.particles.SparkParticle;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndMessage;
import com.wafitz.pixelspacebase.windows.WndOptions;
import com.watabou.noosa.audio.Sample;

public class RescueCradle {

    private static final int[] DEPTHS = new int[]{1, 6, 11, 16, 21};

    public static boolean isAvailableHere() {
        return Statistics.amuletObtained && isRescueDepth(Dungeon.depth);
    }

    public static void read(final int pos) {
        if (isOpened(Dungeon.depth)) {
            GameScene.show(new WndMessage(Messages.get(RescueCradle.class, "already_open",
                    rescuedCount(), totalCradles())));
            return;
        }

        GameScene.show(new WndOptions(
                Messages.get(RescueCradle.class, "title"),
                Messages.get(RescueCradle.class, "prompt", deckName()),
                Messages.get(RescueCradle.class, "open"),
                Messages.get(RescueCradle.class, "leave")) {

            @Override
            protected void onSelect(int index) {
                if (index == 0) {
                    open(pos);
                }
            }
        });
    }

    public static int rescuedCount() {
        int count = 0;
        for (int depth : DEPTHS) {
            if (isOpened(depth)) count++;
        }
        return count;
    }

    public static int totalCradles() {
        return DEPTHS.length;
    }

    private static void open(int pos) {
        Statistics.rescueCradleDepths |= mask(Dungeon.depth);

        Sample.INSTANCE.play(Assets.SND_UNLOCK);
        CellEmitter.get(pos).burst(SparkParticle.FACTORY, 8);

        int rescued = rescuedCount();
        GLog.p(Messages.get(RescueCradle.class, "log", rescued, totalCradles()));

        String key = rescued == totalCradles() ? "opened_final" : "opened";
        GameScene.show(new WndMessage(Messages.get(RescueCradle.class, key, rescued, totalCradles())));
    }

    private static boolean isRescueDepth(int depth) {
        for (int rescueDepth : DEPTHS) {
            if (rescueDepth == depth) return true;
        }
        return false;
    }

    private static boolean isOpened(int depth) {
        return (Statistics.rescueCradleDepths & mask(depth)) != 0;
    }

    private static int mask(int depth) {
        return 1 << depth;
    }

    private static String deckName() {
        return Messages.get(RescueCradle.class, "deck_" + Dungeon.depth);
    }
}
