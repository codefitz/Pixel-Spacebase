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
import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.RegularLevel;
import com.wafitz.pixelspacebase.levels.painters.Workshop;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.ui.GameLog;
import com.wafitz.pixelspacebase.windows.WndError;
import com.wafitz.pixelspacebase.windows.WndStory;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;

import java.io.FileNotFoundException;
import java.io.IOException;

public class InterlevelScene extends PixelScene {

    private static final float TIME_TO_FADE = 0.3f;

    public enum Mode {
        DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL, RESET, NONE
    }

    public static Mode mode;

    public static int returnDepth;
    public static int returnPos;

    public static boolean noStory = false;

    public static boolean fallIntoPit;

    private enum Phase {
        FADE_IN, STATIC, FADE_OUT
    }

    private Phase phase;
    private float timeLeft;

    private RenderedText message;

    private Thread thread;
    private Exception error = null;
    private float waitingTime;

    @Override
    public void create() {
        super.create();

        String text = Messages.get(Mode.class, mode.name());

        message = PixelScene.renderText(text, 9);
        message.x = (Camera.main.width - message.width()) / 2;
        message.y = (Camera.main.height - message.height()) / 2;
        align(message);
        add(message);

        phase = Phase.FADE_IN;
        timeLeft = TIME_TO_FADE;

        thread = new Thread() {
            @Override
            public void run() {

                try {

                    Generator.reset();

                    switch (mode) {
                        case DESCEND:
                            descend();
                            break;
                        case ASCEND:
                            ascend();
                            break;
                        case CONTINUE:
                            restore();
                            break;
                        case RESURRECT:
                            resurrect();
                            break;
                        case RETURN:
                            returnTo();
                            break;
                        case FALL:
                            fall();
                            break;
                        case RESET:
                            reset();
                            break;
                    }

                    if ((SpacebaseRun.depth % 5) == 0) {
                        Sample.INSTANCE.load(Assets.SND_BOSS);
                    }

                } catch (Exception e) {

                    error = e;

                }

                if (phase == Phase.STATIC && error == null) {
                    phase = Phase.FADE_OUT;
                    timeLeft = TIME_TO_FADE;
                }
            }
        };
        thread.start();
        waitingTime = 0f;
    }

    @Override
    public void update() {
        super.update();

        waitingTime += Game.elapsed;

        float p = timeLeft / TIME_TO_FADE;

        switch (phase) {

            case FADE_IN:
                message.alpha(1 - p);
                if ((timeLeft -= Game.elapsed) <= 0) {
                    if (!thread.isAlive() && error == null) {
                        phase = Phase.FADE_OUT;
                        timeLeft = TIME_TO_FADE;
                    } else {
                        phase = Phase.STATIC;
                    }
                }
                break;

            case FADE_OUT:
                message.alpha(p);

                if (mode == Mode.CONTINUE || (mode == Mode.DESCEND && SpacebaseRun.depth == 1)) {
                    Music.INSTANCE.volume(p * (PixelSpacebase.musicVol() / 10f));
                }
                if ((timeLeft -= Game.elapsed) <= 0) {
                    Game.switchScene(GameScene.class);
                }
                break;

            case STATIC:
                if (error != null) {
                    String errorMsg;
                    if (error instanceof FileNotFoundException)
                        errorMsg = Messages.get(this, "file_not_found");
                    else if (error instanceof IOException)
                        errorMsg = Messages.get(this, "io_error");

                    else
                        throw new RuntimeException("fatal error occured while moving between floors", error);

                    add(new WndError(errorMsg) {
                        public void onBackPressed() {
                            super.onBackPressed();
                            Game.switchScene(StartScene.class);
                        }
                    });
                    error = null;
                } else if ((int) waitingTime == 10) {
                    waitingTime = 11f;
                    PixelSpacebase.reportException(
                            new RuntimeException("waited more than 10 seconds on levelgen. Device:" + SpacebaseRun.seed + " depth:" + SpacebaseRun.depth)
                    );
                }
                break;
        }
    }

    private void descend() throws IOException {

        Actor.fixTime();
        boolean nextLevelNeedsPit = levelHasWeakFloor(SpacebaseRun.level);
        if (SpacebaseRun.hero == null) {
            SpacebaseRun.init();
            if (noStory) {
                SpacebaseRun.chapters.add(WndStory.ID_OPERATIONS);
                noStory = false;
            }
            GameLog.wipe();
        } else {
            Workshop.carryStockFrom(SpacebaseRun.level);
            SpacebaseRun.saveAll();
        }

        Level level;
        if (SpacebaseRun.depth >= Statistics.deepestFloor) {
            RegularLevel.weakFloorCreated = nextLevelNeedsPit;
            level = SpacebaseRun.newLevel();
        } else {
            SpacebaseRun.depth++;
            level = SpacebaseRun.loadLevel(SpacebaseRun.hero.heroClass);
            Workshop.deliverStorageTo(level);
        }
        SpacebaseRun.switchLevel(level, level.entrance);
    }

    private void fall() throws IOException {

        Actor.fixTime();
        int targetDepth = SpacebaseRun.depth + 1;
        boolean nextLevelNeedsPit = fallIntoPit || levelHasWeakFloor(SpacebaseRun.level);
        Workshop.carryStockFrom(SpacebaseRun.level);
        SpacebaseRun.saveAll();

        Level level;
        if (targetDepth > Statistics.deepestFloor) {
            RegularLevel.weakFloorCreated = nextLevelNeedsPit;
            level = SpacebaseRun.newLevel();
        } else {
            SpacebaseRun.depth = targetDepth;
            level = SpacebaseRun.loadLevel(SpacebaseRun.hero.heroClass);
            Workshop.deliverStorageTo(level);
        }
        SpacebaseRun.switchLevel(level, fallIntoPit ? level.pitCell() : level.randomRespawnCell());
    }

    private void ascend() throws IOException {
        Actor.fixTime();

        Workshop.carryStockFrom(SpacebaseRun.level);
        SpacebaseRun.saveAll();
        SpacebaseRun.depth--;
        Level level = SpacebaseRun.loadLevel(SpacebaseRun.hero.heroClass);
        Workshop.deliverStorageTo(level);
        SpacebaseRun.switchLevel(level, level.exit);
    }

    private void returnTo() throws IOException {

        Actor.fixTime();

        Workshop.carryStockFrom(SpacebaseRun.level);
        SpacebaseRun.saveAll();
        SpacebaseRun.depth = returnDepth;
        Level level = SpacebaseRun.loadLevel(SpacebaseRun.hero.heroClass);
        Workshop.deliverStorageTo(level);
        SpacebaseRun.switchLevel(level, returnPos);
    }

    private void restore() throws IOException {

        Actor.fixTime();

        GameLog.wipe();

        SpacebaseRun.loadGame(StartScene.curClass);
        if (SpacebaseRun.depth == -1) {
            SpacebaseRun.depth = Statistics.deepestFloor;
            SpacebaseRun.switchLevel(SpacebaseRun.loadLevel(StartScene.curClass), -1);
        } else {
            Level level = SpacebaseRun.loadLevel(StartScene.curClass);
            SpacebaseRun.switchLevel(level, SpacebaseRun.hero.pos);
        }
    }

    private void resurrect() throws IOException {

        Actor.fixTime();

        if (SpacebaseRun.level.locked) {
            SpacebaseRun.hero.resurrect(SpacebaseRun.depth);
            SpacebaseRun.depth--;
            Level level = SpacebaseRun.newLevel();
            SpacebaseRun.switchLevel(level, level.entrance);
        } else {
            SpacebaseRun.hero.resurrect(-1);
            SpacebaseRun.resetLevel();
        }
    }

    private void reset() throws IOException {

        Actor.fixTime();

        SpacebaseRun.depth--;
        RegularLevel.weakFloorCreated = false;
        Level level = SpacebaseRun.newLevel();
        SpacebaseRun.switchLevel(level, level.entrance);
    }

    private boolean levelHasWeakFloor(Level level) {
        return level instanceof RegularLevel && ((RegularLevel) level).hasWeakFloor();
    }

    @Override
    protected void onBackPressed() {
        //Do nothing
    }
}
