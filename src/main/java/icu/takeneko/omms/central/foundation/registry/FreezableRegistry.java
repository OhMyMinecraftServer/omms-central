package icu.takeneko.omms.central.foundation.registry;

public interface FreezableRegistry<I, T> extends MutableRegistry<I, T> {
    void frozen();
}
