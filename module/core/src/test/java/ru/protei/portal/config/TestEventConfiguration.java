package ru.protei.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.protei.portal.test.event.EventHandlerRegistry;

/**
 * Created by michael on 04.05.17.
 */
@Configuration
@EnableScheduling
public class TestEventConfiguration {

    @Bean
    public EventHandlerRegistry testEventRegistry () {
        return new EventHandlerRegistry ();
    }
}
