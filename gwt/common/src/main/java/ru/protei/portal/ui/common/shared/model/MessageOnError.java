package ru.protei.portal.ui.common.shared.model;

import ru.protei.portal.ui.common.client.events.NotifyEvents;

public interface MessageOnError<T> extends HandleOnSuccess<T>{
    MessageOnError<T> withErrorMessage(String errorMessage);

    MessageOnError<T> withErrorMessage(String errorMessage, NotifyEvents.NotifyType type);
}
