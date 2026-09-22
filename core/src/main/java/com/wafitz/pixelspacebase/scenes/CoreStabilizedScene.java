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
package com.wafitz.pixelspacebase.scenes;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.ui.RedButton;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.Starfield;
import com.wafitz.pixelspacebase.ui.Window;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Music;

public class CoreStabilizedScene extends PixelScene {

    private static final int WIDTH = 128;
    private static final int BTN_HEIGHT = 18;
    private static final float GAP = 8;

    @Override
    public void create() {
        super.create();

        Music.INSTANCE.play(Assets.HAPPY, true);
        Music.INSTANCE.volume(PixelSpacebase.musicVol() / 10f);

        uiCamera.visible = false;

        int w = Camera.main.width;
        int h = Camera.main.height;

        Starfield starfield = new Starfield();
        starfield.setSize(w, h);
        add(starfield);

        RenderedText title = renderText(Messages.get(this, "title"), PixelSpacebase.landscape() ? 11 : 10);
        title.hardlight(Window.TITLE_COLOR);
        title.x = (w - title.width()) / 2f;
        title.y = PixelSpacebase.landscape() ? 12 : 22;
        align(title);
        add(title);

        Image override = new Image(Assets.ESCAPE_POD_OVERRIDE);
        add(override);

        RenderedTextMultiline text = renderMultiline(Messages.get(this, "text"), 6);
        text.maxWidth(WIDTH);
        text.hardlight(0xEAFDFF);
        add(text);

        RedButton gameOver = new RedButton(Messages.get(this, "exit")) {
            @Override
            protected void onClick() {
                Game.switchScene(RankingsScene.class);
            }
        };
        gameOver.setSize(WIDTH, BTN_HEIGHT);
        add(gameOver);

        float contentHeight = override.height + GAP + text.height() + GAP + gameOver.height();
        float y = (h - contentHeight) / 2f;

        override.x = (w - override.width) / 2f;
        override.y = y;
        align(override);

        text.setPos((w - text.width()) / 2f, override.y + override.height + GAP);
        align(text);

        gameOver.setPos((w - gameOver.width()) / 2f, text.top() + text.height() + GAP);

        fadeIn();
    }

    @Override
    protected void onBackPressed() {
    }
}
