package ru.protei.portal.webui.app;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.spring4gwt.server.SpringGwtRemoteServiceServlet;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;

//import org.eclipse.jetty.jsp.JettyJspServlet;


public class Main {

    private static Logger logger = Logger.getLogger(Main.class);

    private static final boolean JSP_ENABLED = false;

    private static final int DEFAULT_PORT = 8090;
    private static final String CONTEXT_PATH = "/";
    private static final String CONFIG_LOCATION = "ru.protei.portal.webui.app";
    private static final String MAPPING_URL = "/*";
    //private static final String API_SPACE_URL = "/api/*";
    private static final String DEFAULT_PROFILE = "dev";

    public static void main(String[] args) {
        Server server = null;
        try {
//            Company company = new Company();
//            System.out.println(company);

            String warDir = args.length > 0 ? args[0] : null;
            int port = args.length > 1 ? Integer.parseInt(args[1],10) : DEFAULT_PORT;

            logger.debug("run portal application");
            logger.debug("using port : " + port);
            logger.debug("application war dir: " + (warDir == null ? "default" : warDir));


            server = createServer(port, warDir);

            logger.debug("server created, starting");

//            Thread.currentThread().setContextClassLoader(Main.class.getClassLoader());
            server.start();
            server.join();


        } catch (Exception e) {
            logger.debug("error", e);
        }
    }


    protected static Server createServer (int port, String warDirectory) throws Exception {
        Server server = new Server(port > 0 ? port : DEFAULT_PORT);
        server.setHandler(getServletContextHandler(getContext(), warDirectory));

        if (JSP_ENABLED) {
            Configuration.ClassList classlist = Configuration.ClassList
                    .setServerDefault(server);
            classlist.addBefore(
                    "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                    "org.eclipse.jetty.annotations.AnnotationConfiguration");
        }

        return server;
    }


    private static ServletContextHandler getServletContextHandler(WebApplicationContext context, String warDir) throws IOException {
        //ServletContextHandler contextHandler = new ServletContextHandler();
        WebAppContext webapp = new WebAppContext();

        webapp.setErrorHandler(null);
        webapp.setContextPath(CONTEXT_PATH);

        webapp.getMimeTypes().addMimeMapping("js","text/javascript;charset=utf-8");
//        System.out.println(webapp.getMimeTypes().getMimeByExtension("aaa.js"));

//        if (JSP_ENABLED) {
//            webapp.addServlet(jspServletHolder(), "*.jsp");
//        }


        webapp.addEventListener(new ContextLoaderListener(context));

        // SPRING servlet-dispatcher
        ServletHolder springHolder = springServletHolder(context);
        webapp.addServlet(springHolder, MAPPING_URL);
//        webapp.addServlet(springHolder, API_SPACE_URL);


        // Spring4gwt servlet
        ServletHolder springGwtHolder = springGwtServletHolder(context);
        webapp.addServlet(springGwtHolder, "/Crm/springGwtServices/*");
        // Spring4gwt listener
        webapp.addEventListener( new RequestContextListener() );


        webapp.addServlet(new ServletHolder("cxf", new CXFServlet()), "/api/ws/*");
        webapp.addServlet(new ServletHolder("defaultServletHandler", new DefaultServlet()), "");
        webapp.setDescriptor( ( warDir == null ? "module/web-ui/src/main/webapp" : warDir ) + "/WEB-INF/web.xml");
        webapp.setResourceBase( warDir == null ? "module/web-ui/src/main/webapp" : warDir);

        webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        webapp.setWelcomeFiles(new String[]{"index.html", "login.html"});

        if (JSP_ENABLED) {
            webapp.setAttribute(
                    "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                    ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");
        }

        return webapp;
    }

    private static ServletHolder springGwtServletHolder( WebApplicationContext context ) {
        SpringGwtRemoteServiceServlet servlet = new SpringGwtRemoteServiceServlet();
        ServletHolder holder = new ServletHolder( servlet );
        return holder;
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


//    private static ServletHolder jspServletHolder()
//    {
//        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
//        holderJsp.setInitOrder(0);
//        holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
//        holderJsp.setInitParameter("fork", "false");
//        holderJsp.setInitParameter("xpoweredBy", "false");
//        holderJsp.setInitParameter("compilerTargetVM", "1.8");
//        holderJsp.setInitParameter("compilerSourceVM", "1.8");
//        holderJsp.setInitParameter("keepgenerated", "false");
//        return holderJsp;
//    }
}
