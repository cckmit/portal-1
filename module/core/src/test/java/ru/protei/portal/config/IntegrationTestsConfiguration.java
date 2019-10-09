package ru.protei.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        PortalConfigTestConfiguration.class,
        ServiceTestsConfiguration.class,
        DaoTestsConfiguration.class})
public class IntegrationTestsConfiguration {

}
