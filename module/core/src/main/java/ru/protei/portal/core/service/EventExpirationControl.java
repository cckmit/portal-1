package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;

import static java.lang.System.currentTimeMillis;

public class EventExpirationControl {

    public boolean isExpired(AssembledCaseEvent event) {
        //casting millis to seconds
        return currentTimeMillis() / 1000 - event.getLastUpdated() >= config.data().eventAssemblyConfig().getWaitingPeriod();
    }

    @Autowired
    private PortalConfig config;
}
