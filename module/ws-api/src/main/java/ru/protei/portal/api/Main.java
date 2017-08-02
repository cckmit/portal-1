package ru.protei.portal.api;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ru.protei.portal.api.config.APIConfigurationContext;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class);
    private static final int DEFAULT_PORT = 8090;
    private static final String CONTEXT_PATH = "/";
    private static final String MAPPING_URL = "/*";
    private static final String API_URL = "/api/*";

    public static void main(String[] args) {

        Server server = null;
        try {

            int port = args.length > 1 ? Integer.parseInt(args[0],10) : DEFAULT_PORT;

            logger.debug("run portal-api");
            logger.debug("using port : " + port);

            server = new Server(DEFAULT_PORT);

            WebAppContext webapp = new WebAppContext();
            webapp.setContextPath(CONTEXT_PATH);
            webapp.setDescriptor("module/ws-api/src/main/webapp/WEB-INF/web.xml");
            webapp.setResourceBase("module/ws-api/src/main/webapp");

            AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
            context.register(APIConfigurationContext.class);

            webapp.addEventListener(new ContextLoaderListener(context));

            // SPRING servlet-dispatcher
            ServletHolder springHolder = springServletHolder(context);
            webapp.addServlet(springHolder, MAPPING_URL);

            // CXF servlet-dispatcher
            webapp.addServlet(new ServletHolder("cxf", new CXFServlet()), API_URL);

            server.setHandler(webapp);

            logger.debug("server created, starting");

            server.start();
            server.join();


        } catch (Exception e) {
            logger.debug("error", e);
        }
    }

    private static ServletHolder springServletHolder (WebApplicationContext context) {
        DispatcherServlet servlet = new DispatcherServlet(context);
        ServletHolder springHolder = new ServletHolder(servlet);
        springHolder.setInitOrder(2);
        return springHolder;
    }
}
