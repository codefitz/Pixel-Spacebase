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
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.Rankings;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.ui.RedButton;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.Starfield;
import com.wafitz.pixelspacebase.ui.Window;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Sample;

import java.util.UUID;

public class WelcomeScene extends PixelScene {

    private static int LATEST_UPDATE = 10000;

    @Override
    public void create() {
        super.create();

        final int previousVersion = PixelSpacebase.version();

        if (PixelSpacebase.versionCode == previousVersion) {
            PixelSpacebase.switchNoFade(TitleScene.class);
            return;
        }

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

        DarkRedButton okay = new DarkRedButton(Messages.get(this, "continue")) {
            @Override
            protected void onClick() {
                super.onClick();
                updateVersion(previousVersion);
                PixelSpacebase.switchScene(TitleScene.class);
            }
        };

        if (previousVersion != 0) {
            DarkRedButton changes = new DarkRedButton(Messages.get(this, "changelist")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    updateVersion(previousVersion);
                    PixelSpacebase.switchScene(ChangesScene.class);
                }
            };
            okay.setRect(10, h - 20, (w / 2f) - 12, 16);
            okay.textColor(0xBBBB33);
            add(okay);

            changes.setRect(okay.right() + 4, h - 20, (w / 2f) - 12, 16);
            changes.textColor(0xBBBB33);
            add(changes);
        } else {
            okay.setRect(10, h - 20, w - 20, 16);
            okay.textColor(0xBBBB33);
            add(okay);
        }

        RenderedTextMultiline text = PixelScene.renderMultiline(6);
        String message;
        if (previousVersion == 0) {
            message = Messages.get(this, "welcome_msg");
        } else if (previousVersion <= PixelSpacebase.versionCode) {
            if (previousVersion < LATEST_UPDATE) {
                message = Messages.get(this, "update_intro");
                message += "\n\n" + Messages.get(this, "update_msg");
            } else {
                //TODO: change the messages here in accordance with the type of patch.
                message = Messages.get(this, "patch_intro");
                message += "\n\n" + Messages.get(this, "patch_bugfixes");
                message += "\n" + Messages.get(this, "patch_translations");
                message += "\n" + Messages.get(this, "patch_balance");

            }
        } else {
            message = Messages.get(this, "what_msg");
        }
        text.text(message, w - 20);
        float textSpace = h - title.y - (title.height() - 10) - okay.height() - 2;
        text.setPos((w - text.width()) / 2f, title.y + (title.height() - 10) + ((textSpace - text.height()) / 2));
        add(text);

    }

    private void updateVersion(int previousVersion) {
        //rankings conversion
        if (previousVersion <= 114) {
            Rankings.INSTANCE.load();
            for (Rankings.Record rec : Rankings.INSTANCE.records) {
                if (rec.gameFile != null) {
                    try {
                        SpacebaseRun.loadGame(rec.gameFile, false);
                        rec.gameID = rec.gameFile.replaceAll("\\D", "");

                        Rankings.INSTANCE.saveGameData(rec);
                    } catch (Exception e) {
                        rec.gameID = rec.gameFile.replaceAll("\\D", "");
                        rec.gameData = null;
                    }

                    String file = rec.gameFile;
                    rec.gameFile = "";
                    Game.instance.deleteFile(file);
                } else if (rec.gameID == null) {
                    rec.gameID = UUID.randomUUID().toString();
                }
            }
            Rankings.INSTANCE.save();
        }

        PixelSpacebase.version(PixelSpacebase.versionCode);
    }

    private class DarkRedButton extends RedButton {
        {
            bg.brightness(0.4f);
        }

        DarkRedButton(String text) {
            super(text);
        }

        @Override
        protected void onTouchDown() {
            bg.brightness(0.5f);
            Sample.INSTANCE.play(Assets.SND_CLICK);
        }

        @Override
        protected void onTouchUp() {
            super.onTouchUp();
            bg.brightness(0.4f);
        }
    }
}
