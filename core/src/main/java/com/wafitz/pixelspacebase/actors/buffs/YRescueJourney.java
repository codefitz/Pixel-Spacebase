package com.wafitz.pixelspacebase.actors.buffs;

import com.watabou.utils.Bundle;

/** Silent, saved return ticket; every actual fall replaces the point of origin. */
public class YRescueJourney extends Buff {
    public int sourceDepth = -1;
    public int rescueDepth = -1;

    public void recordFall(int depth) {
        sourceDepth = depth;
        rescueDepth = -1;
    }

    @Override public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("sourceDepth", sourceDepth);
        bundle.put("rescueDepth", rescueDepth);
    }

    @Override public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        sourceDepth = bundle.contains("sourceDepth") ? bundle.getInt("sourceDepth") : -1;
        rescueDepth = bundle.contains("rescueDepth") ? bundle.getInt("rescueDepth") : -1;
    }
}
