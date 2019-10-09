package ru.protei.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ServiceTestsConfiguration.class,
        DaoTestsConfiguration.class})
public class MainTestsConfiguration {

}
