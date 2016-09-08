package ru.protei.portal.webui.controller.ws;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by turik on 07.09.16.
 */
public class WSConfig {

    private static Logger logger = Logger.getLogger(WSConfig.class);

    private String dirPhotos;

    private static WSConfig m_Instance = new WSConfig ();
    public static final WSConfig getInstance() { return m_Instance; }

    public WSConfig () {

        InputStream is = null;

        try {

            is = WSConfig.class.getResourceAsStream("/service.properties");
            Properties props = new Properties();
            props.load(is);
            dirPhotos = props.getProperty ("dir_photos");

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
}
