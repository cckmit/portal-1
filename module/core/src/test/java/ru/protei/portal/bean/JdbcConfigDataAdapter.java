package ru.protei.portal.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.config.TestConfig;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.embeddeddb.EmbeddedDB;
import ru.protei.portal.embeddeddb.EmbeddedDBImpl;
import ru.protei.winter.jdbc.JdbcBeforeContextInitializer;
import ru.protei.winter.jdbc.config.JdbcConfigData;

public class JdbcConfigDataAdapter implements JdbcBeforeContextInitializer {

    public JdbcConfigDataAdapter( TestConfig testConfig ) {
        this.testConfig = testConfig;
    }

    @Override
    public void initialize(JdbcConfigData configData) {
        disableLiquibaseBeanCreationByWinter(configData);
        if(testConfig.data().embeddedDbEnabled && testConfig.data().isRandomPort){
            setPort(configData, testConfig.data().getPort());
        }
    }

    private void disableLiquibaseBeanCreationByWinter(JdbcConfigData configData) {
        for (JdbcConfigData.JdbcConnectionParam connection : CollectionUtils.emptyIfNull(configData.getConnections())) {
            connection.setLiquibaseEnabled(false);
        }
    }

    private void setPort(JdbcConfigData configData, int port) {
        for (JdbcConfigData.JdbcConnectionParam connection : CollectionUtils.emptyIfNull(configData.getConnections())) {
            connection.setUrl( connection.getUrl().replace( String.valueOf( EmbeddedDBImpl.DB_PORT ), String.valueOf(port)));
            log.info( "setPort(): Port replaced: {}", connection.getUrl() );
        }
    }

    private TestConfig testConfig;
    private static final Logger log = LoggerFactory.getLogger( JdbcConfigDataAdapter.class );
}
