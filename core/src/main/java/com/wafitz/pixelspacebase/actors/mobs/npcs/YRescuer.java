package com.wafitz.pixelspacebase.actors.mobs.npcs;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.YRescueJourney;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.YSprite;
import com.wafitz.pixelspacebase.windows.WndOptions;
import com.watabou.utils.Random;
import java.util.ArrayList;

/** Separate from quest Y: a persistent, optional way back after a sealed-room rescue. */
public class YRescuer extends NPC {
    {
        spriteClass = YSprite.class;
        properties.add(Property.IMMOVABLE);
    }

    public static ArrayList<Integer> rescueDestinations(int strandedDepth) {
        ArrayList<Integer> result = new ArrayList<>();
        // Ordinary gameplay decks only: no bosses, locked secret workshop, or ending deck.
        for (int depth = 1; depth <= 24; depth++) {
            if (depth != strandedDepth && !SpacebaseRun.bossLevel(depth)
                    && SpacebaseRun.canVisitDepth(depth)) result.add(depth);
        }
        return result;
    }

    /** Choose within the entrance-connected floor, never a sealed chamber or occupied cell.
     * Nearby enemies are deliberately NOT excluded: rescue is not a safe haven.
     */
    public static int randomReachableCell(Level level, int exclude) {
        boolean[] reached = new boolean[level.length()];
        ArrayList<Integer> queue = new ArrayList<>();
        ArrayList<Integer> candidates = new ArrayList<>();
        queue.add(level.entrance);
        reached[level.entrance] = true;
        for (int i = 0; i < queue.size(); i++) {
            int cell = queue.get(i);
            boolean occupied = false;
            for (Mob mob : level.mobs) if (mob.pos == cell) { occupied = true; break; }
            if (cell != exclude && !occupied && Level.passable[cell]
                    && !level.isPlasmaCell(cell) && !level.isDoorlessRoomCell(cell)) {
                candidates.add(cell);
            }
            int x = cell % level.width();
            int y = cell / level.width();
            for (int next : new int[]{x > 0 ? cell - 1 : -1,
                    x + 1 < level.width() ? cell + 1 : -1,
                    y > 0 ? cell - level.width() : -1,
                    y + 1 < level.height() ? cell + level.width() : -1}) {
                if (next >= 0 && !reached[next] && Level.passable[next]
                        && !level.isPlasmaCell(next)) {
                    reached[next] = true;
                    queue.add(next);
                }
            }
        }
        return candidates.isEmpty() ? -1 : Random.element(candidates);
    }

    public static int safeReturnCell(Level level) {
        int cell = level.entrance;
        boolean occupied = false;
        for (Mob mob : level.mobs) if (mob.pos == cell) { occupied = true; break; }
        if (!occupied && Level.passable[cell] && !level.isPlasmaCell(cell)
                && !level.isDoorlessRoomCell(cell)) return cell;
        return randomReachableCell(level, -1);
    }

    public static void placeOn(Level level) {
        YRescueJourney ticket = SpacebaseRun.hero.buff(YRescueJourney.class);
        boolean needed = ticket != null && ticket.sourceDepth > 0
                && ticket.rescueDepth == SpacebaseRun.depth
                && ticket.phase == YRescueJourney.Phase.VISITING;
        boolean present = false;
        for (Mob mob : level.mobs.toArray(new Mob[0])) {
            if (mob instanceof YRescuer) {
                if (!needed || present) mob.destroy();
                else present = true;
            }
        }
        if (needed && !present) {
            int cell = randomReachableCell(level, SpacebaseRun.hero.pos);
            if (cell >= 0) {
                YRescuer y = new YRescuer();
                y.pos = cell;
                level.mobs.add(y);
                Actor.add(y);
            }
        }
    }

    @Override protected boolean act() {
        throwItem();
        spend(TICK);
        return true;
    }
    @Override public int defenseSkill(Char enemy) { return 1000; }
    @Override public void damage(int dmg, Object src) { }
    @Override public void add(Buff buff) { }
    @Override public boolean reset() { return true; }

    @Override public boolean interact() {
        final YRescueJourney ticket = SpacebaseRun.hero.buff(YRescueJourney.class);
        if (ticket == null || ticket.sourceDepth < 1) return false;
        sprite.turnTo(pos, SpacebaseRun.hero.pos);
        GameScene.show(new WndOptions(Messages.get(Y.class, "name"),
                Messages.get(Y.class, "rescue_return", ticket.sourceDepth),
                Messages.get(Y.class, "rescue_ask")) {
            @Override protected void onSelect(int index) {
                // The saved ticket remains attached until arrival is committed.
                ticket.requestReturn();
            }
        });
        return false;
    }
}
