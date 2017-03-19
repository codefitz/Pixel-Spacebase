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
package com.wafitz.pixelspacebase.actors.blobs;

import com.wafitz.pixelspacebase.Journal;
import com.wafitz.pixelspacebase.Journal.Feature;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.effects.BlobEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.PowerTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.StrengthTech;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Generator.Category;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.artifacts.Artifact;
import com.wafitz.pixelspacebase.items.blasters.Blaster;
import com.wafitz.pixelspacebase.items.modules.Module;
import com.wafitz.pixelspacebase.items.scripts.EnhancementScript;
import com.wafitz.pixelspacebase.items.scripts.Script;
import com.wafitz.pixelspacebase.items.scripts.UpgradeScript;
import com.wafitz.pixelspacebase.items.weapon.Weapon;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Staff;
import com.wafitz.pixelspacebase.items.weapon.melee.MeleeWeapon;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.triggers.Trigger;
import com.watabou.utils.Random;

public class DiffusionalTerminal extends WellWater {

    @Override
    protected Item affectItem(Item item) {

        if (item instanceof DM3000Staff) {
            item = changeStaff((DM3000Staff) item);
        } else if (item instanceof MeleeWeapon) {
            item = changeWeapon((MeleeWeapon) item);
        } else if (item instanceof Script) {
            item = changeScript((Script) item);
        } else if (item instanceof ExperimentalTech) {
            item = changeExperimentalTech((ExperimentalTech) item);
        } else if (item instanceof Module) {
            item = changeModule((Module) item);
        } else if (item instanceof Blaster) {
            item = changeBlaster((Blaster) item);
        } else if (item instanceof Trigger.Gadget) {
            item = changeGadget((Trigger.Gadget) item);
        } else if (item instanceof Artifact) {
            item = changeArtifact((Artifact) item);
        } else {
            item = null;
        }

        if (item != null) {
            Journal.remove(Feature.WELL_OF_TRANSMUTATION);
        }

        return item;

    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(Speck.factory(Speck.CHANGE), 0.2f, 0);
    }

    private DM3000Staff changeStaff(DM3000Staff staff) {
        Class<? extends Blaster> blasterClass = staff.blasterClass();

        if (blasterClass == null) {
            return null;
        } else {
            Blaster n;
            do {
                n = (Blaster) Generator.random(Category.BLASTER);
            } while (n.getClass() == blasterClass);
            n.level(0);
            staff.imbueBlaster(n, null);
        }

        return staff;
    }

    private Weapon changeWeapon(MeleeWeapon w) {

        Weapon n;
        Category c = Generator.wepTiers[w.tier - 1];

        do {
            try {
                n = (Weapon) c.classes[Random.chances(c.probs)].newInstance();
            } catch (Exception e) {
                PixelSpacebase.reportException(e);
                return null;
            }
        } while (!(n instanceof MeleeWeapon) || n.getClass() == w.getClass());

        int level = w.level();
        if (level > 0) {
            n.upgrade(level);
        } else if (level < 0) {
            n.degrade(-level);
        }

        n.enhancement = w.enhancement;
        n.levelKnown = w.levelKnown;
        n.malfunctioningKnown = w.malfunctioningKnown;
        n.malfunctioning = w.malfunctioning;
        n.imbue = w.imbue;

        return n;

    }

    private Module changeModule(Module r) {
        Module n;
        do {
            n = (Module) Generator.random(Category.MODULE);
        } while (n.getClass() == r.getClass());

        n.level(0);

        int level = r.level();
        if (level > 0) {
            n.upgrade(level);
        } else if (level < 0) {
            n.degrade(-level);
        }

        n.levelKnown = r.levelKnown;
        n.malfunctioningKnown = r.malfunctioningKnown;
        n.malfunctioning = r.malfunctioning;

        return n;
    }

    private Artifact changeArtifact(Artifact a) {
        Artifact n = Generator.randomArtifact();

        if (n != null) {
            n.malfunctioningKnown = a.malfunctioningKnown;
            n.malfunctioning = a.malfunctioning;
            n.levelKnown = a.levelKnown;
            n.transferUpgrade(a.visiblyUpgraded());
        }

        return n;
    }

    private Blaster changeBlaster(Blaster w) {

        Blaster n;
        do {
            n = (Blaster) Generator.random(Category.BLASTER);
        } while (n.getClass() == w.getClass());

        n.level(0);
        n.upgrade(w.level());

        n.levelKnown = w.levelKnown;
        n.malfunctioningKnown = w.malfunctioningKnown;
        n.malfunctioning = w.malfunctioning;

        return n;
    }

    private Trigger.Gadget changeGadget(Trigger.Gadget s) {

        Trigger.Gadget n;

        do {
            n = (Trigger.Gadget) Generator.random(Category.GADGET);
        } while (n.getClass() == s.getClass());

        return n;
    }

    private Script changeScript(Script s) {
        if (s instanceof UpgradeScript) {

            return new EnhancementScript();

        } else if (s instanceof EnhancementScript) {

            return new UpgradeScript();

        } else {

            Script n;
            do {
                n = (Script) Generator.random(Category.SCRIPT);
            } while (n.getClass() == s.getClass());
            return n;
        }
    }

    private ExperimentalTech changeExperimentalTech(ExperimentalTech p) {
        if (p instanceof StrengthTech) {

            return new PowerTech();

        } else if (p instanceof PowerTech) {

            return new StrengthTech();

        } else {

            ExperimentalTech n;
            do {
                n = (ExperimentalTech) Generator.random(Category.EXPERIMENTALTECH);
            } while (n.getClass() == p.getClass());
            return n;
        }
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
