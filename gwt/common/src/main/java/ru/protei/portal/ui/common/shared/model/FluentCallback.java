package ru.protei.portal.ui.common.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Либо сообщение о ошибке, либо обработчик ошибки.
 * Если и сообщение и обработик не заданы ошибка обрабатывается обработчиком ошибки по умолчанию.
 */
public class FluentCallback<T> implements MessageOnError<T>, HandleOnError<T>
{

    private String errorMessage = null;
    private NotifyEvents.NotifyType notifyType = NotifyEvents.NotifyType.ERROR;
    private Consumer<Throwable> errorHandler = null;
    private CustomConsumer customErrorHandler = null;
    private Consumer<T> successHandler = null;
    private long marker;
    private BiConsumer<Long, T> markedSuccessHandler = null;

    public FluentCallback<T> withMarkedSuccess(long marker, BiConsumer<Long, T> markedSuccessHandler) {
        this.marker = marker;
        this.markedSuccessHandler = markedSuccessHandler;
        return this;
    }

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
    public HandleOnError<T> withError(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    @Override
    public HandleOnError<T> withError(CustomConsumer errorHandler) {
        this.customErrorHandler = errorHandler;
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

        if (customErrorHandler != null) {
            customErrorHandler.accept(throwable, defaultErrorHandler, getStatus(throwable));
            return;
        }

        if (errorMessage != null) {
            notificationHandler.accept(errorMessage, notifyType);
            return;
        }

        defaultErrorHandler.accept(throwable);
    }

    @Override
    public final void onSuccess(T result) {

        if (markedSuccessHandler != null) {
            markedSuccessHandler.accept(marker, result);
            return;
        }

        if (successHandler != null) {
            successHandler.accept(result);
        }
    }

    private En_ResultStatus getStatus(Throwable throwable) {
        if (!(throwable instanceof RequestFailedException)) {
            return null;
        }

        return ((RequestFailedException) throwable).status;
    }

    @Inject
    static DefaultErrorHandler defaultErrorHandler;
    @Inject
    static DefaultNotificationHandler notificationHandler;
}
