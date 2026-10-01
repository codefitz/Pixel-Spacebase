package com.wafitz.pixelspacebase.actors.mobs;

import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Plasma;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Weakness;
import org.junit.Test;
import java.util.HashSet;
import static org.junit.Assert.*;

public class SignalLeechTest {
    @Test public void spitDealsPlasmaDamageAndBurnsInsteadOfWeakening() {
        Target target = new Target();
        try {
            SignalLeech.spitAt(target, 14);
            assertEquals(86, target.HP);
            assertTrue(target.source instanceof Plasma);
            assertNotNull(target.buff(Burning.class));
            assertNull(target.buff(Weakness.class));
        } finally {
            Burning burn = target.buff(Burning.class);
            if (burn != null) target.remove(burn); // Headless target has no sprite for Burning.fx(false).
        }
    }

    @Test public void burningImmunityDoesNotCrashOrApplyBurn() {
        Target target = new Target() {
            @Override public HashSet<Class<?>> immunities() {
                HashSet<Class<?>> result = new HashSet<>();
                result.add(Burning.class);
                return result;
            }
        };
        SignalLeech.spitAt(target, 12);
        assertEquals(88, target.HP);
        assertNull(target.buff(Burning.class));
    }

    @Test public void lethalSpitDoesNotAttachBurnToDeadTarget() {
        Target target = new Target();
        target.HP = 10;
        SignalLeech.spitAt(target, 14);
        assertFalse(target.isAlive());
        assertNull(target.buff(Burning.class));
    }

    private static class Target extends Char {
        Object source;
        private final HashSet<Buff> localBuffs = new HashSet<>();
        Target() { HP = HT = 100; }
        // Exercise buff attachment without the Android actor scheduler or sprite effects.
        @Override public void add(Buff buff) { localBuffs.add(buff); }
        @Override public void remove(Buff buff) { localBuffs.remove(buff); }
        @Override public HashSet<Buff> buffs() { return new HashSet<>(localBuffs); }
        @Override public <T extends Buff> T buff(Class<T> type) {
            for (Buff buff : localBuffs) if (type.isInstance(buff)) return type.cast(buff);
            return null;
        }
        @Override public void damage(int damage, Object source) {
            this.source = source;
            HP = Math.max(0, HP - damage);
        }
    }
}
