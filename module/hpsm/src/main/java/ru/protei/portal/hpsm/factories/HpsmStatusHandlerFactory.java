package ru.protei.portal.hpsm.factories;

import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.handlers.HpsmStatusHandler;

public interface HpsmStatusHandlerFactory {
    HpsmStatusHandler createHandler(HpsmStatus oldStatus, HpsmStatus newStatus);
}
