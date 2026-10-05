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
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.YRescueJourney;
import com.wafitz.pixelspacebase.actors.mobs.npcs.YRescuer;
import com.wafitz.pixelspacebase.actors.mobs.npcs.StationCat;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.levels.Level;
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
import com.watabou.utils.Random;

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
    public static boolean returnAtEntrance;
    public static boolean rescueScatter;

    public static boolean noStory = false;

    public static boolean fallIntoDoorlessRoom;

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
                    RuntimeException timeout = new RuntimeException(
                            "waited more than 10 seconds on levelgen. Device:" + SpacebaseRun.seed + " depth:" + SpacebaseRun.depth);
                    // Report the generation worker's stack, not this loading-screen update.
                    timeout.setStackTrace(thread.getStackTrace());
                    PixelSpacebase.reportException(timeout);
                }
                break;
        }
    }

    private void descend() throws IOException {
        if (returnFromSideLevel()) return;

        Actor.fixTime();
        if (SpacebaseRun.hero == null) {
            SpacebaseRun.init();
            if (noStory) {
                SpacebaseRun.chapters.add(WndStory.ID_OPERATIONS);
                noStory = false;
            }
            GameLog.wipe();
        } else {
            Workshop.carryStockFrom(SpacebaseRun.level);
            StationCat.carryFollowerFrom(SpacebaseRun.level);
            SpacebaseRun.saveAll();
        }

        Level level;
        if (SpacebaseRun.depth >= Statistics.deepestFloor) {
            level = SpacebaseRun.newLevel();
        } else {
            SpacebaseRun.depth = SpacebaseRun.nextDepth(SpacebaseRun.depth);
            level = SpacebaseRun.loadOrCreateLevel(SpacebaseRun.depth);
            Workshop.deliverStorageTo(level);
        }
        SpacebaseRun.switchLevel(level, level.entrance);
    }

    private void fall() throws IOException {
        if (returnFromSideLevel()) return;

        Actor.fixTime();
        int targetDepth = SpacebaseRun.fallTargetDepth();
        Buff.affect(SpacebaseRun.hero, YRescueJourney.class).recordFall(SpacebaseRun.depth, SpacebaseRun.hero.pos);
        Workshop.carryStockFrom(SpacebaseRun.level);
        StationCat.carryFollowerFrom(SpacebaseRun.level);
        SpacebaseRun.saveAll();

        Level level;
        if (targetDepth > Statistics.deepestFloor) {
            level = SpacebaseRun.newLevel();
        } else {
            SpacebaseRun.depth = targetDepth;
            level = SpacebaseRun.loadOrCreateLevel(targetDepth);
            Workshop.deliverStorageTo(level);
        }
        int landingCell = level.fallLandingCell(fallIntoDoorlessRoom);
        SpacebaseRun.switchLevel(level, landingCell);
        fallIntoDoorlessRoom = false;
    }

    private void ascend() throws IOException {
        if (returnFromSideLevel()) return;
        Actor.fixTime();

        Workshop.carryStockFrom(SpacebaseRun.level);
        StationCat.carryFollowerFrom(SpacebaseRun.level);
        SpacebaseRun.saveAll();
        SpacebaseRun.depth = SpacebaseRun.previousDepth(SpacebaseRun.depth);
        Level level = SpacebaseRun.loadOrCreateLevel(SpacebaseRun.depth);
        Workshop.deliverStorageTo(level);
        SpacebaseRun.switchLevel(level, level.exit);
    }

    private boolean returnFromSideLevel() throws IOException {
        if (!SpacebaseRun.isRescueSideLevel() || SpacebaseRun.hero == null) return false;
        YRescueJourney ticket = SpacebaseRun.hero.buff(YRescueJourney.class);
        if (ticket == null) throw new IOException("Side journey has no return ticket");
        ticket.prepareReturn();
        returnTo();
        return true;
    }

    private void returnTo() throws IOException {

        Actor.fixTime();

        YRescueJourney ticket = SpacebaseRun.hero.buff(YRescueJourney.class);
        if (SpacebaseRun.isRescueSideLevel() && ticket != null && !ticket.pendingTravel()) ticket.prepareReturn();
        boolean journeyTravel = ticket != null && ticket.pendingTravel();
        boolean toPlanet = journeyTravel && ticket.phase == YRescueJourney.Phase.DEPARTING
                && ticket.destination == YRescueJourney.Destination.ALIEN_PLANET;
        boolean toBoss = journeyTravel && ticket.phase == YRescueJourney.Phase.DEPARTING
                && ticket.destination == YRescueJourney.Destination.BOSS;
        boolean toMaze = journeyTravel && ticket.phase == YRescueJourney.Phase.DEPARTING
                && ticket.destination == YRescueJourney.Destination.DARK_MAZE;
        boolean toDungeon = journeyTravel && ticket.phase == YRescueJourney.Phase.DEPARTING
                && ticket.destination == YRescueJourney.Destination.PIXEL_DUNGEON;

        if (journeyTravel) ticket.configureTravel();
        if (!journeyTravel || !ticket.transferPrepared) {
            Workshop.carryStockFrom(SpacebaseRun.level);
            StationCat.carryFollowerFrom(SpacebaseRun.level);
            if (journeyTravel && ticket.destination == YRescueJourney.Destination.BOSS
                    && ticket.phase == YRescueJourney.Phase.RETURNING) ticket.commitBossArena();
            if (journeyTravel) ticket.transferPrepared = true;
            SpacebaseRun.saveAll();
        }
        Level level;
        int landing;
        if (journeyTravel && ticket.arrivalLevel != null) {
            Actor.clear();
            SpacebaseRun.depth = returnDepth;
            SpacebaseRun.selectRescueLevel(toPlanet || toBoss || toMaze || toDungeon ? ticket.destinationIdentity : "");
            level = ticket.arrivalLevel;
            landing = ticket.landingPos;
        } else {
            SpacebaseRun.depth = returnDepth;
            level = toPlanet || toBoss || toMaze || toDungeon ? SpacebaseRun.loadOrCreateRescueLevel(ticket.destinationIdentity)
                    : SpacebaseRun.loadOrCreateLevel(returnDepth);
            if (toBoss) ticket.captureBossBaseline(level);
            landing = toBoss ? level.rescueBossLandingCell() : toPlanet || toMaze || toDungeon ? level.entrance : rescueScatter ? YRescuer.randomReachableCell(level, -1)
                    : returnAtEntrance ? YRescuer.safeReturnCell(level, ticket == null ? -1 : ticket.sourcePos) : returnPos;
            if (landing < 0 && journeyTravel) throw new IOException("No usable Y rescue landing cell");
            if (journeyTravel) {
                ticket.arrivalLevel = level;
                ticket.landingPos = landing;
                // Save an untouched destination before moving cargo or followers into it.
                SpacebaseRun.saveJourneyCheckpoint();
            }
        }
        Workshop.deliverStorageTo(level);
        SpacebaseRun.switchLevel(level, landing);
        returnAtEntrance = false;
        rescueScatter = false;
    }

    private void restore() throws IOException {

        Actor.fixTime();

        GameLog.wipe();

        SpacebaseRun.loadGame(StartScene.curClass);
        YRescueJourney ticket = SpacebaseRun.hero.buff(YRescueJourney.class);
        if (ticket != null && ticket.pendingTravel()) {
            // Do not carry cargo twice when resuming a prepared departure/return.
            if (ticket.arrivalLevel == null) SpacebaseRun.level = SpacebaseRun.loadLevel(StartScene.curClass);
            returnTo();
            return;
        }
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
            SpacebaseRun.depth = SpacebaseRun.previousDepth(SpacebaseRun.depth);
            Level level = SpacebaseRun.newLevel();
            SpacebaseRun.switchLevel(level, level.entrance);
        } else {
            SpacebaseRun.hero.resurrect(-1);
            SpacebaseRun.resetLevel();
        }
    }

    private void reset() throws IOException {
        if (returnFromSideLevel()) return;

        Actor.fixTime();

        SpacebaseRun.depth = SpacebaseRun.previousDepth(SpacebaseRun.depth);
        Level level = SpacebaseRun.newLevel();
        SpacebaseRun.switchLevel(level, level.entrance);
    }

    @Override
    protected void onBackPressed() {
        //Do nothing
    }
}
