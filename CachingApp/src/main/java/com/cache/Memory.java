package com.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


class Memory<K extends Serializable, V extends Serializable> implements ProxyCache<K, V> {
    private final Map<K, V> objectsStorage;
    private final int capacity;

    Memory(int capacity) {
        this.capacity = capacity;
        this.objectsStorage = new ConcurrentHashMap<>(capacity);
    }

    @Override
    public V get(K key) {
        return objectsStorage.get(key);
    }

    @Override
    public void put(K key, V value) {
        objectsStorage.put(key, value);
    }

    @Override
    public void remove(K key) {
        objectsStorage.remove(key);
    }

    @Override
    public int getSize() {
        return objectsStorage.size();
    }

    @Override
    public boolean isObjectPresent(K key) {
        return objectsStorage.containsKey(key);
    }

    @Override
    public boolean hasEmptyPlace() {
        return getSize() < this.capacity;
    }

    @Override
    public void clear() {
        objectsStorage.clear();
    }
}
