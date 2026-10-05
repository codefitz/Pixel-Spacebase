package com.wafitz.pixelspacebase.actors.mobs.npcs;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.TorchBattery;
import com.wafitz.pixelspacebase.items.food.SynthesizedFood;
import com.wafitz.pixelspacebase.items.plasmids.HealingPlasmid;
import com.wafitz.pixelspacebase.items.plasmids.PolymerPlasmid;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.windows.WndOptions;
import com.wafitz.pixelspacebase.windows.WndMessage;
import com.watabou.utils.Bundle;
import com.watabou.utils.Bundlable;
import java.util.ArrayList;
import java.io.IOException;

/** Friendly trader with finite stock saved on the planet, paid for with station parts. */
public class AlienTrader extends AlienResident {
    private ArrayList<Item> stock = new ArrayList<>();
    private boolean initialized;
    public void seedStock() {
        if (initialized) return;
        initialized = true;
        stock.add(new HealingPlasmid().identify());
        stock.add(new TorchBattery().identify());
        stock.add(new SynthesizedFood().identify());
        stock.add(new PolymerPlasmid().identify());
    }
    private int price(Item item) {
        if (item instanceof HealingPlasmid) return 140;
        if (item instanceof TorchBattery) return 80;
        if (item instanceof SynthesizedFood) return 90;
        return 110;
    }
    @Override public boolean interact() {
        sprite.turnTo(pos, SpacebaseRun.hero.pos);
        seedStock();
        if (stock.isEmpty()) {
            GameScene.show(new WndMessage(Messages.get(this, "sold_out")));
            return false;
        }
        final ArrayList<Item> offered = new ArrayList<>(stock);
        String[] options = new String[offered.size() + 1];
        for (int i = 0; i < offered.size(); i++) options[i] = Messages.get(this, "offer", offered.get(i).name(), price(offered.get(i)));
        options[offered.size()] = Messages.get(this, "leave");
        GameScene.show(new WndOptions(name, Messages.get(this, "greeting", SpacebaseRun.parts), options) {
            @Override protected void onSelect(int index) {
                if (index < offered.size()) confirm(offered.get(index));
            }
        });
        return false;
    }
    private void confirm(final Item item) {
        GameScene.show(new WndOptions(item.name(), item.info() + "\n\n" + Messages.get(this, "price", price(item)),
                Messages.get(this, "buy"), Messages.get(this, "leave")) {
            @Override protected void onSelect(int index) { if (index == 0) buy(item); }
        });
    }
    private void buy(Item item) {
        if (!stock.contains(item)) return;
        int price = price(item);
        if (SpacebaseRun.parts < price) {
            GameScene.show(new WndMessage(Messages.get(this, "no_parts")));
            return;
        }
        // Leave both currency and stock unchanged if the item cannot fit.
        if (!item.collect(SpacebaseRun.hero.belongings.backpack)) {
            GameScene.show(new WndMessage(Messages.get(this, "no_space")));
            return;
        }
        SpacebaseRun.parts -= price;
        stock.remove(item);
        GameScene.pickUp(item);
        try { SpacebaseRun.saveAll(); } catch (IOException error) { PixelSpacebase.reportException(error); }
        SpacebaseRun.hero.spendAndNext(1f);
    }
    @Override public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle); bundle.put("alienStock", stock); bundle.put("alienStockInitialized", initialized);
    }
    @Override public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle); stock = new ArrayList<>();
        if (bundle.contains("alienStock")) for (Bundlable item : bundle.getCollection("alienStock")) {
            if (item instanceof Item) stock.add((Item) item);
        }
        initialized = bundle.getBoolean("alienStockInitialized");
    }
}
