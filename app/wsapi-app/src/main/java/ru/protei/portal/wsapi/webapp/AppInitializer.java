package ru.protei.portal.wsapi.webapp;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.io.File;

/**
 * Created by Mike on 02.05.2017.
 */
public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) throws ServletException {

        //init log4j
        String log4jConfigFile = container.getInitParameter("log4j-config-location");
        String fullPath = container.getRealPath("") + File.separator + log4jConfigFile;
        PropertyConfigurator.configure(fullPath);

        LoggerFactory.getLogger(AppInitializer.class).debug("log4j is configured for context");

        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        webApplicationContext.register(AppSpringConfig.class);

        container.setAttribute("rootSpringContext", webApplicationContext);

        // Manage the lifecycle of the root application context
        container.addListener(new ContextLoaderListener(webApplicationContext));

        LoggerFactory.getLogger(AppInitializer.class).debug("init spring dispatcher servlet");



        ServletRegistration.Dynamic springServletDispatcher = container.addServlet("api",
                new DispatcherServlet(webApplicationContext)); // <-- hooray! Spring doesn't look for XML files!

        springServletDispatcher.setLoadOnStartup(2);
        springServletDispatcher.addMapping("/api/rest/*");

        LoggerFactory.getLogger(AppInitializer.class).debug("init spring dispatcher done");

        ServletRegistration.Dynamic cxfDispatcher = container.addServlet("CXF",
                new org.apache.cxf.transport.servlet.CXFServlet ());

        cxfDispatcher.setLoadOnStartup(2);
        cxfDispatcher.addMapping("/api/ws/*");

        LoggerFactory.getLogger(AppInitializer.class).debug("init CXF dispatcher done");

        LoggerFactory.getLogger(AppInitializer.class).debug("web-initializer, work completed");
    }
}
