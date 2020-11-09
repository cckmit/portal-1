package ru.protei.portal.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

public class ListSeparatorByFeatureIterator<T, FV> implements Iterator<List<T>> {
    private final Supplier<List<T>> getNextChunk;
    private final Function<T, FV> getFeatureValue;
    private List<T> chunk;
    private Integer pointer;
    private FV currentFeatureValue;
    private List<T> nextInfos;

    public ListSeparatorByFeatureIterator(Supplier<List<T>> getNextChunk, Function<T, FV> getFeatureValue) {
        this.getNextChunk = getNextChunk;
        this.getFeatureValue = getFeatureValue;
    }

    private void prepare(T t) {
        currentFeatureValue = getFeatureValue.apply(t);
        nextInfos = new ArrayList<>();
        nextInfos.add(t);
    }

    private boolean refreshData() {
        chunk = getNextChunk.get();
        if (chunk.isEmpty()) {
            return false;
        }
        pointer = 0;
        if (currentFeatureValue == null) {
            prepare(chunk.get(pointer++));
        }
        return true;
    }

    @Override
    public boolean hasNext() {
        if (chunk == null) {
            if (!refreshData()) {
                return false;
            }
        }

        if (chunk.isEmpty()) {
            return false;
        }

        while (true) {
            if (pointer == chunk.size()) {
                if (!refreshData()) {
                    return !nextInfos.isEmpty();
                }
            }

            if (currentFeatureValue == null) {
                prepare(chunk.get(pointer++));
                if (pointer == chunk.size()) {
                    if (!refreshData()) {
                        return true;
                    }
                }
            }

            T info = chunk.get(pointer);
            if (getFeatureValue.apply(info).equals(currentFeatureValue)) {
                nextInfos.add(info);
                pointer++;
            } else {
                return true;
            }
        }
    }

    @Override
    public List<T> next() {
        if (nextInfos.isEmpty()) {
            throw new NoSuchElementException();
        }
        currentFeatureValue = null;
        return nextInfos;
    }
}
