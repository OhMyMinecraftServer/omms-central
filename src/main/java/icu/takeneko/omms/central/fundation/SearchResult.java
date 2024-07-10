package icu.takeneko.omms.central.fundation;

import org.jetbrains.annotations.NotNull;

public class SearchResult<T> implements Comparable<SearchResult<T>> {
    private final T result;
    private final int ratio;

    public SearchResult(T result, int ratio) {
        this.result = result;
        this.ratio = ratio;
    }

    @Override
    public int compareTo(@NotNull SearchResult<T> o) {
        return this.ratio - o.ratio;
    }

    public T getResult() {
        return result;
    }

    public int getRatio() {
        return ratio;
    }
}
