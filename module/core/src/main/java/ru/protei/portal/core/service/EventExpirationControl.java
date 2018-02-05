package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;

import static java.lang.System.currentTimeMillis;

public class EventExpirationControl {

    public boolean isExpired(AssembledCaseEvent event) {
        return currentTimeMillis() - event.getLastUpdated() >= config.data().eventAssemblyConfig().getWaitingPeriod();
    }

    @Autowired
    private PortalConfig config;
}
