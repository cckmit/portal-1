package ru.protei.portal.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ru.protei.portal.api.config.APIConfigurationContext;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int DEFAULT_PORT = 8090;
    private static final String MAPPING_URL = "/*";
    private static final String API_URL = "/api/*";

    public static void main(String[] args) {

        try {
            logger.debug("run portal-api");
            logger.debug("using port : " + DEFAULT_PORT);

            Server server = new Server(DEFAULT_PORT);

            WebAppContext webapp = new WebAppContext();
            webapp.setContextPath("/");
            webapp.setResourceBase("");

            AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
            context.register(APIConfigurationContext.class);

            webapp.addEventListener(new ContextLoaderListener(context));

            // SPRING servlet-dispatcher
            ServletHolder springHolder = new ServletHolder("dispatcher", new DispatcherServlet(context));
            springHolder.setInitOrder(2);
            webapp.addServlet(springHolder, MAPPING_URL);

            // CXF servlet-dispatcher
            //webapp.addServlet(new ServletHolder("cxf", new CXFServlet()), API_URL);

            server.setHandler(webapp);

            logger.debug("server created, starting");

            server.start();
            server.join();

        } catch (Exception e) {
            logger.debug("error", e);
        }
    }
}
