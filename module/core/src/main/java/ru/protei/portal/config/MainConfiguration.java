package ru.protei.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.protei.portal.api.WorkersController;
import ru.protei.portal.api.WorkersControllerImpl;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;

@Configuration
public class MainConfiguration {

    @Bean
    public CompanyDAO getCompanyDAO () {
        return new CompanyDAO_Impl();
    }

    @Bean
    public PersonDAO getPersonDAO () {
        return new PersonDAO_Impl();
    }

    @Bean
    public CaseTaskDAO getCaseTaskDAO () {
        return new CaseTaskDAO_Impl();
    }

    @Bean
    public CaseTermDAO getCaseTermDAO () { return new CaseTermDAO_Impl(); }

    @Bean
    public DevUnitDAO getDevUnitDAO () { return new DevUnitDAO_Impl(); }

    @Bean
    public DevUnitVersionDAO getDevUnitVersionDAO () { return new DevUnitVersionDAO_Impl(); }

    @Bean
    public DevUnitBranchDAO getDevUnitBranchDAO () { return new DevUnitBranchDAO_Impl(); }

    @Bean
    public CaseCommentDAO getCaseCommentDAO () {return new CaseCommentDAO_Impl(); }

    @Bean
    public CaseDocumentDAO getCaseDocumentDAO () { return new CaseDocumentDAO_Impl(); }

    @Bean
    public CaseStateMatrixDAO getStateMatrixDAO () { return new CaseStateMatrixDAO_Impl(); }

    @Bean
    public CaseObjectDAO getCaseDAO () { return new CaseObjectDAO_Impl(); }

    @Bean
    public WorkersController getWorkersController () {
        return new WorkersControllerImpl();
    }
}
