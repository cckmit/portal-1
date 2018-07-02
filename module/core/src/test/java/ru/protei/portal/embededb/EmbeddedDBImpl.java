package ru.protei.portal.embededb;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.Charset;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.SchemaConfig;
import liquibase.exception.LiquibaseException;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.SpringLiquibaseAdapter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.config.SchemaConfig.aSchemaConfig;
import static com.wix.mysql.distribution.Version.v5_7_19;

public class EmbeddedDBImpl implements EmbeddedDB {

    @Autowired
    SpringLiquibaseAdapter liquibase;

    static EmbeddedMysql mysqld;

    private final  MysqldConfig config = aMysqldConfig(v5_7_19)
            .withCharset(Charset.UTF8)
            .withPort(33062)
            .withUser("sa", "")
            .withServerVariable("lower_case_table_names", 1)
            .build();

    private final  SchemaConfig schemaConfig = aSchemaConfig("portal_test").build();

    @PostConstruct
    public void onInit() {
        mysqld = anEmbeddedMysql(config).addSchema(schemaConfig).start();
    }

    @PreDestroy
    public void onShutdown() {
        mysqld.dropSchema(schemaConfig);
        mysqld.stop();
    }

    @Override
    public void reloadSchema() {
        mysqld.reloadSchema(schemaConfig);
        try {
            liquibase.reloadSchema();//Заполняется ликвибейзом десятки секунд
        } catch (LiquibaseException e) {
            e.printStackTrace();
        }
    }
}
