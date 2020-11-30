package ru.protei.portal.ui.common.shared.model;

import ru.protei.portal.core.model.dict.En_ResultStatus;

import java.util.function.Consumer;

public interface HandleOnError<T> extends HandleOnSuccess<T> {
    HandleOnError<T> withError(Consumer<Throwable> errorHandler);

    HandleOnError<T> withError(CustomConsumer errorHandler);

    interface CustomConsumer {
        void accept(Throwable throwable, DefaultErrorHandler defaultErrorHandler, En_ResultStatus status);
    }
}
