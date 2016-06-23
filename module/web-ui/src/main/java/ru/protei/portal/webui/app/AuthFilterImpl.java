package ru.protei.portal.webui.app;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by michael on 22.06.16.
 */
public class AuthFilterImpl implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            processRequest((HttpServletRequest)servletRequest);
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }

    private void processRequest(HttpServletRequest req) {
        System.out.println("Auth-filter, Tracking information on request : " + req.getRequestURI());
    }

    @Override
    public void destroy() {

    }
}
