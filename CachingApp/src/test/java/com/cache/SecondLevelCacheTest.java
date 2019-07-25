package com.cache;

import com.cache.SecondLevelCache;
import com.cache.strategies.StrategyType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class SecondLevelCacheTest {
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final String VALUE3 = "value3";

    private SecondLevelCache<Integer, String> secondLevelCache;

    @Before
    public void init() {
        secondLevelCache = new SecondLevelCache<>(1, 1);
    }

    @After
    public void clearCache() {
        secondLevelCache.clear();
    }

    @Test
    public void shouldPutGetAndRemoveObjectTest() {
        secondLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, secondLevelCache.get(0));
        assertEquals(1, secondLevelCache.getSize());

        secondLevelCache.remove(0);
        assertNull(secondLevelCache.get(0));
    }

    @Test
    public void shouldRemoveObjectFromFirstLevelTest() {
        secondLevelCache.put(0, VALUE1);
        secondLevelCache.put(1, VALUE2);

        assertEquals(VALUE1, secondLevelCache.getFirstLevelCache().get(0));
        assertEquals(VALUE2, secondLevelCache.getSecondLevelCache().get(1));

        secondLevelCache.remove(0);

        assertNull(secondLevelCache.getFirstLevelCache().get(0));
        assertEquals(VALUE2, secondLevelCache.getSecondLevelCache().get(1));
    }

    @Test
    public void shouldRemoveObjectFromSecondLevelTest() {
        secondLevelCache.put(0, VALUE1);
        secondLevelCache.put(1, VALUE2);

        assertEquals(VALUE1, secondLevelCache.getFirstLevelCache().get(0));
        assertEquals(VALUE2, secondLevelCache.getSecondLevelCache().get(1));

        secondLevelCache.remove(1);

        assertEquals(VALUE1, secondLevelCache.getFirstLevelCache().get(0));
        assertNull(secondLevelCache.getSecondLevelCache().get(1));
    }

    @Test
    public void shouldNotGetObjectFromCacheIfNotExistsTest() {
        secondLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, secondLevelCache.get(0));
        assertNull(secondLevelCache.get(111));
    }

    @Test
    public void shouldRemoveDuplicatedObjectFromSecondLevelWhenFirstLevelHasEmptyPlaceTest() {
        assertTrue(secondLevelCache.getFirstLevelCache().hasEmptyPlace());

        secondLevelCache.getSecondLevelCache().put(0, VALUE1);
        assertEquals(VALUE1, secondLevelCache.getSecondLevelCache().get(0));

        secondLevelCache.put(0, VALUE1);

        assertEquals(VALUE1, secondLevelCache.getFirstLevelCache().get(0));
        assertFalse(secondLevelCache.getSecondLevelCache().isObjectPresent(0));
    }

    @Test
    public void shouldPutObjectIntoCacheWhenFirstLevelHasEmptyPlaceTest() {
        assertTrue(secondLevelCache.getFirstLevelCache().hasEmptyPlace());
        secondLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, secondLevelCache.get(0));
        assertEquals(VALUE1, secondLevelCache.getFirstLevelCache().get(0));
        assertFalse(secondLevelCache.getSecondLevelCache().isObjectPresent(0));
    }

    @Test
    public void shouldPutObjectIntoCacheWhenObjectExistsInFirstLevelCacheTest() {
        secondLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, secondLevelCache.get(0));
        assertEquals(VALUE1, secondLevelCache.getFirstLevelCache().get(0));
        assertEquals(1, secondLevelCache.getFirstLevelCache().getSize());

        // put the same key with other value
        secondLevelCache.put(0, VALUE2);

        assertEquals(VALUE2, secondLevelCache.get(0));
        assertEquals(VALUE2, secondLevelCache.getFirstLevelCache().get(0));
        assertEquals(1, secondLevelCache.getFirstLevelCache().getSize());
    }

    @Test
    public void shouldPutObjectIntoCacheWhenSecondLevelHasEmptyPlaceTest() {
        IntStream.range(0, 1).forEach(i -> secondLevelCache.put(i, "String " + i));

        assertFalse(secondLevelCache.getFirstLevelCache().hasEmptyPlace());
        assertTrue(secondLevelCache.getSecondLevelCache().hasEmptyPlace());

        secondLevelCache.put(2, VALUE2);

        assertEquals(VALUE2, secondLevelCache.get(2));
        assertEquals(VALUE2, secondLevelCache.getSecondLevelCache().get(2));
    }

    @Test
    public void shouldPutObjectIntoCacheWhenObjectExistsInSecondLevelTest() {
        IntStream.range(0, 1).forEach(i -> secondLevelCache.put(i, "String " + i));

        assertFalse(secondLevelCache.getFirstLevelCache().hasEmptyPlace());

        secondLevelCache.put(2, VALUE2);

        assertEquals(VALUE2, secondLevelCache.get(2));
        assertEquals(VALUE2, secondLevelCache.getSecondLevelCache().get(2));
        assertEquals(1, secondLevelCache.getSecondLevelCache().getSize());

        // put the same key with other value
        secondLevelCache.put(2, VALUE3);

        assertEquals(VALUE3, secondLevelCache.get(2));
        assertEquals(VALUE3, secondLevelCache.getSecondLevelCache().get(2));
        assertEquals(1, secondLevelCache.getSecondLevelCache().getSize());
    }

    @Test
    public void shouldPutObjectIntoCacheWhenObjectShouldBeReplacedTest() {
        IntStream.range(0, 2).forEach(i -> secondLevelCache.put(i, "String " + i));

        assertFalse(secondLevelCache.hasEmptyPlace());
        assertFalse(secondLevelCache.getStrategy().isObjectPresent(3));

        secondLevelCache.put(3, VALUE3);

        assertTrue(secondLevelCache.get(3).equals(VALUE3));
        assertTrue(secondLevelCache.getStrategy().isObjectPresent(3));
        assertTrue(secondLevelCache.getFirstLevelCache().isObjectPresent(3));
        assertFalse(secondLevelCache.getSecondLevelCache().isObjectPresent(3));
    }

    @Test
    public void shouldGetCacheSizeTest() {
        secondLevelCache.put(0, VALUE1);
        assertEquals(1, secondLevelCache.getSize());

        secondLevelCache.put(1, VALUE2);
        assertEquals(2, secondLevelCache.getSize());
    }

    @Test
    public void isObjectPresentTest() {
        assertFalse(secondLevelCache.isObjectPresent(0));

        secondLevelCache.put(0, VALUE1);
        assertTrue(secondLevelCache.isObjectPresent(0));
    }

    @Test
    public void isEmptyPlaceTest() {
        assertFalse(secondLevelCache.isObjectPresent(0));
        secondLevelCache.put(0, VALUE1);
        assertTrue(secondLevelCache.hasEmptyPlace());

        secondLevelCache.put(1, VALUE2);
        assertFalse(secondLevelCache.hasEmptyPlace());
    }

    @Test
    public void shouldClearCacheTest() {
        secondLevelCache.put(0, VALUE1);
        secondLevelCache.put(1, VALUE2);

        assertEquals(2, secondLevelCache.getSize());
        assertTrue(secondLevelCache.getStrategy().isObjectPresent(0));
        assertTrue(secondLevelCache.getStrategy().isObjectPresent(1));

        secondLevelCache.clear();

        assertEquals(0, secondLevelCache.getSize());
        assertFalse(secondLevelCache.getStrategy().isObjectPresent(0));
        assertFalse(secondLevelCache.getStrategy().isObjectPresent(1));
    }

    @Test
    public void shouldUseLRUStrategyTest() {
        secondLevelCache = new SecondLevelCache<>(1, 1, StrategyType.LRU);
        secondLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, secondLevelCache.get(0));
        assertEquals(VALUE1, secondLevelCache.getFirstLevelCache().get(0));
        assertFalse(secondLevelCache.getSecondLevelCache().isObjectPresent(0));
    }

    @Test
    public void shouldUseMRUStrategyTest() {
        secondLevelCache = new SecondLevelCache<>(1, 1, StrategyType.MRU);
        secondLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, secondLevelCache.get(0));
        assertEquals(VALUE1, secondLevelCache.getFirstLevelCache().get(0));
        assertFalse(secondLevelCache.getSecondLevelCache().isObjectPresent(0));
    }
}