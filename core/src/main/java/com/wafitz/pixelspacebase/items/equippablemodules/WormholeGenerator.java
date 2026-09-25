package com.wafitz.pixelspacebase.items.equippablemodules;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.TorchBattery;
import com.wafitz.pixelspacebase.items.upgrades.PhaseShiftUpgrade;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** A reusable teleporter powered directly by batteries in the backpack. */
public class WormholeGenerator extends EquippableModule {

    private static final String AC_ACTIVATE = "ACTIVATE";
    private static final float TIME_TO_ACTIVATE = 1f;

    {
        image = ItemSpriteSheet.WORMHOLE_GENERATOR;
        defaultAction = AC_ACTIVATE;
        levelCap = 1;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero)) actions.add(AC_ACTIVATE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (!AC_ACTIVATE.equals(action)) return;

        if (!isEquipped(hero)) {
            GLog.i(Messages.get(EquippableModule.class, "need_to_equip"));
            return;
        }
        if (malfunctioning) {
            GLog.w(Messages.get(this, "malfunctioning"));
            return;
        }

        TorchBattery battery = hero.belongings.getItem(TorchBattery.class);
        if (battery == null) {
            GLog.w(Messages.get(this, "no_battery"));
            return;
        }

        int destination = randomDestination(hero);
        if (destination == -1) {
            GLog.w(Messages.get(this, "no_destination"));
            return;
        }

        battery.detach(hero.belongings.backpack);
        PhaseShiftUpgrade.appear(hero, destination);
        SpacebaseRun.level.press(destination, hero);
        SpacebaseRun.observe();
        GameScene.updateFog();
        hero.spendAndNext(TIME_TO_ACTIVATE);
        GLog.i(Messages.get(this, "teleported"));
    }

    private static int randomDestination(Hero hero) {
        int chosen = -1;
        int count = 0;
        for (int cell = 0; cell < SpacebaseRun.level.length(); cell++) {
            int terrain = SpacebaseRun.level.map[cell];
            if (cell == hero.pos || !Level.passable[cell] || Level.solid[cell]
                    || SpacebaseRun.level.isDoorlessRoomCell(cell)
                    || terrain == Terrain.ENTRANCE || terrain == Terrain.EXIT
                    || terrain == Terrain.UNLOCKED_EXIT || Actor.findChar(cell) != null) {
                continue;
            }
            // Reservoir sampling gives every eligible cell the same chance.
            if (Random.Int(++count) == 0) chosen = cell;
        }
        return chosen;
    }

    @Override
    protected ModuleBuff passiveBuff() {
        // The generator has no time-based recharge or passive effect.
        return new ModuleBuff();
    }

    @Override
    public String status() {
        if (SpacebaseRun.hero == null) return null;
        TorchBattery battery = SpacebaseRun.hero.belongings.getItem(TorchBattery.class);
        return battery == null ? "0" : Integer.toString(battery.quantity());
    }

    @Override
    public Item random() {
        return this;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
}
