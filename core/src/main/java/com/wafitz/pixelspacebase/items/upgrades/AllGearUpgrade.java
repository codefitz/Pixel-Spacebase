package com.wafitz.pixelspacebase.items.upgrades;

import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.buffs.Camoflage;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.containers.Container;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

/** Rare one-use upgrade that improves all upgradeable items owned by the hero. */
public class AllGearUpgrade extends Upgrade {

    {
        initials = 12;
        bones = true;
    }

    @Override
    protected void doRead() {
        Sample.INSTANCE.play(Assets.SND_READ);
        Camoflage.dispel();

        ArrayList<Item> carriedItems = new ArrayList<>();
        for (Item item : curUser.belongings) {
            collectItems(item, carriedItems);
        }

        int upgraded = 0;
        for (Item item : carriedItems) {
            if (item.isUpgradable()) {
                item.upgrade();
                Badges.validateItemLevelAquired(item);
                upgraded++;
            }
        }

        curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, Math.max(3, upgraded));
        GLog.p(Messages.get(this, "upgraded", upgraded));
        setKnown();
        readAnimation();
    }

    private void collectItems(Item item, ArrayList<Item> items) {
        if (items.contains(item)) return;
        items.add(item);
        if (item instanceof Container) {
            for (Item contained : ((Container) item).items) {
                collectItems(contained, items);
            }
        }
    }

    @Override
    public Item random() {
        if (SpacebaseRun.limitedDrops.allGearUpgrade.count >= 2) {
            return Generator.random(Generator.Category.UPGRADE);
        }
        SpacebaseRun.limitedDrops.allGearUpgrade.count++;
        return super.random();
    }

    @Override
    public int cost() {
        return isKnown() ? 100 * quantity : super.cost();
    }
}
