package com.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cache.Memory;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class MemoryTest {
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";

    private Memory<Integer, String> memoryCache;

    @Before
    public void init() {
        memoryCache = new Memory<>(3);
    }

    @After
    public void clearCache() {
        memoryCache.clear();
    }

    @Test
    public void shouldPutGetAndRemoveObjectTest() {
        memoryCache.put(0, VALUE1);
        assertEquals(VALUE1, memoryCache.get(0));
        assertEquals(1, memoryCache.getSize());

        memoryCache.remove(0);
        assertNull(memoryCache.get(0));
    }

    @Test
    public void shouldNotGetObjectFromCacheIfNotExistsTest() {
        memoryCache.put(0, VALUE1);
        assertEquals(VALUE1, memoryCache.get(0));
        assertNull(memoryCache.get(111));
    }

    @Test
    public void shouldNotRemoveObjectFromCacheIfNotExistsTest() {
        memoryCache.put(0, VALUE1);
        assertEquals(VALUE1, memoryCache.get(0));
        assertEquals(1, memoryCache.getSize());

        memoryCache.remove(5);
        assertEquals(VALUE1, memoryCache.get(0));
    }

    @Test
    public void shouldGetCacheSizeTest() {
        memoryCache.put(0, VALUE1);
        assertEquals(1, memoryCache.getSize());

        memoryCache.put(1, VALUE2);
        assertEquals(2, memoryCache.getSize());
    }

    @Test
    public void isObjectPresentTest() {
        assertFalse(memoryCache.isObjectPresent(0));

        memoryCache.put(0, VALUE1);
        assertTrue(memoryCache.isObjectPresent(0));
    }

    @Test
    public void isEmptyPlaceTest() {
        memoryCache = new Memory<>(5);

        IntStream.range(0, 4).forEach(i -> memoryCache.put(i, "String " + i));

        assertTrue(memoryCache.hasEmptyPlace());
        memoryCache.put(5, "String");
        assertFalse(memoryCache.hasEmptyPlace());
    }

    @Test
    public void shouldClearCacheTest() {
        IntStream.range(0, 3).forEach(i -> memoryCache.put(i, "String " + i));

        assertEquals(3, memoryCache.getSize());
        memoryCache.clear();
        assertEquals(0, memoryCache.getSize());
    }
}