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
            String url = this.getThreadLocalRequest().getRequestURI();
            String controller = this.extractControllerName(url);
            Object bean = this.getBean(controller);
            RPCRequest rpcRequest = RPC.decodeRequest(payload, bean.getClass(), this);
            this.onAfterRequestDeserialized(rpcRequest);

            LOG.info( "Invoking: " + bean.getClass().getSimpleName() + "."+rpcRequest.getMethod().getName() + "(..) for URL: " + url);

            SerializationPolicy serializationPolicy = rpcRequest.getSerializationPolicy();
            if (serializationPolicy == null) {
                LOG.warn( "processCall(): SerializationPolicy not found. May occur when application redeployed." );
                return RPC.encodeResponseForFailure( null, new IncompatibleRemoteServiceException( "SerializationPolicy not found" ) );
            }

            return RPC.invokeAndEncodeResponse(bean, rpcRequest.getMethod(), rpcRequest.getParameters(), serializationPolicy );
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

        SerializationPolicy serializationPolicy = super.doGetSerializationPolicy( request, moduleBaseURL, strongName );
        if (serializationPolicy == null) {
            LOG.warn( "doGetSerializationPolicyI(): SerializationPolicy not found. May occur when application redeployed." );
            throw new  IncompatibleRemoteServiceException( "SerializationPolicy not found" );
        }

        return serializationPolicy;
    }

    private String extractControllerName( String url ) {
           String service = url.substring(url.lastIndexOf("/") + 1);
        return service;
    }

    private Object getBean( String service ) {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        if(applicationContext == null) {
            throw new IllegalStateException("No Spring web application context found");
        } else if(!applicationContext.containsBean(service)) {
            throw new IllegalArgumentException("Spring bean not found: " + service);
        }

        Object bean = applicationContext.getBean( service );

        if (!(bean instanceof RemoteService)) {
            throw new IllegalArgumentException( "Spring bean is not a GWT RemoteService: " + service + " (" + bean + ")" );
        }
        return bean;
    }
}
