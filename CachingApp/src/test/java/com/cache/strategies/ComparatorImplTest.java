package com.cache.strategies;

import org.junit.Before;
import org.junit.Test;

import com.cache.strategies.ComparatorImpl;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ComparatorImplTest {
    private ComparatorImpl<String> comparator;
    private Map<String, Long> comparatorMap;

    @Before
    public void setUp() {
        comparatorMap = new HashMap<>();
        comparator = new ComparatorImpl<>(comparatorMap);
    }

    @Test
    public void keysShouldBeEquals() throws Exception {
        //Given
        comparatorMap.put("key1", 1L);
        comparatorMap.put("key2", 1L);
        //When
        int result = comparator.compare("key1", "key2");
        //Then
        assertEquals(0, result);
    }

    @Test
    public void key1ShouldBeLaterThanKey2() throws Exception {
        //Given
        comparatorMap.put("key1", 2L);
        comparatorMap.put("key2", 1L);
        //When
        int result = comparator.compare("key1", "key2");
        //Then
        assertEquals(1, result);
    }

    @Test
    public void key1ShouldBeEarlierThanKey2() throws Exception {
        //Given
        comparatorMap.put("key1", 1L);
        comparatorMap.put("key2", 2L);
        //When
        int result = comparator.compare("key1", "key2");
        //Then
        assertEquals(-1, result);
    }

    @Test(expected = NullPointerException.class)
    public void shouldInitNPE() throws Exception {
        //Given
        comparatorMap.put("key1", 1L);
        comparatorMap.put("key2", null);
        //When
        comparator.compare("key1", "key2");
    }
}
