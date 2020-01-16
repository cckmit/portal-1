package ru.protei.portal.bean;

import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.winter.jdbc.JdbcBeforeContextInitializer;
import ru.protei.winter.jdbc.config.JdbcConfigData;

public class JdbcConfigDataAdapter implements JdbcBeforeContextInitializer {

    @Override
    public void initialize(JdbcConfigData configData) {
        disableLiquibaseBeanCreationByWinter(configData);
    }

    private void disableLiquibaseBeanCreationByWinter(JdbcConfigData configData) {
        for (JdbcConfigData.JdbcConnectionParam connection : CollectionUtils.emptyIfNull(configData.getConnections())) {
            connection.setLiquibaseEnabled(false);
        }
    }

}
