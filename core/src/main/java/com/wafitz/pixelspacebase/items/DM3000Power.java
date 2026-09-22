package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Recharging;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.upgrades.RechargeUpgrade;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class DM3000Power {

    public static final float TIME_TO_CONSUME = 1f;

    private DM3000Power() {
    }

    public static void consumeBattery(Hero hero) {
        Buff.affect(hero, Recharging.class, 4f);
        RechargeUpgrade.charge(hero);

        if (hero.sprite != null) {
            hero.sprite.operate(hero.pos);
        }
        Sample.INSTANCE.play(Assets.SND_EAT);
        GLog.p(Messages.get(DM3000Power.class, "consume"));
    }
}
