package ru.protei.portal.ui.common.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.function.BiConsumer;

public interface HandleOnSuccess<T> extends AsyncCallback<T> {
    AsyncCallback<T> withSuccess(BiConsumer<T, Long> successHandler);
}
