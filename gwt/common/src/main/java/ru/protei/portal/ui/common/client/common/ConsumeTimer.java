package ru.protei.portal.ui.common.client.common;

import com.google.gwt.user.client.Timer;

import java.util.function.Consumer;

public abstract class ConsumeTimer<T> extends Timer implements Consumer<T> {
    private T object;

    @Override
    public void run() {
        accept(object);
    }

    public synchronized void setObject(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }
}
