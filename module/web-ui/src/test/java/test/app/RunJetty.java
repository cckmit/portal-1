package test.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created by michael on 23.06.16.
 *
 * It used for testing purposes only
 */
public class RunJetty {


    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8090);

        WebAppContext context = new WebAppContext();
        context.setDescriptor("module/web-ui/web/WEB-INF/web.xml");
        context.setResourceBase("module/web-ui/web");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);

        context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        context.setWelcomeFiles(new String[]{"index.html", "login.html"});

        server.setHandler(context);

        server.start();
        server.join();
    }
}
