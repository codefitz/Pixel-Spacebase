package com.wafitz.pixelspacebase.windows;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Leonard;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.blasters.EMP;
import com.wafitz.pixelspacebase.items.equippablemodules.McGyvrModule;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;

public class WndLeonardReward extends WndOptions {

    private final Leonard leonard;
    private final Item mcGyverTool = new McGyvrModule();
    private final Item repairBlaster = new EMP();

    public WndLeonardReward(Leonard leonard) {
        super(Messages.get(WndLeonardReward.class, "title"),
                Messages.get(WndLeonardReward.class, "message"),
                Messages.titleCase(new McGyvrModule().name()),
                Messages.titleCase(new EMP().name()));
        this.leonard = leonard;
    }

    @Override
    protected void onSelect(int index) {
        Item reward = index == 0 ? mcGyverTool : repairBlaster;
        reward.identify();

        if (reward.doPickUp(SpacebaseRun.hero)) {
            GLog.i(Messages.get(SpacebaseRun.hero, "you_now_have", reward.name()));
        } else {
            SpacebaseRun.level.drop(reward, leonard.pos).sprite.drop();
        }

        Leonard.Quest.rewardCollected();
    }
}
