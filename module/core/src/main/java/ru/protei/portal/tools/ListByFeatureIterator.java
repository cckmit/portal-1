package ru.protei.portal.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

public class ListByFeatureIterator<T, FV> implements Iterator<List<T>> {
    private final Supplier<List<T>> getNextChunk;
    private final Function<T, FV> getFeatureValue;
    private List<T> chunk;
    private Integer pointer;
    private FV currentFeatureValue;
    private List<T> next;
    private List<T> tempNext;

    public ListByFeatureIterator(Supplier<List<T>> getNextChunk, Function<T, FV> getFeatureValue) {
        this.getNextChunk = getNextChunk;
        this.getFeatureValue = getFeatureValue;
    }

    private void refreshChunk() {
        chunk = getNextChunk.get();
        pointer = 0;
    }

    private void refreshData(T t, FV fv) {
        currentFeatureValue = fv;
        tempNext = new ArrayList<>();
        tempNext.add(t);
    }

    private void cutList() {
        next = tempNext;
    }

    private boolean isLastHasNextFalse() {
        return chunk != null && chunk.isEmpty();
    }

    private boolean isNoChunkOrChunkProcessed() {
        return chunk == null || pointer == chunk.size();
    }

    private T getT() {
        if (isLastHasNextFalse()) {
            return null;
        }

        if (isNoChunkOrChunkProcessed()) {
            refreshChunk();
            if (chunk == null || chunk.isEmpty()) {
                return null;
            }
        }

        return chunk.get(pointer++);
    }

    @Override
    public boolean hasNext() {
        T t;
        if (currentFeatureValue == null) {
            t = getT();
            if (t != null) {
                refreshData(t, getFeatureValue.apply(t));
            } else {
                return false;
            }
        }

        while (true) {
            t = getT();
            if (t == null) {
                if (tempNext.isEmpty()) {
                    return false;
                } else {
                    cutList();
                    tempNext = new ArrayList<>();
                    return true;
                }
            }

            final FV featureValue = getFeatureValue.apply(t);
            if (featureValue.equals(currentFeatureValue)) {
                tempNext.add(t);
            } else {
                cutList();
                refreshData(t, featureValue);
                return true;
            }
        }
    }

    @Override
    public List<T> next() {
        if (next == null || next.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<T> next = new ArrayList<>(this.next);
        this.next = null;
        return next;
    }
}
