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
import com.wafitz.pixelspacebase.ui.ChangesButton;
import com.wafitz.pixelspacebase.ui.ExitButton;
import com.wafitz.pixelspacebase.ui.LanguageButton;
import com.wafitz.pixelspacebase.ui.PrefsButton;
import com.wafitz.pixelspacebase.ui.Starfield;
import com.wafitz.pixelspacebase.ui.Window;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

public class TitleScene extends PixelScene {

    @Override
    public void create() {

        super.create();

        Music.INSTANCE.play(Assets.THEME, true);
        Music.INSTANCE.volume(PixelSpacebase.musicVol() / 10f);

        uiCamera.visible = false;

        int w = Camera.main.width;
        int h = Camera.main.height;

        Starfield starfield = new Starfield();
        starfield.setSize(w, h);
        add(starfield);

        float topRegion = Math.max(95f, h * 0.45f);

        RenderedText title = renderText("PIXEL SPACEBASE", PixelSpacebase.landscape() ? 17 : 15);
        title.hardlight(Window.TITLE_COLOR);
        title.x = (w - title.width()) / 2f;
        title.y = PixelSpacebase.landscape() ? 18 : 28;
        align(title);
        add(title);

        RenderedText subtitle = renderText("FORWARD THROUGH THE VOID", 7);
        subtitle.hardlight(0x66E6FF);
        subtitle.x = (w - subtitle.width()) / 2f;
        subtitle.y = title.y + title.height() + 5;
        align(subtitle);
        add(subtitle);

        DashboardItem btnBadges = new DashboardItem(Messages.get(this, "badges"), 3) {
            @Override
            protected void onClick() {
                PixelSpacebase.switchNoFade(BadgesScene.class);
            }
        };
        add(btnBadges);

        DashboardItem btnAbout = new DashboardItem(Messages.get(this, "about"), 1) {
            @Override
            protected void onClick() {
                PixelSpacebase.switchNoFade(AboutScene.class);
            }
        };
        add(btnAbout);

        DashboardItem btnPlay = new DashboardItem(Messages.get(this, "play"), 0) {
            @Override
            protected void onClick() {
                PixelSpacebase.switchNoFade(StartScene.class);
            }
        };
        add(btnPlay);

        DashboardItem btnRankings = new DashboardItem(Messages.get(this, "rankings"), 2) {
            @Override
            protected void onClick() {
                PixelSpacebase.switchNoFade(RankingsScene.class);
            }
        };
        add(btnRankings);

        if (PixelSpacebase.landscape()) {
            btnRankings.setPos(w / 2 - btnRankings.width(), topRegion);
            btnBadges.setPos(w / 2, topRegion);
            btnPlay.setPos(btnRankings.left() - btnPlay.width(), topRegion);
            btnAbout.setPos(btnBadges.right(), topRegion);
        } else {
            btnPlay.setPos(w / 2 - btnPlay.width(), topRegion);
            btnRankings.setPos(w / 2, btnPlay.top());
            btnBadges.setPos(w / 2 - btnBadges.width(), btnPlay.top() + DashboardItem.SIZE);
            btnAbout.setPos(w / 2, btnBadges.top());
        }

        BitmapText version = new BitmapText("v " + Game.version + "", pixelFont);
        version.measure();
        version.hardlight(0xCCCCCC);
        version.x = w - version.width();
        version.y = h - version.height();
        add(version);

        Button changes = new ChangesButton();
        changes.setPos(w - changes.width(), h - version.height() - changes.height());
        add(changes);

        PrefsButton btnPrefs = new PrefsButton();
        btnPrefs.setPos(0, 0);
        add(btnPrefs);

        LanguageButton btnLang = new LanguageButton();
        btnLang.setPos(16, 1);
        add(btnLang);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(w - btnExit.width(), 0);
        add(btnExit);

        fadeIn();
    }

    private static class DashboardItem extends Button {

        public static final float SIZE = 48;

        private static final int IMAGE_SIZE = 32;

        private Image image;
        private RenderedText label;

        public DashboardItem(String text, int index) {
            super();

            image.frame(image.texture.uvRect(index * IMAGE_SIZE, 0, (index + 1) * IMAGE_SIZE, IMAGE_SIZE));
            this.label.text(text);

            setSize(SIZE, SIZE);
        }

        @Override
        protected void createChildren() {
            super.createChildren();

            image = new Image(Assets.DASHBOARD);
            add(image);

            label = renderText(9);
            add(label);
        }

        @Override
        protected void layout() {
            super.layout();

            image.x = x + (width - image.width()) / 2;
            image.y = y;
            align(image);

            label.x = x + (width - label.width()) / 2;
            label.y = image.y + image.height() + 2;
            align(label);
        }

        @Override
        protected void onTouchDown() {
            image.brightness(1.5f);
            Sample.INSTANCE.play(Assets.SND_CLICK, 1, 1, 0.8f);
        }

        @Override
        protected void onTouchUp() {
            image.resetColor();
        }
    }
}
