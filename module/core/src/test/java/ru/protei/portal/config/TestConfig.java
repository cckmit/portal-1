package ru.protei.portal.config;

import ru.protei.winter.core.utils.config.ConfigUtils;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.config.utils.PropertiesWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class TestConfig {

    private TestConfigData data;

    public TestConfig(String sourcePath) {
        try {
            URL path = ConfigUtils.locateFileOrDirectory(sourcePath);
            File configFile = ConfigUtils.extractFile(path);
            try (FileInputStream fileInputStream = new FileInputStream(configFile);
                 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream)) {
                data = new TestConfigData(new PropertiesWrapper(inputStreamReader));
            }
        } catch (ConfigException | IOException e) {
            e.printStackTrace();
        }
    }

    public TestConfigData data() {
        return data;
    }

    public class TestConfigData {

        public TestConfigData(PropertiesWrapper wrapper) throws ConfigException {
            embeddedDbEnabled = wrapper.getProperty("portal.db.embedded.enabled", Boolean.class, true);
        }

        public final boolean embeddedDbEnabled;
        public boolean isRandomPort = true;
    }
}