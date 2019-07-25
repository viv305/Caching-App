package com.cache.strategies;

import org.junit.After;
import org.junit.Test;

import com.cache.SecondLevelCache;

import static com.cache.strategies.StrategyType.LFU;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LeastFrequentlyUsedTest {
    private SecondLevelCache<Integer, String> secondLevelCache;

    @After
    public void clearCache() {
        secondLevelCache.clear();
    }

    @Test
    public void shouldMoveObjectFromCacheTest() {
        secondLevelCache = new SecondLevelCache<>(2, 2, LFU);

        secondLevelCache.put(0, "String 0");
        secondLevelCache.get(0);
        secondLevelCache.get(0);
        secondLevelCache.put(1, "String 1");
        secondLevelCache.get(1); // Least Frequently Used - will be removed
        secondLevelCache.put(2, "String 2");
        secondLevelCache.get(2);
        secondLevelCache.get(2);
        secondLevelCache.put(3, "String 3");
        secondLevelCache.get(3);
        secondLevelCache.get(3);

        assertTrue(secondLevelCache.isObjectPresent(0));
        assertTrue(secondLevelCache.isObjectPresent(1));
        assertTrue(secondLevelCache.isObjectPresent(2));
        assertTrue(secondLevelCache.isObjectPresent(3));

        secondLevelCache.put(4, "String 4");
        secondLevelCache.get(4);
        secondLevelCache.get(4);

        assertTrue(secondLevelCache.isObjectPresent(0));
        assertFalse(secondLevelCache.isObjectPresent(1)); // Least Frequently Used - has been removed
        assertTrue(secondLevelCache.isObjectPresent(2));
        assertTrue(secondLevelCache.isObjectPresent(3));
        assertTrue(secondLevelCache.isObjectPresent(4));
    }

    @Test
    public void shouldNotRemoveObjectIfNotPresentTest() {
        secondLevelCache = new SecondLevelCache<>(1, 1, LFU);

        secondLevelCache.put(0, "String 0");
        secondLevelCache.put(1, "String 1");

        secondLevelCache.remove(2);

    }
}