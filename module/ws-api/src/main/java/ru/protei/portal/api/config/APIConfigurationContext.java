package ru.protei.portal.api.config;

import org.springframework.context.annotation.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.protei.portal.api.model.*;
import ru.protei.portal.api.tools.migrate.WSMigrationManager;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.List;

@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = "ru.protei.portal.api.controller")
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
    public AuditObjectDAO getAuditDAO() {
        return new AuditObjectDAO_Impl();
    }

/*
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
*/

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        converters.add(getMarshallingHttpMessageConverter());
        converters.add(getByteArrayHttpMessageConverter());
    }

    private MarshallingHttpMessageConverter getMarshallingHttpMessageConverter() {
        Jaxb2Marshaller oxmMarshaller = new Jaxb2Marshaller();
        oxmMarshaller.setClassesToBeBound(
                WorkerRecord.class, WorkerRecordList.class,
                DepartmentRecord.class, IdList.class,
                Photo.class, PhotoList.class,
                ServiceResult.class, ServiceResultList.class);
        return new MarshallingHttpMessageConverter(oxmMarshaller);
    }

    private ByteArrayHttpMessageConverter getByteArrayHttpMessageConverter() {
        return new ByteArrayHttpMessageConverter();
    }
}
