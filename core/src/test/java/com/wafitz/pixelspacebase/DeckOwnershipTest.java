package com.wafitz.pixelspacebase;

import com.watabou.utils.Bundle;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeckOwnershipTest {
    @Test public void currentRunCanRevisitItsOwnDeck() {
        assertTrue(SpacebaseRun.deckBelongsToRun(new SeedBundle(123L), 123L, false));
    }

    @Test public void anotherRunsDeckIsRejectedIncludingDuringLegacyMigration() {
        assertFalse(SpacebaseRun.deckBelongsToRun(new SeedBundle(456L), 123L, false));
        assertFalse(SpacebaseRun.deckBelongsToRun(new SeedBundle(456L), 123L, true));
    }

    @Test public void newRunsRejectUnmarkedDecksLeftByOlderGames() {
        assertFalse(SpacebaseRun.deckBelongsToRun(new SeedBundle(null), 123L, false));
    }

    @Test public void existingLegacyRunsRetainAccessToUnmarkedDecks() {
        assertTrue(SpacebaseRun.deckBelongsToRun(new SeedBundle(null), 123L, true));
    }

    // The Android JSON implementation is unavailable in these plain JVM tests.
    private static class SeedBundle extends Bundle {
        private final Long seed;
        SeedBundle(Long seed) { this.seed = seed; }
        @Override public boolean contains(String key) { return "seed".equals(key) && seed != null; }
        @Override public long getLong(String key) { return seed == null ? 0 : seed; }
    }
}
