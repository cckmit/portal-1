package ru.protei.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.protei.portal.core.service.events.AsyncEventPublisherService;
import ru.protei.portal.core.service.events.EventPublisherService;

import static org.mockito.Mockito.spy;

/**
 * Created by michael on 04.05.17.
 */
@Configuration
@EnableScheduling
public class TestEventConfiguration {

    @Bean
    public EventPublisherService getEventPublisherService() {
        return  spy(new AsyncEventPublisherService());
    }
}
