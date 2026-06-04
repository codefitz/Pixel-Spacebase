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
package com.wafitz.pixelspacebase.windows;

import com.wafitz.pixelspacebase.Chrome;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.Window;
import com.watabou.input.Touchscreen.Touch;
import com.watabou.noosa.Game;
import com.watabou.noosa.TouchArea;
import com.watabou.utils.SparseArray;

public class WndStory extends Window {

    private static final int WIDTH_P = 132;
    private static final int WIDTH_L = 156;
    private static final int MARGIN = 10;

    private static final float bgR = 0.77f;
    private static final float bgG = 0.73f;
    private static final float bgB = 0.62f;

    public static final int ID_OPERATIONS = 0;
    public static final int ID_SECURITY_BLOCK = 1;
    public static final int ID_LOWER_ENGINEERING = 2;
    public static final int ID_HABITATION_COMMAND = 3;
    public static final int ID_DEEP_CONTAINMENT = 4;

    private static final SparseArray<String> CHAPTERS = new SparseArray<>();

    static {
        CHAPTERS.put(ID_OPERATIONS, "operations");
        CHAPTERS.put(ID_SECURITY_BLOCK, "security_block");
        CHAPTERS.put(ID_LOWER_ENGINEERING, "lower_engineering");
        CHAPTERS.put(ID_HABITATION_COMMAND, "habitation_command");
        CHAPTERS.put(ID_DEEP_CONTAINMENT, "deep_containment");
    }

    private RenderedTextMultiline tf;

    private float delay;

    protected WndStory(String text) {
        super(0, 0, Chrome.get(Chrome.Type.SCRIPT));

        int width = PixelSpacebase.landscape() ? WIDTH_L : WIDTH_P;

        tf = PixelScene.renderMultiline(text, 6);
        tf.maxWidth(width - MARGIN * 2);
        tf.hardlight(0xEAFDFF);
        tf.setPos(MARGIN, MARGIN);
        add(tf);

        add(new TouchArea(chrome) {
            @Override
            protected void onClick(Touch touch) {
                hide();
            }
        });

        resize(width, (int) Math.min(tf.height() + MARGIN * 2, 180));
    }

    @Override
    public void update() {
        super.update();

        if (delay > 0 && (delay -= Game.elapsed) <= 0) {
            shadow.visible = chrome.visible = tf.visible = true;
        }
    }

    public static void showChapter(int id) {

        if (SpacebaseRun.chapters.contains(id)) {
            return;
        }

        String text = Messages.get(WndStory.class, CHAPTERS.get(id));
        if (text != null) {
            WndStory wnd = new WndStory(text);
            if ((wnd.delay = 0.6f) > 0) {
                wnd.shadow.visible = wnd.chrome.visible = wnd.tf.visible = false;
            }

            Game.scene().add(wnd);

            SpacebaseRun.chapters.add(id);
        }
    }
}
