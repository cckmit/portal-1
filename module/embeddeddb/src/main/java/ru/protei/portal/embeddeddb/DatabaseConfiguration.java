package ru.protei.portal.embeddeddb;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.jdbc.WinterLiquibase;
import ru.protei.winter.jdbc.config.JdbcConfig;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration implements ApplicationContextAware {

    private final static String WINTER_PROPERTIES_CONFIG = CoreConfigurationContext.WINTER_CONFIG;
    private final static String LIQUIBASE_CHANGELOG_PATH = "classpath:liquibase/changelog.xml";
    private static ApplicationContext context;

    @Override
    public void setApplicationContext( ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Bean
    public TestConfig getTestConfig() {
        return new TestConfig(WINTER_PROPERTIES_CONFIG);
    }

    @Bean
    public JdbcConfig getJdbcConfig() throws ConfigException {
        return new JdbcConfig(WINTER_PROPERTIES_CONFIG);
    }

    @Bean
    public JdbcConfigDataAdapter getJdbcConfigDataAdapter( @Autowired TestConfig testConfig ) {
        return new JdbcConfigDataAdapter( testConfig );
    }

    /**
     * Чейнджсеты ликвибейза применяются при инициализации его бина.
     * В нашем случае инициализируем бин вручную после разворачивания базы данных.
     */
    @Bean
    @Lazy
    public SpringLiquibase getSpringLiquibase(@Autowired DataSource dataSource) {
        log.info( "getSpringLiquibase(): Liquibase try start." );
        SpringLiquibase springLiquibase = new WinterLiquibase();
        springLiquibase.setChangeLog(LIQUIBASE_CHANGELOG_PATH);
        springLiquibase.setDataSource(dataSource);
        springLiquibase.setShouldRun(true);
        springLiquibase.setBeanName(SpringLiquibase.class.getName());
        return springLiquibase;
    }

    @Bean
    public EmbeddedDB getEmbeddedDB(@Autowired TestConfig testConfig) {
        if (testConfig.data().embeddedDbEnabled) {
            return new EmbeddedDBImpl();
        }
        log.info( "getEmbeddedDB(): EmbeddedDB don`t used." );
        context.getBean(SpringLiquibase.class);
        return new EmbeddedDBImplStub();
    }

    private static final Logger log = LoggerFactory.getLogger( DatabaseConfiguration.class );
}

class EmbeddedDBImplStub implements EmbeddedDB{
    //  EmbeddedDB don`t used.
}
