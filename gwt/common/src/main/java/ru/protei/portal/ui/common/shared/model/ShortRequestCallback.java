package ru.protei.portal.ui.common.shared.model;

import ru.protei.portal.ui.common.client.events.NotifyEvents;

/**
 * Шаблонный класс для формирования асинхронных запросов
 * когда не обязательны Success и/или Failure обработчики
 * Возможно задать сообщение для неожиданных типов исключений при отсутствии обработчика Failure.
 */
public class ShortRequestCallback<T> extends RequestCallback<T> implements SuccessHandler<T>, ErrorHandler {

    private ErrorHandler errorHandler;
    private SuccessHandler<T> successHandler;
    private String errorMessage;

    public ShortRequestCallback() {
    }

    public void onSuccess(T result) {
        if (successHandler != null) {
            successHandler.onSuccess(result);
        }
    }

    public void onError(Throwable throwable) {
        if (errorHandler != null) {
            errorHandler.onError(throwable);
        } else {
            if (errorMessage == null) errorMessage = throwable.getMessage();
            activity.fireEvent(new NotifyEvents.Show(errorMessage, NotifyEvents.NotifyType.ERROR));
        }
    }

    public ShortRequestCallback<T> setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public ShortRequestCallback<T> setOnError(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public ShortRequestCallback<T> setOnSuccess(SuccessHandler<T> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

}
