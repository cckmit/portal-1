package ru.protei.portal.core.wsapi;



import org.apache.cxf.Bus;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

/**
 * Created by Mike on 01.05.2017.
 */
@Configuration
public class WSAPIConfiguration {

    @Bean
    public WSCaseModule createSoapCaseService () {
        return new WSCaseModuleImpl ();
    }

    @Bean(name= Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean(name = "ws-case-endpoint")
    public Endpoint wsCaseEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), createSoapCaseService ());
        JaxWsServiceFactoryBean b = new JaxWsServiceFactoryBean();
        b.setDataBinding (new AegisDatabinding());
        endpoint.setServiceFactory (b);
        endpoint.publish ("/api/ws/case");
        return endpoint;
    }
}
