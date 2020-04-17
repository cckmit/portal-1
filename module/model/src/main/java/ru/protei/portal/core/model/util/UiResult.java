package ru.protei.portal.core.model.util;

import java.io.Serializable;

public class UiResult<T> implements Serializable {
    private T data;
    private String message;

    public UiResult() {}

    public UiResult(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
