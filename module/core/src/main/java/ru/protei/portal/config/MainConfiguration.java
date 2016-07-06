package ru.protei.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.portal.core.service.user.AuthServiceImpl;
import ru.protei.portal.core.service.user.LDAPAuthProvider;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.portal.core.utils.SimpleSidGenerator;

@Configuration
public class MainConfiguration {

    @Bean
    public LDAPAuthProvider getLDAPAuthProvider() {
        return new LDAPAuthProvider();
    }

    @Bean
    public MigrationEntryDAO getMigrationEntryDAO () { return  new MigrationEntryDAO_Impl(); }

    @Bean
    public CompanyGroupHomeDAO getCompanyGroupHomeDAO () { return new CompanyGroupHomeDAO_Impl(); }

    @Bean
    public AbsenceReasonDAO getAbsenceReasonDAO() {
        return new AbsenceReasonDAO_Impl();
    }

    @Bean
    public PersonAbsenceDAO getPersonAbsenceDAO() {
        return new PersonAbsenceDAO_Impl();
    }

    @Bean
    public SessionIdGen getSessionIdGenerator() {
        return new SimpleSidGenerator();
    }

    @Bean
    public AuthService getAuthService() {
        return new AuthServiceImpl();
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
    public CaseTaskDAO getCaseTaskDAO() {
        return new CaseTaskDAO_Impl();
    }

    @Bean
    public CaseTermDAO getCaseTermDAO() {
        return new CaseTermDAO_Impl();
    }

    @Bean
    public DevUnitDAO getDevUnitDAO() {
        return new DevUnitDAO_Impl();
    }

    @Bean
    public DevUnitVersionDAO getDevUnitVersionDAO() {
        return new DevUnitVersionDAO_Impl();
    }

    @Bean
    public DevUnitBranchDAO getDevUnitBranchDAO() {
        return new DevUnitBranchDAO_Impl();
    }

    @Bean
    public CaseCommentDAO getCaseCommentDAO() {
        return new CaseCommentDAO_Impl();
    }

    @Bean
    public CaseDocumentDAO getCaseDocumentDAO() {
        return new CaseDocumentDAO_Impl();
    }

    @Bean
    public CaseStateMatrixDAO getStateMatrixDAO() {
        return new CaseStateMatrixDAO_Impl();
    }

    @Bean
    public CaseObjectDAO getCaseDAO() {
        return new CaseObjectDAO_Impl();
    }


    @Bean
    public UserSessionDAO getUserSessionDAO() {
        return new UserSessionDAO_Impl();
    }

    @Bean
    public UserRoleDAO getUserRoleDAO() {
        return new UserRoleDAO_impl();
    }

    @Bean
    public UserLoginDAO getUserLoginDAO() {
        return new UserLoginDAO_Impl();
    }


}
