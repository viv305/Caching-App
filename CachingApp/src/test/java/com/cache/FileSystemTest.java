package com.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileSystemTest {
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";

    private FileSystem<Integer, String> fileSystemCache;

    @Before
    public void init() {
        fileSystemCache = new FileSystem<>();
    }

    @After
    public void clearCache() {
        fileSystemCache.clear();
    }

    @Test
    public void shouldPutGetAndRemoveObjectTest() {
        fileSystemCache.put(0, VALUE1);
        assertEquals(VALUE1, fileSystemCache.get(0));
        assertEquals(1, fileSystemCache.getSize());

        fileSystemCache.remove(0);
        assertNull(fileSystemCache.get(0));
    }

    @Test
    public void shouldNotGetObjectFromCacheIfNotExistsTest() {
        fileSystemCache.put(0, VALUE1);
        assertEquals(VALUE1, fileSystemCache.get(0));
        assertNull(fileSystemCache.get(111));
    }

    @Test
    public void shouldNotRemoveObjectFromCacheIfNotExistsTest() {
        fileSystemCache.put(0, VALUE1);
        assertEquals(VALUE1, fileSystemCache.get(0));
        assertEquals(1, fileSystemCache.getSize());

        fileSystemCache.remove(5);
        assertEquals(VALUE1, fileSystemCache.get(0));
    }

    @Test
    public void shouldGetCacheSizeTest() {
        fileSystemCache.put(0, VALUE1);
        assertEquals(1, fileSystemCache.getSize());

        fileSystemCache.put(1, VALUE2);
        assertEquals(2, fileSystemCache.getSize());
    }

    @Test
    public void isObjectPresentTest() {
        assertFalse(fileSystemCache.isObjectPresent(0));

        fileSystemCache.put(0, VALUE1);
        assertTrue(fileSystemCache.isObjectPresent(0));
    }

    @Test
    public void isEmptyPlaceTest() {
        fileSystemCache = new FileSystem<>(5);

        IntStream.range(0, 4).forEach(i -> fileSystemCache.put(i, "String " + i));
        assertTrue(fileSystemCache.hasEmptyPlace());
        fileSystemCache.put(5, "String");
        assertFalse(fileSystemCache.hasEmptyPlace());
    }

    @Test
    public void shouldClearCacheTest() {
        IntStream.range(0, 3).forEach(i -> fileSystemCache.put(i, "String " + i));

        assertEquals(3, fileSystemCache.getSize());
        fileSystemCache.clear();
        assertEquals(0, fileSystemCache.getSize());
    }
}
