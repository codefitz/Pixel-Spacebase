package com.wafitz.pixelspacebase.levels;

import org.junit.Test;
import java.lang.reflect.Method;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FinalBossSealedRoomTest {
    @Test
    public void finalBossSkipsSharedChamberGeneration() throws Exception {
        DeepContainmentCoreLevel level = new DeepContainmentCoreLevel();
        assertFalse(level.needsDoorlessRoom());
        // Both new-game generation and old-save migration use this method.
        // An unbuilt level has no map: the exclusion must happen before painting.
        Method generate = Level.class.getDeclaredMethod("createDoorlessRoom");
        generate.setAccessible(true);
        generate.invoke(level);
        assertEquals(-1, level.doorlessRoomLandingCell());
    }

    @Test
    public void normalDecksStillRequireChambers() {
        assertTrue(new MaintenanceLevel().needsDoorlessRoom());
        assertTrue(new HabitationRingLevel().needsDoorlessRoom());
        assertTrue(new DeepContainmentDeckLevel().needsDoorlessRoom());
    }
}
