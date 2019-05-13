package ru.protei.portal.ui.common.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.events.NotifyEvents;

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
    private BiConsumer<T, Long> successHandler = null;
    private Runnable resultHandler = null;
    private Long marker = null;

    /**
     * Обработчик, который будет вызван при любом ответе сервера
     * Установленный обработчик будет вызван до любого другого обработчика и не отменит их обработку
     * @param resultHandler
     */
    public FluentCallback<T> withResult(Runnable resultHandler) {
        this.resultHandler = resultHandler;
        return this;
    }

    public FluentCallback<T> withMarker(long marker) {
        this.marker = marker;
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
    public AsyncCallback<T> withSuccess(BiConsumer<T, Long> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    @Override
    public final void onFailure(Throwable throwable) {

        if (resultHandler != null) {
            resultHandler.run();
        }

        if (errorHandler != null) {
            errorHandler.accept(throwable);
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

        if (resultHandler != null) {
            resultHandler.run();
        }

        if (successHandler != null) {
            successHandler.accept(result, marker);
        }
    }

    @Inject
    static DefaultErrorHandler defaultErrorHandler;
    @Inject
    static DefaultNotificationHandler notificationHandler;
}
