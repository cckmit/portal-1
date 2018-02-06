package ru.protei.portal.hpsm.handlers;

import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.ServiceInstance;

/**
 * Created by michael on 28.04.17.
 */
public interface HpsmEventHandler {
    void handle(HpsmEvent request, ServiceInstance instance) throws Exception;

}
