package com.cache;

interface ProxyCache<K, V> {
    void put(K key, V value);

    V get(K key);

    void remove(K key);

    void clear();
    
    int getSize();

    boolean isObjectPresent(K key);

    boolean hasEmptyPlace();
}
