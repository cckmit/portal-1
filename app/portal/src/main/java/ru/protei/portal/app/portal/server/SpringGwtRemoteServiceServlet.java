package ru.protei.portal.app.portal.server;


import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;

public class SpringGwtRemoteServiceServlet extends RemoteServiceServlet {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public SpringGwtRemoteServiceServlet() {
    }

    public void init() {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Spring GWT service exporter deployed");
        }

    }

    public String processCall(String payload) throws SerializationException {
        try {
            Object ex = this.getBean(this.getThreadLocalRequest());
            RPCRequest rpcRequest = RPC.decodeRequest(payload, ex.getClass(), this);
            this.onAfterRequestDeserialized(rpcRequest);
            if(LOG.isDebugEnabled()) {
                LOG.debug("Invoking " + ex.getClass().getName() + "." + rpcRequest.getMethod().getName());
            }

            return RPC.invokeAndEncodeResponse(ex, rpcRequest.getMethod(), rpcRequest.getParameters(), rpcRequest.getSerializationPolicy());
        } catch (IncompatibleRemoteServiceException var4) {
            this.log("An IncompatibleRemoteServiceException was thrown while processing this call.", var4);
            return RPC.encodeResponseForFailure(null, var4);
        }

    }

    @Override
    protected SerializationPolicy doGetSerializationPolicy(
            HttpServletRequest request, String moduleBaseURL, String strongName) {
        //get the base url from the header instead of the body this way
        //apache reverse proxy with rewrite on the header can work
        String moduleBaseURLHdr = request.getHeader("X-GWT-Module-Base");
        if(moduleBaseURLHdr != null){
            moduleBaseURL = moduleBaseURLHdr;
        }

        return super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
    }


    private Object getBean( HttpServletRequest request ) {
        String service = this.getService(request);
        Object bean = this.getBean(service);
        if(!(bean instanceof RemoteService )) {
            throw new IllegalArgumentException("Spring bean is not a GWT RemoteService: " + service + " (" + bean + ")");
        } else {
            if(LOG.isDebugEnabled()) {
                LOG.debug("Bean for service " + service + " is " + bean);
            }

            return bean;
        }
    }

    private String getService( HttpServletRequest request ) {
        String url = request.getRequestURI();
        String service = url.substring(url.lastIndexOf("/") + 1);
        if(LOG.isDebugEnabled()) {
            LOG.debug("Service for URL " + url + " is " + service);
        }

        return service;
    }

    private Object getBean( String name ) {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        if(applicationContext == null) {
            throw new IllegalStateException("No Spring web application context found");
        } else if(!applicationContext.containsBean(name)) {
            throw new IllegalArgumentException("Spring bean not found: " + name);
        } else {
            return applicationContext.getBean(name);
        }
    }
}
