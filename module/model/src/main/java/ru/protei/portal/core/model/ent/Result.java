package ru.protei.portal.core.model.ent;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private T data;
    private String message;

    public Result() {}

    public Result(T data, String message) {
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
