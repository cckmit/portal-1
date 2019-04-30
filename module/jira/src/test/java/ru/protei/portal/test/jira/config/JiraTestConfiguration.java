package ru.protei.portal.test.jira.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.jira.config.JiraConfigurationContext;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

@Configuration
@Import({CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class, JiraConfigurationContext.class})
public class JiraTestConfiguration {
}
