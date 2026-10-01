package com.wafitz.pixelspacebase.actors.mobs;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UniformDisguiseTest {

    @Test
    public void unprovokedHostileMachinesIgnoreUniformedHero() {
        assertTrue(Mob.shouldIgnoreUniformedHero(true, true, false, true));
    }

    @Test
    public void provokedMachinesRememberTheHero() {
        assertFalse(Mob.shouldIgnoreUniformedHero(true, true, true, true));
    }

    @Test
    public void disguiseDoesNotAffectOrganicOrAlliedMobs() {
        assertFalse(Mob.shouldIgnoreUniformedHero(true, false, false, true));
        assertFalse(Mob.shouldIgnoreUniformedHero(false, true, false, true));
    }

    @Test
    public void removingUniformRestoresOrdinaryTargeting() {
        assertFalse(Mob.shouldIgnoreUniformedHero(true, true, false, false));
    }
}
