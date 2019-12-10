package ru.protei.portal.test.jira.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import ru.protei.portal.test.jira.embeddeddb.EmbeddedDB;
import ru.protei.portal.test.jira.embeddeddb.EmbeddedDBImpl;
import ru.protei.portal.test.jira.bean.JdbcConfigDataAdapter;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.jdbc.WinterLiquibase;
import ru.protei.winter.jdbc.config.JdbcConfig;

import javax.sql.DataSource;

@Configuration
public class DatabaseTestConfiguration {

    private final static String WINTER_PROPERTIES_CONFIG = CoreConfigurationContext.WINTER_CONFIG;
    private final static String LIQUIBASE_CHANGELOG_PATH = "classpath:liquibase/changelog.xml";

    @Bean
    public TestConfig getTestConfig() {
        return new TestConfig(WINTER_PROPERTIES_CONFIG);
    }

    @Bean
    public JdbcConfig getJdbcConfig() throws ConfigException {
        return new JdbcConfig(WINTER_PROPERTIES_CONFIG);
    }

    @Bean
    public JdbcConfigDataAdapter getJdbcConfigDataAdapter() {
        return new JdbcConfigDataAdapter();
    }

    /**
     * Чейнджсеты ликвибейза применяются при инициализации его бина.
     * В нашем случае инициализируем бин вручную после разворачивания базы данных.
     */
    @Bean
    @Lazy
    public SpringLiquibase getSpringLiquibase(@Autowired DataSource dataSource) {
        SpringLiquibase springLiquibase = new WinterLiquibase();
        springLiquibase.setChangeLog(LIQUIBASE_CHANGELOG_PATH);
        springLiquibase.setDataSource(dataSource);
        springLiquibase.setShouldRun(true);
        springLiquibase.setBeanName(SpringLiquibase.class.getName());
        return springLiquibase;
    }

    @Bean
    public EmbeddedDB getEmbeddedDB() {
        return new EmbeddedDBImpl();
    }
}
