package ru.protei.portal.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by turik on 07.09.16.
 */
public class WSConfig {

    private static Logger logger = LoggerFactory.getLogger(WSConfig.class);

    private String dirPhotos;

    private boolean isEnableMigration = false;

    private static WSConfig m_Instance = new WSConfig ();
    public static final WSConfig getInstance() { return m_Instance; }

    public WSConfig () {

        InputStream is = null;

        try {

            is = WSConfig.class.getResourceAsStream("/service.properties");
            Properties props = new Properties();
            props.load(is);
            dirPhotos = props.getProperty ("dir_photos");
            isEnableMigration = new Boolean(props.getProperty("enable_migration"));

            logger.debug("dirPhotos = " + dirPhotos);
            logger.debug("isEnableMigration = " + isEnableMigration);

        } catch (Exception e) {
            logger.error ("Can not read config!", e);
        } finally {
            try {
                is.close ();
            } catch (Exception e) {}
        }
    }

    public String getDirPhotos() {
        return dirPhotos;
    }

    public boolean isEnableMigration() {
        return isEnableMigration;
    }
}
