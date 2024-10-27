package icu.takeneko.omms.central.foundation.registry;

public record Holder<T>(
    T value,
    Identifier id
) {

}
