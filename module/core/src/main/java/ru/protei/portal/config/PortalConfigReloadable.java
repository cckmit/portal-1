package ru.protei.portal.config;

import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.config.impl.AbstractPropertiesReloadableConfig;
import ru.protei.winter.core.utils.config.utils.PropertiesWrapper;

/**
 * Created by michael on 31.05.17.
 */
public class PortalConfigReloadable extends AbstractPropertiesReloadableConfig<PortalConfigData> implements PortalConfig
 {

    public PortalConfigReloadable( String sourcePath) throws ConfigException {
        super(sourcePath);
    }

    @Override
    protected PortalConfigData createConfigData(PropertiesWrapper propertiesWrapper) throws ConfigException {
        return new PortalConfigData(propertiesWrapper);
    }
}
