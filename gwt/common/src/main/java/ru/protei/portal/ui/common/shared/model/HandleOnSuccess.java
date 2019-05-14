package ru.protei.portal.ui.common.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.function.Consumer;

public interface HandleOnSuccess<T> extends AsyncCallback<T> {
    AsyncCallback<T> withSuccess(Consumer<T> successHandler);
}
