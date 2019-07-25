package com.cache.strategies;

import org.junit.After;
import org.junit.Test;

import com.cache.SecondLevelCache;

import java.util.stream.IntStream;

import static com.cache.strategies.StrategyType.LRU;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LeastRecentlyUsedTest {
    private SecondLevelCache<Integer, String> secondLevelCache;

    @After
    public void clearCache() {
        secondLevelCache.clear();
    }

    @Test
    public void shouldMoveObjectFromCacheTest() {
        secondLevelCache = new SecondLevelCache<>(2, 2, LRU);

        // i=0 - Least Recently Used - will be removed
        IntStream.range(0, 4).forEach(i -> {
            secondLevelCache.put(i, "String " + i);
            assertTrue(secondLevelCache.isObjectPresent(i));
            secondLevelCache.get(i);
        });

        secondLevelCache.put(4, "String 4");

        assertFalse(secondLevelCache.isObjectPresent(0)); //Least Recently Used - has been removed
        assertTrue(secondLevelCache.isObjectPresent(1));
        assertTrue(secondLevelCache.isObjectPresent(2));
        assertTrue(secondLevelCache.isObjectPresent(3));
        assertTrue(secondLevelCache.isObjectPresent(4));
    }
}
