package ru.protei.portal.embeddeddb;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.Charset;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.SchemaConfig;
import com.wix.mysql.distribution.Version;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.protei.portal.embeddeddb.TestConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class EmbeddedDBImpl implements EmbeddedDB, ApplicationContextAware {

    private static final String DB_SCHEMA_NAME = "portal_test";
    private int port = TestConfig.EmbeddedDB.DB_PORT;
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "sql";
    private static final SchemaConfig SCHEMA_CONFIG = SchemaConfig.aSchemaConfig(DB_SCHEMA_NAME).build();
    private static final Logger log = LoggerFactory.getLogger(EmbeddedDBImpl.class);
    private static boolean isInitialized = false;
    private static EmbeddedMysql mysqld;
    private static ApplicationContext context;
    @Autowired private TestConfig testConfig;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @PostConstruct
    public void onInit() {
        if (isInitialized) {
            log.error("Attempt to initialize already initialized EmbeddedDB");
            return;
        }
        isInitialized = true;
        if (testConfig.data().embeddedDbEnabled) {
            port = testConfig.data().getPort();
            mysqld = EmbeddedMysql.anEmbeddedMysql(buildConfig(port))
                    .addSchema(SCHEMA_CONFIG)
                    .start();
            log.info( "onInit(): Started on port={}", port );
        }
        applyLiquibase();
    }

    @PreDestroy
    public void onShutdown() {
        if (testConfig.data().embeddedDbEnabled) {
            mysqld.dropSchema(SCHEMA_CONFIG);
            mysqld.stop();
            log.info( "onShutdown(): Stopped on port={}", port );
        }
        isInitialized = false;
    }

    public int getPort(  ) {
        return port;
    }

    private void applyLiquibase() {
        // Ленивая инициализация ликвибейз бина - применение чейнджсетов в базе данных
        context.getBean(SpringLiquibase.class);
    }

    private MysqldConfig buildConfig(int port) {
        return MysqldConfig.aMysqldConfig(Version.v5_7_27)
                .withCharset(Charset.UTF8)
                .withPort(port)
                .withUser(DB_USERNAME, DB_PASSWORD)
                .withServerVariable("lower_case_table_names", 1)
                .build();
    }
}
