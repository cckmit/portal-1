package ru.protei.portal.hpsm.logic;

import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.hpsm.struct.HpsmMessage;

/**
 * Created by michael on 15.05.17.
 */
public interface BackChannelEventHandler {
    void handle (CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) throws Exception;
}
