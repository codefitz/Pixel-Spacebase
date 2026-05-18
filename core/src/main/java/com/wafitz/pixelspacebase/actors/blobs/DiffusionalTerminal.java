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
import com.wafitz.pixelspacebase.items.plasmids.Plasmid;
import com.wafitz.pixelspacebase.items.plasmids.TitanPlasmid;
import com.wafitz.pixelspacebase.items.plasmids.MyoFiberPlasmid;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Generator.Category;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.equippablemodules.EquippableModule;
import com.wafitz.pixelspacebase.items.blasters.Blaster;
import com.wafitz.pixelspacebase.items.modules.Module;
import com.wafitz.pixelspacebase.items.upgrades.EnhancementUpgrade;
import com.wafitz.pixelspacebase.items.upgrades.Upgrade;
import com.wafitz.pixelspacebase.items.upgrades.UpgradePatch;
import com.wafitz.pixelspacebase.items.weapon.Weapon;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Launcher;
import com.wafitz.pixelspacebase.items.weapon.melee.MeleeWeapon;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.mines.Mine;
import com.watabou.utils.Random;

public class DiffusionalTerminal extends WellWater {

    @Override
    protected Item affectItem(Item item) {

        if (item instanceof DM3000Launcher) {
            item = changeLauncher((DM3000Launcher) item);
        } else if (item instanceof MeleeWeapon) {
            item = changeWeapon((MeleeWeapon) item);
        } else if (item instanceof Upgrade) {
            item = changeScript((Upgrade) item);
        } else if (item instanceof Plasmid) {
            item = changePlasmid((Plasmid) item);
        } else if (item instanceof Module) {
            item = changeModule((Module) item);
        } else if (item instanceof Blaster) {
            item = changeBlaster((Blaster) item);
        } else if (item instanceof Mine.Device) {
            item = changeDevice((Mine.Device) item);
        } else if (item instanceof EquippableModule) {
            item = changeArtifact((EquippableModule) item);
        } else {
            item = null;
        }

        if (item != null) {
            Journal.remove(Feature.DIFFUSION_TERMINAL);
        }

        return item;

    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(Speck.factory(Speck.CHANGE), 0.2f, 0);
    }

    private DM3000Launcher changeLauncher(DM3000Launcher launcher) {
        Class<? extends Blaster> blasterClass = launcher.blasterClass();

        if (blasterClass == null) {
            return null;
        } else {
            Blaster n;
            do {
                n = (Blaster) Generator.random(Category.BLASTER);
            } while (n.getClass() == blasterClass);
            n.level(0);
            launcher.convertBlaster(n, null);
        }

        return launcher;
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
        n.convert = w.convert;

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

    private EquippableModule changeArtifact(EquippableModule a) {
        EquippableModule n = Generator.randomEquippableModule();

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

    private Mine.Device changeDevice(Mine.Device s) {

        Mine.Device n;

        do {
            n = (Mine.Device) Generator.random(Category.DEVICE);
        } while (n.getClass() == s.getClass());

        return n;
    }

    private Upgrade changeScript(Upgrade s) {
        if (s instanceof UpgradePatch) {

            return new EnhancementUpgrade();

        } else if (s instanceof EnhancementUpgrade) {

            return new UpgradePatch();

        } else {

            Upgrade n;
            do {
                n = (Upgrade) Generator.random(Category.UPGRADE);
            } while (n.getClass() == s.getClass());
            return n;
        }
    }

    private Plasmid changePlasmid(Plasmid p) {
        if (p instanceof MyoFiberPlasmid) {

            return new TitanPlasmid();

        } else if (p instanceof TitanPlasmid) {

            return new MyoFiberPlasmid();

        } else {

            Plasmid n;
            do {
                n = (Plasmid) Generator.random(Category.PLASMID);
            } while (n.getClass() == p.getClass());
            return n;
        }
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
