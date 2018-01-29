package ru.protei.portal.ui.crm.server.init;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        //init log4j

        String log4j_location = servletContext.getInitParameter("log4j-config-location");
        if (log4j_location == null) {
            log4j_location = "WEB-INF/classes/log4j.properties";
        }

        String fullPath = servletContext.getRealPath("") + File.separator + log4j_location;
        if (new File(fullPath).exists()) {
            PropertyConfigurator.configure(fullPath);
            LoggerFactory.getLogger(WebAppInitializer.class).debug("log4j is configured for context {}", servletContext.getContextPath());
        }
        else {
            System.out.println("unable to configure log4j, no file at : " + fullPath);
        }
    }
}
