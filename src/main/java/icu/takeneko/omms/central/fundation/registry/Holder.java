package icu.takeneko.omms.central.fundation.registry;

public record Holder<T>(
    T value,
    Identifier id
) {

}
