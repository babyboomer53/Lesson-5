package cse41321.containers;

public class Homework5 {

    /**
     * This class is intended for use with a hash table.
     *
     * @param <K>
     * @param <V>
     */
    static class KeyValuePair<K, V> {
        private K key;
        private V value;

        public KeyValuePair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }



}