package ru.protei.portal.test.hpsm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.hpsm.config.HpsmConfigurationContext;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by michael on 19.04.17.
 */
@Configuration
@Import({CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class, HpsmConfigurationContext.class})
public class HpsmTestConfiguration {
}
