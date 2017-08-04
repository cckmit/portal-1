package ru.protei.portal.api.config;

import org.apache.cxf.Bus;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.protei.portal.api.model.DepartmentRecord;
import ru.protei.portal.api.model.ServiceResult;
import ru.protei.portal.api.model.WorkerRecord;
import ru.protei.portal.api.service.WorkerService;
import ru.protei.portal.api.service.WorkerServiceImpl;
import ru.protei.portal.api.tools.migrate.WSMigrationManager;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.xml.ws.Endpoint;
import java.util.List;

@Configuration
@Import({CoreConfigurationContext.class, JdbcConfigurationContext.class})
public class APIConfigurationContext extends WebMvcConfigurerAdapter {

    @Bean
    public CompanyGroupHomeDAO getCompanyGroupHomeDAO() {
        return new CompanyGroupHomeDAO_Impl();
    }

    @Bean
    public CompanyDAO getCompanyDAO() {
        return new CompanyDAO_Impl();
    }

    @Bean
    public PersonDAO getPersonDAO() {
        return new PersonDAO_Impl();
    }

    @Bean
    public CompanyDepartmentDAO getCompanyDepartmentDAO() {
        return new CompanyDepartmentDAO_Impl();
    }

    @Bean
    public WorkerPositionDAO getWorkerPositionDAO() {
        return new WorkerPositionDAO_Impl();
    }

    @Bean
    public WorkerEntryDAO getWorkerEntryDAO() {
        return new WorkerEntryDAO_Impl();
    }

    @Bean
    public WSMigrationManager getWSMigrationManager () { return new WSMigrationManager (); }

    @Bean
    public WorkerService createWorkerWebService () {
        return new WorkerServiceImpl ();
    }

    @Bean(name= Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), createWorkerWebService ());
        JaxWsServiceFactoryBean b = new JaxWsServiceFactoryBean ();
        b.setDataBinding (new AegisDatabinding ());
        endpoint.setServiceFactory (b);
        endpoint.publish ("/worker");
        return endpoint;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jaxb2Marshaller oxmMarshaller = new Jaxb2Marshaller();
        //oxmMarshaller.setClassesToBeBound(WorkerRecord.class, DepartmentRecord.class, ServiceResult.class);
        MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter(oxmMarshaller);

        converters.add(marshallingHttpMessageConverter);
    }
}
