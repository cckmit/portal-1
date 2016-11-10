package ru.protei.portal.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.protei.portal.core.service.SmokeyService;

/**
 * Created by Mike on 06.11.2016.
 */
@EnableAspectJAutoProxy
@Configuration
public class TestConfiguration {

    @Bean
    public SmokeyService getSmokeyService () {
        return new SmokeyService();
    }
}
