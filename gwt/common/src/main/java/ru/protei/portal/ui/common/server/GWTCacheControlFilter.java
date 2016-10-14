package ru.protei.portal.ui.common.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link Filter} to add cache control headers for GWT generated files to ensure
 * that the correct files get cached.
 */
public class GWTCacheControlFilter implements Filter {

    public void destroy() {}

    public void init( FilterConfig config ) throws ServletException {}

    public void doFilter( ServletRequest request, ServletResponse response, FilterChain filterChain ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String requestURI = httpRequest.getRequestURI();
//        if ( requestURI.contains( ".nocache." ) ) {
            Date now = new Date();
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setDateHeader( "Date", now.getTime() );
            httpResponse.setDateHeader( "Expires", now.getTime() - EXPIRES_PERIOD );
            httpResponse.setHeader( "Pragma", "no-cache" );
            httpResponse.setHeader( "Cache-control", "no-cache, no-store, must-revalidate" );
//        }

        filterChain.doFilter( request, response );
    }

    // one day old
//    private static final long EXPIRES_PERIOD = 86400000L;
    // 10 second
    private static final long EXPIRES_PERIOD = 10000L;
}