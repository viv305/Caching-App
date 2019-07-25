package com.cache.strategies;

public class LeastRecentlyUsed<K> extends CacheStrategy<K> {
    @Override
    public void putObject(K key) {
        getObjectsStorage().put(key, System.nanoTime());
    }
}
