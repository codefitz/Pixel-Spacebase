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
package com.wafitz.pixelspacebase.items.weapon.melee;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroSubClass;
import com.wafitz.pixelspacebase.effects.particles.ElmoParticle;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.blasters.Blaster;
import com.wafitz.pixelspacebase.items.blasters.Disintergrator;
import com.wafitz.pixelspacebase.items.blasters.EMP;
import com.wafitz.pixelspacebase.items.blasters.MindBlaster;
import com.wafitz.pixelspacebase.items.containers.Container;
import com.wafitz.pixelspacebase.items.scripts.RechargingScript;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndContainer;
import com.wafitz.pixelspacebase.windows.WndItem;
import com.wafitz.pixelspacebase.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DM3000Staff extends MeleeWeapon {

    private Blaster blaster;

    private static final String AC_IMBUE = "IMBUE";
    private static final String AC_ZAP = "ZAP";

    private static final float STAFF_SCALE_FACTOR = 0.75f;

    {
        image = ItemSpriteSheet.DM3000_STAFF;

        tier = 1;

        defaultAction = AC_ZAP;
        usesTargeting = true;

        unique = true;
        bones = false;
    }

    public DM3000Staff() {
        blaster = null;
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) +    //8 base damage, down from 10
                lvl * (tier + 1);   //scaling unaffected
    }

    public DM3000Staff(Blaster blaster) {
        this();
        blaster.identify();
        blaster.malfunctioning = false;
        this.blaster = blaster;
        blaster.maxCharges = Math.min(blaster.maxCharges + 1, 10);
        blaster.curCharges = blaster.maxCharges;
        name = Messages.get(blaster, "staff_name");
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_IMBUE);
        if (blaster != null && blaster.curCharges > 0) {
            actions.add(AC_ZAP);
        }
        return actions;
    }

    @Override
    public void activate(Char ch) {
        if (blaster != null) blaster.charge(ch, STAFF_SCALE_FACTOR);
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_IMBUE)) {

            curUser = hero;
            GameScene.selectItem(itemSelector, WndContainer.Mode.BLASTER, Messages.get(this, "prompt"));

        } else if (action.equals(AC_ZAP)) {

            if (blaster == null) {
                GameScene.show(new WndItem(null, this, true));
                return;
            }

            blaster.execute(hero, AC_ZAP);
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (blaster != null && Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE) {
            if (blaster.curCharges < blaster.maxCharges) blaster.partialCharge += 0.33f;
            RechargingScript.charge((Hero) attacker);
            blaster.onHit(this, attacker, defender, damage);
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int reachFactor(Hero hero) {
        int reach = super.reachFactor(hero);
        if (blaster instanceof Disintergrator && hero.subClass == HeroSubClass.BATTLEMAGE) {
            reach++;
        }
        return reach;
    }

    @Override
    public boolean collect(Container container) {
        if (super.collect(container)) {
            if (container.owner != null && blaster != null) {
                blaster.charge(container.owner, STAFF_SCALE_FACTOR);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDetach() {
        if (blaster != null) blaster.stopCharging();
    }

    public Item imbueBlaster(Blaster blaster, Char owner) {

        blaster.malfunctioning = false;
        this.blaster = null;

        //syncs the level of the two items.
        int targetLevel = Math.max(this.level(), blaster.level());

        //if the staff's level is being overridden by the blaster, preserve 1 upgrade
        if (blaster.level() >= this.level() && this.level() > 0) targetLevel++;

        int staffLevelDiff = targetLevel - this.level();
        if (staffLevelDiff > 0)
            this.upgrade(staffLevelDiff);
        else if (staffLevelDiff < 0)
            this.degrade(Math.abs(staffLevelDiff));

        int blasterLevelDiff = targetLevel - blaster.level();
        if (blasterLevelDiff > 0)
            blaster.upgrade(blasterLevelDiff);
        else if (blasterLevelDiff < 0)
            blaster.degrade(Math.abs(blasterLevelDiff));

        this.blaster = blaster;
        blaster.maxCharges = Math.min(blaster.maxCharges + 1, 10);
        blaster.curCharges = blaster.maxCharges;
        blaster.identify();
        if (owner != null) blaster.charge(owner);

        name = Messages.get(blaster, "staff_name");

        //This is necessary to reset any particles.
        //FIXME this is gross, should implement a better way to fully reset quickslot visuals
        int slot = Dungeon.quickslot.getSlot(this);
        if (slot != -1) {
            Dungeon.quickslot.clearSlot(slot);
            updateQuickslot();
            Dungeon.quickslot.setSlot(slot, this);
            updateQuickslot();
        }

        return this;
    }

    public Class<? extends Blaster> blasterClass() {
        return blaster != null ? blaster.getClass() : null;
    }

    @Override
    public Item upgrade(boolean enhance) {
        super.upgrade(enhance);

        if (blaster != null) {
            int curCharges = blaster.curCharges;
            blaster.upgrade();
            //gives the blaster one additional charge
            blaster.maxCharges = Math.min(blaster.maxCharges + 1, 10);
            blaster.curCharges = Math.min(blaster.curCharges + 1, 10);
            updateQuickslot();
        }

        return this;
    }

    @Override
    public Item degrade() {
        super.degrade();

        if (blaster != null) {
            int curCharges = blaster.curCharges;
            blaster.degrade();
            //gives the blaster one additional charge
            blaster.maxCharges = Math.min(blaster.maxCharges + 1, 10);
            blaster.curCharges = curCharges - 1;
            updateQuickslot();
        }

        return this;
    }

    @Override
    public String status() {
        if (blaster == null) return super.status();
        else return blaster.status();
    }

    @Override
    public String info() {
        String info = super.info();

        if (blaster == null) {
            info += "\n\n" + Messages.get(this, "no_blaster");
        } else {
            info += "\n\n" + Messages.get(this, "has_blaster", Messages.get(blaster, "name")) + " " + blaster.statsDesc();
        }

        return info;
    }

    @Override
    public Emitter emitter() {
        if (blaster == null) return null;
        Emitter emitter = new Emitter();
        emitter.pos(12.5f, 3);
        emitter.fillTarget = false;
        emitter.pour(StaffParticleFactory, 0.1f);
        return emitter;
    }

    private static final String BLASTER = "blaster";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BLASTER, blaster);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        blaster = (Blaster) bundle.get(BLASTER);
        if (blaster != null) {
            blaster.maxCharges = Math.min(blaster.maxCharges + 1, 10);
            name = Messages.get(blaster, "staff_name");
        }
    }

    @Override
    public int cost() {
        return 0;
    }

    private final WndContainer.Listener itemSelector = new WndContainer.Listener() {
        @Override
        public void onSelect(final Item item) {
            if (item != null) {

                if (!item.isIdentified()) {
                    GLog.w(Messages.get(DM3000Staff.class, "id_first"));
                    return;
                } else if (item.malfunctioning) {
                    GLog.w(Messages.get(DM3000Staff.class, "malfunctioning"));
                    return;
                }

                if (blaster == null) {
                    applyBlaster((Blaster) item);
                } else {
                    final int newLevel =
                            item.level() >= level() ?
                                    level() > 0 ?
                                            item.level() + 1
                                            : item.level()
                                    : level();
                    GameScene.show(
                            new WndOptions("",
                                    Messages.get(DM3000Staff.class, "warning", newLevel),
                                    Messages.get(DM3000Staff.class, "yes"),
                                    Messages.get(DM3000Staff.class, "no")) {
                                @Override
                                protected void onSelect(int index) {
                                    if (index == 0) {
                                        applyBlaster((Blaster) item);
                                    }
                                }
                            }
                    );
                }
            }
        }

        private void applyBlaster(Blaster blaster) {
            Sample.INSTANCE.play(Assets.SND_BURNING);
            curUser.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
            evoke(curUser);

            Dungeon.quickslot.clearItem(blaster);

            blaster.detach(curUser.belongings.backpack);
            Badges.validateTutorial();

            GLog.p(Messages.get(DM3000Staff.class, "imbue", blaster.name()));
            imbueBlaster(blaster, curUser);

            updateQuickslot();
        }
    };

    private final Emitter.Factory StaffParticleFactory = new Emitter.Factory() {
        @Override
        //reimplementing this is needed as instance creation of new staff particles must be within this class.
        public void emit(Emitter emitter, int index, float x, float y) {
            StaffParticle c = (StaffParticle) emitter.getFirstAvailable(StaffParticle.class);
            if (c == null) {
                c = new StaffParticle();
                emitter.add(c);
            }
            c.reset(x, y);
        }

        @Override
        //some particles need light mode, others don't
        public boolean lightMode() {
            return !((blaster instanceof Disintergrator)
                    || (blaster instanceof MindBlaster)
                    || (blaster instanceof EMP));
        }
    };

    //determines particle effects to use based on blaster the staff owns.
    public class StaffParticle extends PixelParticle {

        private float minSize;
        private float maxSize;
        public float sizeJitter = 0;

        public StaffParticle() {
            super();
        }

        public void reset(float x, float y) {
            revive();

            speed.set(0);

            this.x = x;
            this.y = y;

            if (blaster != null)
                blaster.staffFx(this);

        }

        public void setSize(float minSize, float maxSize) {
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

        public void setLifespan(float life) {
            lifespan = left = life;
        }

        public void shuffleXY(float amt) {
            x += Random.Float(-amt, amt);
            y += Random.Float(-amt, amt);
        }

        public void radiateXY(float amt) {
            float hypot = (float) Math.hypot(speed.x, speed.y);
            this.x += speed.x / hypot * amt;
            this.y += speed.y / hypot * amt;
        }

        @Override
        public void update() {
            super.update();
            size(minSize + (left / lifespan) * (maxSize - minSize) + Random.Float(sizeJitter));
        }
    }
}
