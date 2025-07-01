package ru.otus.cachehw;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.service.DbServiceClientImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(MyCache.class);
    private final Map<K, V> cache = new WeakHashMap<>();
    private final List<HwListener<K, V>> listeners = new ArrayList<>();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        //log.info("Добавлено значение в кэш с идентификатором {}", key);
        notifyListener(key, value, String.format("Add new value %s with key %s", value, key));
    }

    @Override
    public void remove(K key) {
        if (cache.containsKey(key)) {
            notifyListener(key, cache.get(key), String.format("Remove value %s with key %s", cache.get(key), key));
            cache.remove(key);
        }
    }

    @Override
    public V get(K key) {
        log.info("Запрос в кэш для получения значения с идентификатором {}. Значение {}", key, cache.get(key));
        //log.info("Размер cache {}", cache.size());
        return cache.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void notifyListener(K key, V value, String action) {
        for (HwListener<K, V> listener : listeners) {
            listener.notify(key, value, action);
        }
    }

    public int size() {
        return cache.size();
    }
}
