package ru.protei.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import ru.protei.portal.core.service.TemplateService;
import ru.protei.portal.core.service.TemplateServiceImpl;


@Configuration
@PropertySource("classpath:winter.properties")
public class TemplateConfiguration {

    @Autowired
    Environment environment;

    @Bean
    public TemplateService getTemplateService() {
        return new TemplateServiceImpl();

    }

}
