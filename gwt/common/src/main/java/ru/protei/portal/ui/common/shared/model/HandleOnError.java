package ru.protei.portal.ui.common.shared.model;

import java.util.function.Consumer;

public interface HandleOnError<T> extends HandleOnSuccess<T> {
    HandleOnError<T> withError(Consumer<Throwable> errorHandler);
}