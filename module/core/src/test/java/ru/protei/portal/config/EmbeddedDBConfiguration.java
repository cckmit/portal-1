package ru.protei.portal.config;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import ru.protei.portal.embededb.EmbeddedDB;
import ru.protei.portal.embededb.EmbeddedDBImpl;

import javax.sql.DataSource;


@Configuration
@PropertySource("classpath:winter.properties")
public class EmbeddedDBConfiguration {

    @Autowired
    Environment environment;

    @Bean
    public DataSource getDataSource() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setUrl( environment.getProperty( "jdbc.url", "jdbc:mysql://127.0.0.1:33062/portal_test?characterEncoding=utf-8&useUnicode=true&useSSL=false" ) );
        dataSource.setUser( environment.getProperty( "jdbc.user", "sa" ) );
        dataSource.setPassword( environment.getProperty( "jdbc.password", "" ) );

        return dataSource;
    }

    @Bean
    public SpringLiquibaseAdapter createLiquibase(DataSource dataSource ) {
        SpringLiquibaseAdapter liquibase = new SpringLiquibaseAdapter();
        liquibase.setDataSource( dataSource );
        liquibase.setChangeLog( "classpath:liquibase/changelog.xml" );
        return liquibase;
    }

    @Bean
    public EmbeddedDB getEmbeddedDB ()  {
        /* turn on/of
        return new EmbeddedDBImpl();
        /*/
        return null;
        //*/
    }

}
