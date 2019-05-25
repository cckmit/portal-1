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
    private Consumer<T> successHandler = null;
    private Runnable resultHandler = null;
    private long marker;
    private BiConsumer<Long, T> markedSuccessHandler = null;

    /**
     * Обработчик, который будет вызван при любом ответе сервера
     * Установленный обработчик будет вызван до любого другого обработчика и не отменит их обработку
     * @param resultHandler
     */
    public FluentCallback<T> withResult(Runnable resultHandler) {
        this.resultHandler = resultHandler;
        return this;
    }

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
    public AsyncCallback<T> withSuccess(Consumer<T> successHandler) {
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

        if (markedSuccessHandler != null) {
            markedSuccessHandler.accept(marker, result);
            return;
        }

        if (successHandler != null) {
            successHandler.accept(result);
        }
    }

    @Inject
    static DefaultErrorHandler defaultErrorHandler;
    @Inject
    static DefaultNotificationHandler notificationHandler;
}
