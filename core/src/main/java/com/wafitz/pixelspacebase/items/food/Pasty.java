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
package com.wafitz.pixelspacebase.items.food;

import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Hunger;
import com.wafitz.pixelspacebase.actors.buffs.Recharging;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.scripts.RechargingScript;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

import java.util.Calendar;

public class Pasty extends Food {

    //TODO: implement fun stuff for other holidays
    //TODO: probably should externalize this if I want to add any more festive stuff.
    private enum Holiday {
        NONE,
        EASTER, //TBD
        HWEEN,//2nd week of october though first day of november
        XMAS //3rd week of december through first week of january
    }

    private static Holiday holiday;

    static {

        holiday = Holiday.NONE;

        final Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                // This reports an error on commit - Issue #230099 Android Studio, IDEA-127764 IntelliJ
                if (calendar.get(Calendar.WEEK_OF_MONTH) == 1)
                    holiday = Holiday.XMAS;
                break;
            case Calendar.OCTOBER:
                if (calendar.get(Calendar.WEEK_OF_MONTH) >= 2)
                    holiday = Holiday.HWEEN;
                break;
            case Calendar.NOVEMBER:
                // This reports an error on commit - Issue #230099 Android Studio, IDEA-127764 IntelliJ
                if (calendar.get(Calendar.DAY_OF_MONTH) == 1)
                    holiday = Holiday.HWEEN;
                break;
            case Calendar.DECEMBER:
                if (calendar.get(Calendar.WEEK_OF_MONTH) >= 3)
                    holiday = Holiday.XMAS;
                break;
        }
    }

    {

        switch (holiday) {
            case NONE:
                name = Messages.get(this, "pasty");
                image = ItemSpriteSheet.PASTY;
                break;
            case HWEEN:
                name = Messages.get(this, "pie");
                image = ItemSpriteSheet.PUMPKIN_PIE;
                break;
            case XMAS:
                name = Messages.get(this, "cane");
                image = ItemSpriteSheet.CANDY_CANE;
                break;
        }

        energy = Hunger.STARVING;
        hornValue = 5;

        bones = true;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_EAT)) {
            switch (holiday) {
                case NONE:
                    break; //do nothing extra
                case HWEEN:
                    //heals for 10% max hp
                    hero.HP = Math.min(hero.HP + hero.HT / 10, hero.HT);
                    hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                    break;
                case XMAS:
                    Buff.affect(hero, Recharging.class, 2f); //half of a charge
                    RechargingScript.charge(hero);
                    break;
            }
        }
    }

    @Override
    public String info() {
        switch (holiday) {
            case NONE:
            default:
                return Messages.get(this, "pasty_desc");
            case HWEEN:
                return Messages.get(this, "pie_desc");
            case XMAS:
                return Messages.get(this, "cane_desc");
        }
    }

    @Override
    public int price() {
        return 20 * quantity;
    }
}
