package ru.protei.portal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.config.utils.PropertiesWrapper;

import java.io.*;

/**
 *
 */
public class TestPortalConfig implements PortalConfig {

    PortalConfigData portalConfigData;

    public TestPortalConfig(Resource resource) {
        try {
            FileInputStream fileInputStream = new FileInputStream(resource.getFile());
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            portalConfigData = new PortalConfigData(new PropertiesWrapper(inputStreamReader));
        } catch (ConfigException e) {
            e.printStackTrace();
            log.error( "TestPortalConfig(): Can't load test portal properties", e );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error( "TestPortalConfig(): Can't load test portal properties. File not found.", e );
        } catch (IOException e) {
            e.printStackTrace();
            log.error( "TestPortalConfig(): Can't load test portal properties", e );
        }
    }

    @Override
    public PortalConfigData data() {
        return portalConfigData;
    }

    private static final Logger log = LoggerFactory.getLogger( TestPortalConfig.class );
}

