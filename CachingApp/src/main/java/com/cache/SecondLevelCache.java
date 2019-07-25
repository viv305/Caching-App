package com.cache;

import static java.lang.String.format;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cache.strategies.CacheStrategy;
import com.cache.strategies.LeastFrequentlyUsed;
import com.cache.strategies.LeastRecentlyUsed;
import com.cache.strategies.MostRecentlyUsed;
import com.cache.strategies.StrategyType;

public class SecondLevelCache<K extends Serializable, V extends Serializable> implements ProxyCache<K, V> {
	private static final Logger LOG = LoggerFactory.getLogger(SecondLevelCache.class);
	
    private final Memory<K, V> firstLevelCache;
    private final FileSystem<K, V> secondLevelCache;
    private final CacheStrategy<K> strategy;

    public SecondLevelCache(final int memoryCapacity, final int fileCapacity, final StrategyType strategyType) {
        this.firstLevelCache = new Memory<>(memoryCapacity);
        this.secondLevelCache = new FileSystem<>(fileCapacity);
        this.strategy = getStrategy(strategyType);
    }

    public SecondLevelCache(final int memoryCapacity, final int fileCapacity) {
        this.firstLevelCache = new Memory<>(memoryCapacity);
        this.secondLevelCache = new FileSystem<>(fileCapacity);
        this.strategy = getStrategy(StrategyType.LFU);
    }

    private CacheStrategy<K> getStrategy(StrategyType strategyType) {
        switch (strategyType) {
            case LRU:
                return new LeastRecentlyUsed<>();
            case MRU:
                return new MostRecentlyUsed<>();
            case LFU:
            default:
                return new LeastFrequentlyUsed<>();
        }
    }

    @Override
    public synchronized void put(K newKey, V newValue) {
        if (firstLevelCache.isObjectPresent(newKey) || firstLevelCache.hasEmptyPlace()) {
            LOG.debug(format("Put object with key %s to the 1st level", newKey));
            firstLevelCache.put(newKey, newValue);
            if (secondLevelCache.isObjectPresent(newKey)) {
                secondLevelCache.remove(newKey);
            }
        } else if (secondLevelCache.isObjectPresent(newKey) || secondLevelCache.hasEmptyPlace()) {
            LOG.debug(format("Put object with key %s to the 2nd level", newKey));
            secondLevelCache.put(newKey, newValue);
        } else {
            // Here we have full cache and have to replace some object with new one according to cache strategy.
            replaceObject(newKey, newValue);
        }

        if (!strategy.isObjectPresent(newKey)) {
            LOG.debug(format("Put object with key %s to strategy", newKey));
            strategy.putObject(newKey);
        }
    }

    private void replaceObject(K key, V value) {
        K replacedKey = strategy.getReplacedKey();
        if (firstLevelCache.isObjectPresent(replacedKey)) {
            LOG.debug(format("Replace object with key %s from 1st level", replacedKey));
            firstLevelCache.remove(replacedKey);
            firstLevelCache.put(key, value);
        } else if (secondLevelCache.isObjectPresent(replacedKey)) {
            LOG.debug(format("Replace object with key %s from 2nd level", replacedKey));
            secondLevelCache.remove(replacedKey);
            secondLevelCache.put(key, value);
        }
    }

    @Override
    public synchronized V get(K key) {
        if (firstLevelCache.isObjectPresent(key)) {
            strategy.putObject(key);
            return firstLevelCache.get(key);
        } else if (secondLevelCache.isObjectPresent(key)) {
            strategy.putObject(key);
            return secondLevelCache.get(key);
        }
        return null;
    }

    @Override
    public synchronized void remove(K key) {
        if (firstLevelCache.isObjectPresent(key)) {
            LOG.debug(format("Remove object with key %s from 1st level", key));
            firstLevelCache.remove(key);
        }
        if (secondLevelCache.isObjectPresent(key)) {
            LOG.debug(format("Remove object with key %s from 2nd level", key));
            secondLevelCache.remove(key);
        }
        strategy.removeObject(key);
    }

    @Override
    public int getSize() {
        return firstLevelCache.getSize() + secondLevelCache.getSize();
    }

    @Override
    public boolean isObjectPresent(K key) {
        return firstLevelCache.isObjectPresent(key) || secondLevelCache.isObjectPresent(key);
    }

    @Override
    public void clear() {
        firstLevelCache.clear();
        secondLevelCache.clear();
        strategy.clear();
    }

    @Override
    public synchronized boolean hasEmptyPlace() {
        return firstLevelCache.hasEmptyPlace() || secondLevelCache.hasEmptyPlace();
    }

	public Memory<K, V> getFirstLevelCache() {
		return firstLevelCache;
	}

	public FileSystem<K, V> getSecondLevelCache() {
		return secondLevelCache;
	}

	public CacheStrategy<K> getStrategy() {
		return strategy;
	}

}
