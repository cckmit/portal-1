package ru.protei.portal.hpsm.factories;

import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.handlers.HpsmStatusHandler;
import ru.protei.portal.hpsm.struct.HpsmMessage;

public interface HpsmStatusHandlerFactory {
    HpsmStatusHandler createHandler(HpsmMessage msg, HpsmStatus newStatus);
}
