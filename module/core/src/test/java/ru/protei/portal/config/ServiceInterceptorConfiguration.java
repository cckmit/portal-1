package ru.protei.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.protei.portal.core.service.SmokeyService;

@Configuration
@EnableAspectJAutoProxy
public class ServiceInterceptorConfiguration {

    @Bean
    public SmokeyService getSmokeyService() {
        return new SmokeyService();
    }
}
