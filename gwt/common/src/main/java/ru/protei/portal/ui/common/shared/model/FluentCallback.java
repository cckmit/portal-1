package ru.protei.portal.ui.common.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.events.NotifyEvents;

import java.util.function.Consumer;

/**
 * Либо сообщение о ошибке, либо обработчик ошибки.
 * Если сообщение и обработик не заданы ошибка обрабатывается обработчиком ошибки по умолчанию.
 */
public class FluentCallback<T> implements MessageOnError<T>, HandleOnError<T>
{

    private String errorMessage = null;
    private NotifyEvents.NotifyType notifyType = NotifyEvents.NotifyType.ERROR;
    private Consumer<Throwable> errorHandler = null;
    private Consumer<T> successHandler = null;

    @Override
    public MessageOnError<T> withErrorMessage(String errorMessage) {
        withErrorMessage(errorMessage, NotifyEvents.NotifyType.ERROR);
        return this;
    }

    @Override
    public MessageOnError<T> withErrorMessage(String errorMessage, NotifyEvents.NotifyType type) {
        this.errorMessage = errorMessage;
        this.notifyType = type;
        return this;
    }

    @Override
    public HandleOnError<T> withErrorHandler(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    @Override
    public AsyncCallback<T> withSuccess(Consumer<T> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    @Override
    public final void onFailure(Throwable throwable) {

        if (errorHandler != null) {
            errorHandler.accept(throwable);
            return;
        }

        if (errorMessage != null) {
            notificationHandler.accept(errorMessage, notifyType);
        } else {
            defaultErrorHandler.accept(throwable);
        }

    }

    @Override
    public final void onSuccess(T result) {
        if (successHandler != null) {
            successHandler.accept(result);
        }
    }

    @Inject
    static DefaultErrorHandler defaultErrorHandler;
    @Inject
    static DefaultNotificationHandler notificationHandler;
}
