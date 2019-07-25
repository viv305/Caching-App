package com.cache.strategies;

import org.junit.After;
import org.junit.Test;

import com.cache.SecondLevelCache;

import java.util.stream.IntStream;

import static com.cache.strategies.StrategyType.MRU;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MostRecentlyUsedTest {
    private SecondLevelCache<Integer, String> secondLevelCache;

    @After
    public void clearCache() {
        secondLevelCache.clear();
    }

    @Test
    public void shouldMoveObjectFromCacheTest() {
        secondLevelCache = new SecondLevelCache<>(2, 2, MRU);

        // i=3 - Most Recently Used - will be removed
        IntStream.range(0, 4).forEach(i -> {
            secondLevelCache.put(i, "String " + i);
            assertTrue(secondLevelCache.isObjectPresent(i));
            secondLevelCache.get(i);
        });

        secondLevelCache.put(4, "String 4");

        assertTrue(secondLevelCache.isObjectPresent(0));
        assertTrue(secondLevelCache.isObjectPresent(1));
        assertTrue(secondLevelCache.isObjectPresent(2));
        assertFalse(secondLevelCache.isObjectPresent(3)); //Most Recently Used - has been removed
        assertTrue(secondLevelCache.isObjectPresent(4));
    }
}
