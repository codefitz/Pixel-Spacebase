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
package com.wafitz.pixelspacebase.actors.mobs.npcs;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.Journal;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.mobs.HolodeckMonarch;
import com.wafitz.pixelspacebase.actors.mobs.HolodeckLegionary;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.modules.Module;
import com.wafitz.pixelspacebase.items.quest.HardLightEmitter;
import com.wafitz.pixelspacebase.levels.HabitationRingLevel;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.YSprite;
import com.wafitz.pixelspacebase.windows.WndY;
import com.wafitz.pixelspacebase.windows.WndQuest;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.wafitz.pixelspacebase.utils.GLog;

public class Y extends NPC {

    private static final int REQUIRED_EMITTERS = 6;

    {
        spriteClass = YSprite.class;

        properties.add(Property.IMMOVABLE);
    }

    private boolean seenBefore = false;

    @Override
    protected boolean act() {

        if (!Quest.given && SpacebaseRun.visible[pos]) {
            if (!seenBefore) {
                yell(Messages.get(this, "hey", SpacebaseRun.hero.givenName()));
            }
            seenBefore = true;
        } else {
            seenBefore = false;
        }

        throwItem();

        return super.act();
    }

    @Override
    public int defenseSkill(Char enemy) {
        return 1000;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public void add(Buff buff) {
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean interact() {

        sprite.turnTo(pos, SpacebaseRun.hero.pos);
        if (Quest.given) {

            HardLightEmitter tokens = SpacebaseRun.hero.belongings.getItem(HardLightEmitter.class);
            if (tokens != null && tokens.quantity() >= REQUIRED_EMITTERS) {
                GameScene.show(new WndY(this, tokens));
            } else {
                tell(Messages.get(this, "golems_2", SpacebaseRun.hero.givenName()));
            }

        } else {
            tell(Messages.get(this, "golems_1", SpacebaseRun.hero.givenName()));
            Quest.given = true;
            Quest.completed = false;

            Journal.add(Journal.Feature.ARP);
        }

        return false;
    }

    private void tell(String text) {
        GameScene.show(
                new WndQuest(this, text));
    }

    public void flee() {

        yell(Messages.get(this, "cya", SpacebaseRun.hero.givenName()));

        destroy();
        sprite.die();
    }

    public static class Quest {

        private static boolean alternative;

        private static boolean spawned;
        private static boolean given;
        private static boolean completed;
        private static boolean holodeckPoweredDown;
        private static boolean secretWorkshopUnlocked;
        private static boolean secretWorkshopStateSaved;

        public static Module reward;

        public static void reset() {
            alternative = false;
            spawned = false;
            given = false;
            completed = false;
            holodeckPoweredDown = false;
            secretWorkshopUnlocked = false;
            secretWorkshopStateSaved = false;

            reward = null;
        }

        private static final String NODE = "anomaly";

        private static final String ALTERNATIVE = "alternative";
        private static final String SPAWNED = "spawned";
        private static final String GIVEN = "given";
        private static final String COMPLETED = "completed";
        private static final String POWERED_DOWN = "holodeckPoweredDown";
        private static final String SECRET_WORKSHOP = "secretWorkshopUnlocked";
        private static final String REWARD = "reward";

        public static void storeInBundle(Bundle bundle) {

            Bundle node = new Bundle();

            node.put(SPAWNED, spawned);
            node.put(POWERED_DOWN, holodeckPoweredDown);
            node.put(SECRET_WORKSHOP, secretWorkshopUnlocked);

            if (spawned) {
                node.put(ALTERNATIVE, alternative);

                node.put(GIVEN, given);
                node.put(COMPLETED, completed);
                node.put(REWARD, reward);
            }

            bundle.put(NODE, node);
        }

        public static void restoreFromBundle(Bundle bundle) {

            Bundle node = bundle.getBundle(NODE);

            reset();
            if (!node.isNull()) {
                spawned = node.getBoolean(SPAWNED);
                holodeckPoweredDown = node.getBoolean(POWERED_DOWN);
                secretWorkshopStateSaved = node.contains(SECRET_WORKSHOP);
                secretWorkshopUnlocked = node.getBoolean(SECRET_WORKSHOP);
                if (spawned) {
                    alternative = node.getBoolean(ALTERNATIVE);
                    given = node.getBoolean(GIVEN);
                    completed = node.getBoolean(COMPLETED);
                    reward = (Module) node.get(REWARD);
                }
            }
        }

        public static boolean isHolodeckPoweredDown() {
            return holodeckPoweredDown;
        }

        public static boolean hasSecretWorkshopAccess() {
            return secretWorkshopUnlocked;
        }

        /** Called after the hero is restored, to keep old saves consistent. */
        public static void reconcileHolodeckState(Hero hero, int depth, int deepestFloor) {
            if (hero != null) {
                HardLightEmitter emitters = hero.belongings.getItem(HardLightEmitter.class);
                if (emitters != null && emitters.quantity() >= REQUIRED_EMITTERS) {
                    holodeckPoweredDown = true;
                    if (!secretWorkshopStateSaved && depth < 20) {
                        secretWorkshopUnlocked = true;
                    }
                }
            }
            // Older saves always generated floor 21. Preserve access if it was
            // reached already, or if shutdown happened before this rule existed.
            if (!secretWorkshopStateSaved && (deepestFloor >= 21
                    || depth <= 20 && holodeckPoweredDown)) {
                secretWorkshopUnlocked = true;
            }
            secretWorkshopStateSaved = true;
        }

        public static void onEmitterAcquired(Hero hero) {
            if (hero != null) {
                HardLightEmitter emitters = hero.belongings.getItem(HardLightEmitter.class);
                if (emitters != null && emitters.quantity() >= REQUIRED_EMITTERS) {
                    if (SpacebaseRun.depth < 20) secretWorkshopUnlocked = true;
                    if (holodeckPoweredDown) return;
                    holodeckPoweredDown = true;
                    discardProjections(SpacebaseRun.level);
                    if (SpacebaseRun.level != null) {
                        GameScene.resetMap();
                        GameScene.resetCustomTiles();
                    }
                    GLog.p(Messages.get(Y.class, "holodeck_shutdown"));
                }
            }
        }

        /** Removes stored holograms without treating shutdown as killing them. */
        public static void discardProjections(Level level) {
            if (level == null || level.mobs == null) return;
            for (Mob mob : level.mobs.toArray(new Mob[0])) {
                if (mob instanceof HolodeckLegionary) {
                    level.mobs.remove(mob);
                    Actor.remove(mob);
                    if (mob.sprite != null) mob.sprite.killAndErase();
                }
            }
        }

        public static void spawn(HabitationRingLevel level) {
            if (!spawned && SpacebaseRun.depth > 16 && Random.Int(20 - SpacebaseRun.depth) == 0) {

                Y npc = new Y();
                do {
                    npc.pos = level.randomRespawnCell();
                } while (npc.pos == -1 || level.heaps.get(npc.pos) != null);
                level.mobs.add(npc);

                spawned = true;
                alternative = false;

                given = false;

                do {
                    reward = (Module) Generator.random(Generator.Category.MODULE);
                } while (reward.malfunctioning);
                reward.upgrade(2);
                reward.malfunctioning = true;
            }
        }

        public static void process(Mob mob) {
            if (spawned && given && !completed) {
                if (mob instanceof HolodeckMonarch.Undead) {
                    SpacebaseRun.level.drop(new HardLightEmitter(), mob.pos).sprite.drop();
                }
            }
        }

        public static void processMonarchDefeat(int pos) {
            if (spawned && given && !completed) {
                int missing = REQUIRED_EMITTERS;
                HardLightEmitter tokens = SpacebaseRun.hero.belongings.getItem(HardLightEmitter.class);
                if (tokens != null) {
                    missing -= tokens.quantity();
                }
                if (missing > 0) {
                    SpacebaseRun.level.drop(new HardLightEmitter().quantity(missing), pos).sprite.drop();
                }
            }
        }

        public static void complete() {
            reward = null;
            completed = true;

            Journal.remove(Journal.Feature.ARP);
        }

        public static boolean isCompleted() {
            return completed;
        }
    }
}
