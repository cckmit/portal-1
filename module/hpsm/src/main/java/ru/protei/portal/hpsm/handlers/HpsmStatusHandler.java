package ru.protei.portal.hpsm.handlers;

import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.ServiceInstance;

public interface HpsmStatusHandler {
    void handle(HpsmEvent request, ServiceInstance instance) throws Exception;
}
