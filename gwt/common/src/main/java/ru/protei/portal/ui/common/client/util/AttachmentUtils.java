package ru.protei.portal.ui.common.client.util;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.HandleOnError;

public class AttachmentUtils {
    public static HandleOnError.CustomConsumer getRemoveErrorHandler(Activity activity, Lang lang) {
        return (throwable, defaultErrorHandler, status) -> {
            if (En_ResultStatus.NOT_FOUND.equals(status)) {
                activity.fireEvent(new NotifyEvents.Show(lang.fileNotFoundError(), NotifyEvents.NotifyType.ERROR));
                return;
            }

            if (En_ResultStatus.NOT_REMOVED.equals(status)) {
                activity.fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR));
                return;
            }

            defaultErrorHandler.accept(throwable);
        };
    }
}
