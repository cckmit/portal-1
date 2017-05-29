package ru.protei.portal.test.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.test.event.EventHandlerRegistry;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by michael on 04.05.17.
 */
@Configuration
@Import({CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class})
public class TestEventConfiguration {


    @Bean
    public EventHandlerRegistry testEventRegistry () {
        return new EventHandlerRegistry ();
    }

}
