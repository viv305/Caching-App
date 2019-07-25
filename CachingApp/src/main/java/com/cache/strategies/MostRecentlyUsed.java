package com.cache.strategies;

public class MostRecentlyUsed<K> extends CacheStrategy<K> {
    @Override
    public void putObject(K key) {
        getObjectsStorage().put(key, System.nanoTime());
    }

    @Override
    public K getReplacedKey() {
        getSortedObjectsStorage().putAll(getObjectsStorage());
        return getSortedObjectsStorage().lastKey();
    }
}
