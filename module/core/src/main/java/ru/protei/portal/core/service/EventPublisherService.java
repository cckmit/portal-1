package ru.protei.portal.core.service;

import org.springframework.context.ApplicationEvent;

/**
 * Created by michael on 04.05.17.
 */
public interface EventPublisherService {

    void publishEvent(ApplicationEvent event);
}
