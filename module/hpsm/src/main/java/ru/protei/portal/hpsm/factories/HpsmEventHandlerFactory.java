package ru.protei.portal.hpsm.factories;

import ru.protei.portal.hpsm.handlers.HpsmEventHandler;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.ServiceInstance;

public interface HpsmEventHandlerFactory {
    HpsmEventHandler createHandler (HpsmEvent request, ServiceInstance instance);
}
