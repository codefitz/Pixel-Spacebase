package com.wafitz.pixelspacebase.windows;

import org.junit.Test;
import static org.junit.Assert.*;

public class RankingItemNameTest {
    private final WndRanking.TextWidth monospace = text -> text.codePointCount(0, text.length());

    @Test public void fittingNameIsPreserved() {
        assertEquals("Loader", WndRanking.fitItemName("Loader", 6, monospace));
    }

    @Test public void longNameIsShortenedWithEllipsis() {
        assertEquals("Hun...", WndRanking.fitItemName("Hunter suit", 6, monospace));
    }

    @Test public void zeroNegativeOrInsufficientWidthCannotUnderflow() {
        for (int width : new int[]{0, -10, 1, 2}) {
            assertEquals("", WndRanking.fitItemName("Hunter suit", width, monospace));
        }
        assertEquals("...", WndRanking.fitItemName("Hunter suit", 3, monospace));
    }

    @Test public void emptyOrMissingNamesAreSafe() {
        assertEquals("", WndRanking.fitItemName("", 50, monospace));
        assertEquals("", WndRanking.fitItemName(null, 50, monospace));
    }

    @Test public void shorteningDoesNotSplitSupplementaryCharacters() {
        assertEquals("\uD83D\uDE80...", WndRanking.fitItemName("\uD83D\uDE80Hoverpod", 4, monospace));
    }
}
