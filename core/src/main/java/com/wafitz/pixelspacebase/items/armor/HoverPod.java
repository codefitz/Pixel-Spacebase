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
package com.wafitz.pixelspacebase.items.armor;

import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.RupturedCrewSuit;
import com.wafitz.pixelspacebase.items.Bomb;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.levels.vents.LightningVent;
import com.wafitz.pixelspacebase.levels.vents.Vent;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.mines.Mine;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;

public class HoverPod extends Armor {

    private static final int BASE_INTEGRITY = 15;
    private static final String INTEGRITY = "integrity";
    private static final String REINFORCEMENTS = "reinforcements";

    private int integrity = BASE_INTEGRITY;
    private int reinforcements;

    {
        image = ItemSpriteSheet.HOVERPOD;
    }

    public HoverPod() {
        super(4);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(INTEGRITY, integrity);
        bundle.put(REINFORCEMENTS, reinforcements);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        reinforcements = Math.max(0, bundle.getInt(REINFORCEMENTS));
        integrity = bundle.contains(INTEGRITY)
                ? Math.max(0, Math.min(bundle.getInt(INTEGRITY), maxIntegrity()))
                : maxIntegrity();
    }

    @Override
    public void reset() {
        super.reset();
        reinforcements = 0;
        integrity = BASE_INTEGRITY;
    }

    public int integrity() {
        return integrity;
    }

    public int maxIntegrity() {
        return BASE_INTEGRITY + reinforcements;
    }

    public boolean repair() {
        if (integrity >= maxIntegrity()) return false;
        integrity = maxIntegrity();
        updateQuickslot();
        return true;
    }

    public void reinforce() {
        reinforcements++;
        integrity = maxIntegrity();
        updateQuickslot();
    }

    public void absorbHit(Hero hero) {
        if (!isEquipped(hero) || integrity <= 0) return;
        integrity--;
        updateQuickslot();
        if (integrity > 0) {
            hero.sprite.showStatus(CharSprite.WARNING, "%d/%d", integrity, maxIntegrity());
        } else {
            GLog.w(Messages.get(this, "destroyed"));
            // Remove the pod before the blast so its own explosion reaches the hero.
            forceUnequip(hero);
            new Bomb().explode(hero.pos);
        }
    }

    @Override
    public String status() {
        return Integer.toString(integrity);
    }

    @Override
    public String info() {
        return super.info() + "\n\n" + Messages.get(this, "integrity", integrity, maxIntegrity());
    }

    public static HoverPod equipped(Hero hero) {
        return hero != null && hero.belongings.armor instanceof HoverPod
                ? (HoverPod) hero.belongings.armor : null;
    }

    public static boolean blocksImpact(Object source) {
        if (source instanceof Bomb || source instanceof RupturedCrewSuit) return false;
        return source instanceof Char || source instanceof Item || source instanceof Vent
                || source instanceof Mine || source == LightningVent.LIGHTNING;
    }

}
