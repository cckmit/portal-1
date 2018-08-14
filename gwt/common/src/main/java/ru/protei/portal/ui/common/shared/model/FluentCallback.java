package ru.protei.portal.ui.common.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.function.Consumer;

public class FluentCallback<T> implements AsyncCallback<T> {

    private String errorMessage = null;
    private Consumer<Throwable> errorHandler = null;
    private Consumer<T> successHandler = null;

    public FluentCallback<T> withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public FluentCallback<T> withError(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public FluentCallback<T> withSuccess(Consumer<T> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    @Override
    public final void onFailure(Throwable throwable) {

        if (throwable instanceof RequestFailedException) {
            RequestFailedException rf = (RequestFailedException) throwable;
            activity.fireEvent(new NotifyEvents.Show(lang.getMessage(rf.status), NotifyEvents.NotifyType.ERROR));
        }

        if (errorMessage != null) {
            activity.fireEvent(new NotifyEvents.Show(errorMessage, NotifyEvents.NotifyType.ERROR));
        }

        if (errorHandler != null) {
            errorHandler.accept(throwable);
        }
    }

    @Override
    public final void onSuccess(T result) {
        if (successHandler != null) {
            successHandler.accept(result);
        }
    }

    @Inject
    static En_ResultStatusLang lang;
    @Inject
    static NotifyActivity activity;
}
