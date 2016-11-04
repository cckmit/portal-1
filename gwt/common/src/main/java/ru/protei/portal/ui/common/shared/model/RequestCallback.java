package ru.protei.portal.ui.common.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
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
            activity.fireEvent(new NotifyEvents.Show(lang.getMessage(rf.status), NotifyEvents.NotifyType.ERROR));
        }

        onError( throwable );
    }

    public abstract void onError( Throwable throwable );

    @Inject
    static En_ResultStatusLang lang;
    @Inject
    static NotifyActivity activity;
}
