package ru.protei.portal.ui.common.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;


/**
 * Шаблонный класс для формирования асинхронных запросов
 */
public abstract class RequestCallback<T> implements AsyncCallback<T> {

    protected RequestCallback() {}

    @Override
    public final void onFailure( Throwable throwable ) {
        defaultErrorHandler.accept(throwable);

        onError( throwable );
    }

    public abstract void onError( Throwable throwable );

    @Inject
    static DefaultErrorHandler defaultErrorHandler;
}
