package ru.protei.portal.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.GenericUnmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.protei.portal.api.model.*;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;
import ru.protei.portal.core.model.struct.Photo;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.auth.AuthServiceImpl;
import ru.protei.portal.core.service.auth.LDAPAuthProvider;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.portal.core.utils.SimpleSidGenerator;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.portal.tools.migrate.sybase.SybConnProvider;
import ru.protei.portal.tools.migrate.sybase.SybConnWrapperImpl;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.core.utils.config.exception.ConfigException;
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
    public SessionIdGen getSessionIdGenerator() { return new SimpleSidGenerator(); }

    @Bean
    public AuthService getAuthService() { return new AuthServiceImpl(); }

    @Bean
    public LDAPAuthProvider getLDAPAuthProvider() {
        return new LDAPAuthProvider();
    }

    @Bean
    public UserSessionDAO getUserSessionDAO() {
        return new UserSessionDAO_Impl();
    }

    @Bean
    public EmployeeSqlBuilder employeeSqlBuilder() {
        return new EmployeeSqlBuilder();
    }

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
    public UserLoginDAO getUserLoginDAO() {
        return new UserLoginDAO_Impl();
    }

    @Bean
    public UserRoleDAO getUserRoleDAO() {
        return new UserRoleDAO_impl();
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
    public AuditObjectDAO getAuditDAO() {
        return new AuditObjectDAO_Impl();
    }

    @Bean
    public EmployeeRegistrationDAO getEmployeeRegistrationDAO() {
        return new EmployeeRegistrationDAO_Impl();
    }

    /**
     * Config
     * @return
     */

    @Bean
    public PortalConfig getPortalConfig () throws ConfigException {
        return new PortalConfig("portal.properties");
    }

    @Bean
    public SybConnProvider getSybConnProvider (@Autowired PortalConfig config) throws Throwable {
        return new SybConnWrapperImpl(
                config.data().legacySysConfig().getJdbcDriver(),
                config.data().legacySysConfig().getJdbcURL(),
                config.data().legacySysConfig().getLogin(),
                config.data().legacySysConfig().getPasswd()
        );
    }

    @Bean
    public LegacySystemDAO getLegacySystemDAO () { return new LegacySystemDAO(); }

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
                Result.class, ResultList.class,
                WorkerRecord.class, WorkerRecordList.class,
                DepartmentRecord.class, IdList.class,
                Photo.class, PhotoList.class);
        return new MarshallingHttpMessageConverter(oxmMarshaller);
    }

    private ByteArrayHttpMessageConverter getByteArrayHttpMessageConverter() {
        return new ByteArrayHttpMessageConverter();
    }
}
