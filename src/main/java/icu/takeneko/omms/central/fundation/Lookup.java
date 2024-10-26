package icu.takeneko.omms.central.fundation;

public interface Lookup<K, V> {
    V get(K key);
}
