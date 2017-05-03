package ru.protei.portal.test.model.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.impl.CompanyDAO_Impl;
import ru.protei.portal.core.model.dao.impl.CompanyGroupHomeDAO_Impl;
import ru.protei.portal.core.model.dao.impl.PersonDAO_Impl;
import ru.protei.portal.core.model.ent.Company;

/**
 * Created by Mike on 03.11.2016.
 */
@Configuration
public class TestConfiguration {

    @Bean
    public PersonDAO getPersonDAO() {
        return new PersonDAO_Impl();
    }

    @Bean
    public CompanyDAO getCompanyDAO () {
        return new CompanyDAO_Impl();
    }

    @Bean
    public CompanyGroupHomeDAO companyGroupHomeDAO() {
        return new CompanyGroupHomeDAO_Impl();
    }


}
