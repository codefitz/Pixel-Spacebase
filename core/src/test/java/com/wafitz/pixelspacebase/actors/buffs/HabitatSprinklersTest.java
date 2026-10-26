package com.wafitz.pixelspacebase.actors.buffs;

import com.wafitz.pixelspacebase.actors.Char;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class HabitatSprinklersTest {
    @Test public void legacySprinklerBuffIsDiscardedInsteadOfAttaching() {
        HabitatSprinklers sprinklers = new HabitatSprinklers();
        Char hero = new Char() { };
        assertFalse(sprinklers.attachTo(hero));
        assertNull(sprinklers.target);
        assertTrue(hero.buffs().isEmpty());
    }

    @Test public void previouslyAttachedBuffRetiresWithoutTouchingFireOrBurning() {
        final boolean[] removed = {false};
        Char hero = new Char() {
            @Override public void remove(Buff buff) { removed[0] = true; }
        };
        HabitatSprinklers sprinklers = new HabitatSprinklers();
        sprinklers.target = hero;
        // No level or actor list is needed: retiring the old buff must not inspect either.
        assertTrue(sprinklers.act());
        assertTrue(removed[0]);
    }
}
