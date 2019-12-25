package ru.protei.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.core.utils.config.exception.ConfigException;

@Configuration
@Import({CoreConfigurationContext.class})
public class PortalConfigTestConfiguration {

    @Bean
    public PortalConfig getPortalConfig() throws ConfigException {
        return new PortalConfig("portal.properties");
    }

}
