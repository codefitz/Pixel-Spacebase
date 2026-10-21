package com.wafitz.pixelspacebase.actors.buffs;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Y;
import com.watabou.utils.Bundle;

/** Counts player turns spent in the sealed fall chamber and calls Y after twenty. */
public class StrandedRoomRescue extends Buff {

    private static final String TURNS = "turns";
    private static final String ROOM_DEPTH = "roomDepth";

    private int turns = 0;
    private int roomDepth = -1;

    /** Called once when the hero spends time on an action, regardless of its duration. */
    public void recordHeroTurn() {
        if (target == SpacebaseRun.hero
                && SpacebaseRun.level != null
                && SpacebaseRun.depth == roomDepth
                && SpacebaseRun.level.isDoorlessRoomCell(target.pos)) {
            turns++;
            if (turns == 20) {
                // Let the actor loop dispatch the rescue after this action, even for fast actions.
                spend(-cooldown());
            }
        }
    }

    @Override
    public boolean attachTo(com.wafitz.pixelspacebase.actors.Char target) {
        boolean attached = super.attachTo(target);
        if (attached) roomDepth = SpacebaseRun.depth;
        return attached;
    }

    @Override
    public boolean act() {
        if (target != SpacebaseRun.hero
                || SpacebaseRun.level == null
                || SpacebaseRun.depth != roomDepth
                || !SpacebaseRun.level.isDoorlessRoomCell(target.pos)) {
            detach();
            return true;
        }

        if (turns >= 20) {
            detach();
            Y.rescueStrandedHero();
        } else {
            spend(Actor.TICK);
        }
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TURNS, turns);
        bundle.put(ROOM_DEPTH, roomDepth);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        turns = bundle.getInt(TURNS);
        roomDepth = bundle.getInt(ROOM_DEPTH);
    }
}
