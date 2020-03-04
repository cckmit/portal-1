package ru.protei.portal.redmine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.config.TestPortalConfig;
import ru.protei.winter.core.utils.config.exception.ConfigException;

@Configuration
public class PortalConfigTestConfiguration {

    @Value("classpath:portal.properties")
    private Resource props;

    @Bean
    public PortalConfig getPortalConfig() throws ConfigException {
        return new TestPortalConfig(props);
    }

}

