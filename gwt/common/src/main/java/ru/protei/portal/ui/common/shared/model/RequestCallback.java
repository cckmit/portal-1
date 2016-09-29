package ru.protei.portal.ui.common.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;


/**
 * Шаблонный класс для формирования асинхронных запросов
 */
public abstract class RequestCallback<T> implements AsyncCallback<T> {

    protected RequestCallback() {}

    @Override
    public final void onFailure( Throwable throwable ) {
        if ( throwable instanceof RequestFailedException ) {
            RequestFailedException rf = (RequestFailedException) throwable;
            // TODO: analyse error and show error msg
        }

        onError( throwable );
    }

    public abstract void onError( Throwable throwable );
}
