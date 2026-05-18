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
package com.wafitz.pixelspacebase.mechanics;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.utils.BArray;

public final class ShadowCaster {

    private static final int MAX_DISTANCE = 8;

    private static boolean[] falseArray;

    private static int[][] rounding;

    static {
        rounding = new int[MAX_DISTANCE + 1][];
        for (int i = 1; i <= MAX_DISTANCE; i++) {
            rounding[i] = new int[i + 1];
            for (int j = 1; j <= i; j++) {
                rounding[i][j] = (int) Math.min(j, Math.round(i * Math.cos(Math.asin(j / (i + 0.5)))));
            }
        }
    }

    public static void castShadow(int x, int y, boolean[] fieldOfView, int distance) {

        BArray.setFalse(fieldOfView);

        fieldOfView[y * SpacebaseRun.level.width() + x] = true;

        boolean[] losBlocking = Level.losBlocking;
        Obstacles obs = new Obstacles();

        scanSector(distance, fieldOfView, losBlocking, obs, x, y, +1, +1, 0, 0);
        scanSector(distance, fieldOfView, losBlocking, obs, x, y, -1, +1, 0, 0);
        scanSector(distance, fieldOfView, losBlocking, obs, x, y, +1, -1, 0, 0);
        scanSector(distance, fieldOfView, losBlocking, obs, x, y, -1, -1, 0, 0);
        scanSector(distance, fieldOfView, losBlocking, obs, x, y, 0, 0, +1, +1);
        scanSector(distance, fieldOfView, losBlocking, obs, x, y, 0, 0, -1, +1);
        scanSector(distance, fieldOfView, losBlocking, obs, x, y, 0, 0, +1, -1);
        scanSector(distance, fieldOfView, losBlocking, obs, x, y, 0, 0, -1, -1);

    }

    //FIXME This is is the primary performance bottleneck for game logic, need to optimize or rewrite
    private static void scanSector(int distance, boolean[] fieldOfView, boolean[] losBlocking, Obstacles obs, int cx, int cy, int m1, int m2, int m3, int m4) {

        obs.reset();

        final int width = SpacebaseRun.level.width();
        final int height = SpacebaseRun.level.height();
        final int[] roundingDistance = rounding[distance];
        final int stepPos = m4 * width + m1;

        int rowXBase = cx + m3;
        int rowYBase = cy + m2;
        int rowPosBase = rowYBase * width;
        final int rowStride = m2 * width;

        for (int p = 1; p <= distance; p++, rowXBase += m3, rowYBase += m2, rowPosBase += rowStride) {

            final float invP = 1f / p;
            final float dq2 = 0.5f * invP;

            final int pp = roundingDistance[p];
            
            float a0 = 0f;
            int x = rowXBase;
            int y = rowYBase;
            int pos = rowPosBase + rowXBase;

            for (int q = 0; q <= pp; q++) {

                if (y >= 0 && y < height && x >= 0 && x < width) {

                    float a1 = a0 - dq2;
                    float a2 = a0 + dq2;

                    if (!(obs.isBlocked(a0) && obs.isBlocked(a1) && obs.isBlocked(a2))) {
                        fieldOfView[pos] = true;
                    }

                    if (losBlocking[pos]) {
                        obs.add(a1, a2);
                    }

                }

                a0 += invP;
                x += m1;
                y += m4;
                pos += stepPos;
            }

            obs.nextRow();
        }
    }

    private static final class Obstacles {

        private static int SIZE = (MAX_DISTANCE + 1) * (MAX_DISTANCE + 1) / 2;
        private float[] a1 = new float[SIZE];
        private float[] a2 = new float[SIZE];

        private int length;
        private int limit;

        public void reset() {
            length = 0;
            limit = 0;
        }

        public void add(float o1, float o2) {

            if (length > limit && o1 <= a2[length - 1]) {

                // Merging several blocking cells
                a2[length - 1] = o2;

            } else {

                a1[length] = o1;
                a2[length++] = o2;

            }

        }

        public boolean isBlocked(float a) {
            for (int i = 0; i < limit; i++) {
                if (a >= a1[i] && a <= a2[i]) {
                    return true;
                }
            }
            return false;
        }

        public void nextRow() {
            limit = length;
        }
    }
}
