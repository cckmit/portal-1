package ru.protei.portal.ui.common.shared.model;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.events.NotifyEvents;

import java.util.function.BiConsumer;


/**
 * Обработка(отправка) нотификаций
 */
public class DefaultNotificationHandler implements BiConsumer<String, NotifyEvents.NotifyType> {

    @Override
    public final void accept( String message, NotifyEvents.NotifyType type ) {
        activity.fireEvent(new NotifyEvents.Show(message, type));
    }

    @Inject
    static NotifyActivity activity;
}
