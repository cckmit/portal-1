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
    private List<T> nextInfos;
    private List<T> tempNextInfos;

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
        tempNextInfos = new ArrayList<>();
        tempNextInfos.add(t);
    }

    private void cutList() {
        nextInfos = tempNextInfos;
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
                if (tempNextInfos.isEmpty()) {
                    return false;
                } else {
                    cutList();
                    tempNextInfos = new ArrayList<>();
                    return true;
                }
            }

            final FV featureValue = getFeatureValue.apply(t);
            if (featureValue.equals(currentFeatureValue)) {
                tempNextInfos.add(t);
            } else {
                cutList();
                refreshData(t, featureValue);
                return true;
            }
        }
    }

    @Override
    public List<T> next() {
        if (nextInfos == null || nextInfos.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<T> next = new ArrayList<>(nextInfos);
        nextInfos = null;
        return next;
    }
}
