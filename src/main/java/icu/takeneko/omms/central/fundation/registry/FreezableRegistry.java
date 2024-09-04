package icu.takeneko.omms.central.fundation.registry;

public interface FreezableRegistry<I, T> extends MutableRegistry<I, T> {
    void frozen();
}
