package ru.protei.portal.config;

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PortalConfigData data() {
        return portalConfigData;
    }
}

