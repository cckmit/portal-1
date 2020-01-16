package ru.protei.portal.ui.common.shared.model;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.dict.En_ResultStatus.PERMISSION_DENIED;


/**
 * Обрабока ошибок асинхронных серверных запросов
 */
public class DefaultErrorHandler implements Consumer<Throwable> {

    @Override
    public final void accept( Throwable throwable ) {
        if (!( throwable instanceof RequestFailedException )) {
            return;
        }

        RequestFailedException rf = (RequestFailedException) throwable;
        activity.fireEvent(new NotifyEvents.Show(lang.getMessage(rf.status), NotifyEvents.NotifyType.ERROR));
    }

    @Inject
    static En_ResultStatusLang lang;
    @Inject
    static NotifyActivity activity;
}
