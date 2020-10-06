package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;

import java.util.List;

public class ReservedIpAdminNotificationEvent extends ApplicationEvent {

    private final List<String> nonAvailableIps;

    public ReservedIpAdminNotificationEvent(Object source, List<String> nonAvailableIps) {
        super(source);
        this.nonAvailableIps = nonAvailableIps;
    }

    public List<String> getNonAvailableIps() {
        return nonAvailableIps;
    }
}
