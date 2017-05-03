package ru.protei.portal.wsapi.webapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.wsapi.WSAPIConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by Mike on 02.05.2017.
 */
@Configuration
@EnableWebMvc
@Import({CoreConfigurationContext.class,JdbcConfigurationContext.class,MainConfiguration.class, WSAPIConfiguration.class})
public class AppSpringConfig {
}
