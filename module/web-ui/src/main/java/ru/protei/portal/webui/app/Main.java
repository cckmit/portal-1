package ru.protei.portal.webui.app;

import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ru.protei.portal.core.model.ent.Company;

import java.io.IOException;


public class Main {

    private static final boolean JSP_ENABLED = false;

    private static final int DEFAULT_PORT = 8090;
    private static final String CONTEXT_PATH = "/";
    private static final String CONFIG_LOCATION = "ru.protei.portal.webui.app";
    private static final String MAPPING_URL = "/*";
    private static final String API_SPACE_URL = "/api/*";
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

        if (JSP_ENABLED) {
            Configuration.ClassList classlist = Configuration.ClassList
                    .setServerDefault(server);
            classlist.addBefore(
                    "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                    "org.eclipse.jetty.annotations.AnnotationConfiguration");
        }

        return server;
    }


    private static ServletContextHandler getServletContextHandler(WebApplicationContext context) throws IOException {
        //ServletContextHandler contextHandler = new ServletContextHandler();
        WebAppContext contextHandler = new WebAppContext();

        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath(CONTEXT_PATH);

        if (JSP_ENABLED) {
            contextHandler.addServlet(jspServletHolder(), "*.jsp");
        }


        // SPRING servlet-dispatcher
        ServletHolder springHolder = springServletHolder(context);
        contextHandler.addServlet(springHolder, MAPPING_URL);
        contextHandler.addServlet(springHolder, API_SPACE_URL);

        contextHandler.addServlet(new ServletHolder("defaultServletHandler", new DefaultServlet()), "");
        contextHandler.addEventListener(new ContextLoaderListener(context));
        contextHandler.setResourceBase("module/web-ui/web");

        if (JSP_ENABLED) {
            contextHandler.setAttribute(
                    "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                    ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");
        }

        return contextHandler;
    }


    private static WebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(CONFIG_LOCATION);
        context.getEnvironment().setDefaultProfiles(DEFAULT_PROFILE);
        return context;
    }

    private static ServletHolder springServletHolder (WebApplicationContext context) {
        DispatcherServlet servlet = new DispatcherServlet(context);
        ServletHolder springHolder = new ServletHolder(servlet);
        springHolder.setInitOrder(2);
        return springHolder;
    }


    private static ServletHolder jspServletHolder()
    {
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);
        holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
        holderJsp.setInitParameter("fork", "false");
        holderJsp.setInitParameter("xpoweredBy", "false");
        holderJsp.setInitParameter("compilerTargetVM", "1.8");
        holderJsp.setInitParameter("compilerSourceVM", "1.8");
        holderJsp.setInitParameter("keepgenerated", "false");
        return holderJsp;
    }
}
