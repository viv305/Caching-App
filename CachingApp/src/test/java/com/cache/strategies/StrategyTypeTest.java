package com.cache.strategies;

import org.junit.Test;

import com.cache.strategies.StrategyType;

import static org.junit.Assert.assertEquals;

public class StrategyTypeTest {
    @Test
    public void shouldGetCorrectValuesFromEnumTest() {
        assertEquals(StrategyType.LFU, StrategyType.valueOf("LFU"));
        assertEquals(StrategyType.LRU, StrategyType.valueOf("LRU"));
        assertEquals(StrategyType.MRU, StrategyType.valueOf("MRU"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenTypeIsNotCorrectTest() {
        StrategyType.valueOf("wrong_value");
    }
}
