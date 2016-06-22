package ru.protei.portal.webui.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ru.protei.portal.core.model.ent.Company;

import java.io.IOException;


public class Main {

    private static final int DEFAULT_PORT = 8090;
    private static final String CONTEXT_PATH = "/";
    private static final String CONFIG_LOCATION = "ru.protei.portal.webui.app";
    private static final String MAPPING_URL = "/*";
    private static final String DEFAULT_PROFILE = "dev";

    public static void main(String[] args) {
        Server server = null;
        try {
            Company company = new Company();
            System.out.println(company);

            server = createServer(DEFAULT_PORT);
            server.start();
            server.join();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected static Server createServer (int port) throws Exception {
        Server server = new Server(port > 0 ? port : DEFAULT_PORT);
        server.setHandler(getServletContextHandler(getContext()));
        return server;
    }


    private static ServletContextHandler getServletContextHandler(WebApplicationContext context) throws IOException {
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath(CONTEXT_PATH);
        contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)), MAPPING_URL);
        contextHandler.addServlet(new ServletHolder("defaultServletHandler", new DefaultServlet()), "");
        contextHandler.addEventListener(new ContextLoaderListener(context));

//        System.err.println("MY ROOT POINT: " + new FilePath("web").getURI().toString());

//        contextHandler.setResourceBase(new ClassPathResource("web").getURI().toString());
        contextHandler.setResourceBase("module/web-ui/web");
        return contextHandler;
    }

    private static WebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(CONFIG_LOCATION);
        context.getEnvironment().setDefaultProfiles(DEFAULT_PROFILE);
        return context;
    }
}
