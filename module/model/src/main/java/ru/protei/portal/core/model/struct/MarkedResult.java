package ru.protei.portal.core.model.struct;

import java.io.Serializable;

public class MarkedResult<T> implements Serializable {

    long marker;
    T data;

    public MarkedResult() {}

    public MarkedResult( long marker, T data ) {
        this.marker = marker;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public long getMarker() {
        return marker;
    }
}
