package com.ericsson.common.util;

import com.ericsson.common.util.function.FunctionalUtil;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author delma
 */
public enum LegacyUtil {

    /**
     * Singleton.
     */
    INSTANCE;

    public static <K, V> Dictionary<K, V> toDictionary(Map<K, V> map) {
        return FunctionalUtil.applyIfCan(DictionaryWrapper.class, map, m -> m.dictionary)
                .orElseGet(() -> new MapWrapper(map));
    }

    public static <K, V> Map<K, V> toMap(Dictionary dictionary) {
        return FunctionalUtil.applyIfCan(MapWrapper.class, dictionary, d -> d.map)
                .orElseGet(() -> new DictionaryWrapper(dictionary));
    }

    private static class MapWrapper<K, V> extends Dictionary<K, V> {

        private final Map<K, V> map;

        MapWrapper(Map<K, V> map) {
            this.map = map;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public Enumeration<K> keys() {
            return Collections.enumeration(map.keySet());
        }

        @Override
        public Enumeration<V> elements() {
            return Collections.enumeration(map.values());
        }

        @Override
        public V get(Object key) {
            return map.get(key);
        }

        @Override
        public V put(K key, V value) {
            return map.put(key, value);
        }

        @Override
        public V remove(Object key) {
            return map.remove(key);
        }
    }

    private static class DictionaryWrapper<K, V> extends AbstractMap<K, V> {

        private final Dictionary dictionary;

        DictionaryWrapper(Dictionary dictionary) {
            this.dictionary = dictionary;
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return new AbstractSet<Map.Entry<K, V>>() {

                @Override
                public Iterator<Map.Entry<K, V>> iterator() {
                    return new Iterator<Map.Entry<K, V>>() {
                        Enumeration enumeration = dictionary.keys();

                        @Override
                        public boolean hasNext() {
                            return enumeration.hasMoreElements();
                        }

                        @Override
                        public Map.Entry<K, V> next() {
                            return new Map.Entry<K, V>() {
                                Object key = enumeration.nextElement();
                                Object value = dictionary.get(key);

                                @Override
                                public K getKey() {
                                    return (K) key;
                                }

                                @Override
                                public V getValue() {
                                    return (V) value;
                                }

                                @Override
                                public V setValue(V value) {
                                    return (V) dictionary.put(key, value);
                                }

                            };
                        }

                    };
                }

                @Override
                public int size() {
                    return dictionary.size();
                }

            };
        }
    }
}
