package ru.protei.portal.ui.company.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.CaseStateService;
import ru.protei.portal.core.service.CompanyService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.CompanyController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;
import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

/**
 * Реализация сервиса по работе с компаниями
 */
@Service( "CompanyController" )
public class CompanyControllerImpl implements CompanyController {

    @Override
    public SearchResult< Company > getCompanies(CompanyQuery companyQuery) throws RequestFailedException {

        log.info( "getCompanies(): searchPattern={} | categories={} | sortField={} | sortDir={}",
                companyQuery.getSearchString(), companyQuery.getCategoryIds(),
                companyQuery.getSortField(), companyQuery.getSortDir() );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(companyService.getCompanies(token, companyQuery));
    }

    @Override
    public List< CompanyGroup > getCompanyGroups( String searchPattern ) throws RequestFailedException {

        log.info("getCompanyGroups: searchPattern={}", searchPattern);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        CompanyGroupQuery query = new CompanyGroupQuery( searchPattern, En_SortField.group_name, En_SortDir.ASC );

        Result< List<CompanyGroup> > result = companyService.groupList(token, query);

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    @Override
    public Boolean saveCompany( Company company ) throws RequestFailedException {

        log.info( "saveCompany(): company={}", company );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        if (isCompanyNameExists(company.getCname(), company.getId()))
            throw new RequestFailedException(En_ResultStatus.ALREADY_EXIST);
        
        Result< Company > response;

        if ( company.getId() == null )
            response = companyService.createCompany( token, company );
        else
            response = companyService.updateCompany( token, company );

        log.info( "saveCompany(): response.isOk()={}", response.isOk() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData() != null;
    }

    @Override
    public Boolean updateState(Long companyId, boolean isArchived) throws RequestFailedException {
        log.info("updateState(): companyId={} | isArchived={}", companyId, isArchived);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result response = companyService.updateState(token, companyId, isArchived);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        log.info("updateState(): response.getData()={}", response.isOk());

        return response.getData() != null;
    }

    @Override
    public Boolean isCompanyNameExists( String name, Long excludeId ) throws RequestFailedException {

        log.info( "isCompanyNameExists(): name={} | excludeId={}", name, excludeId );
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Boolean> response = companyService.isCompanyNameExists( token, name, excludeId );

        log.info( "isCompanyNameExists(): response.isOk()={} | response.getData() = {}", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }

    @Override
    public Boolean isGroupNameExists(String name, Long excludeId) throws RequestFailedException {

        log.info( "isGroupNameExists(): name={} | excludeId={}", name, excludeId );
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Boolean> response = companyService.isGroupNameExists(token, name, excludeId);

        log.info( "isGroupNameExists(): response.isOk()={} | response.getData() = {}", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }


    @Override
    public Company getCompany( long id ) throws RequestFailedException {
        log.info( "getCompany(): id={}", id );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Company> response = companyService.getCompany(token, id);

        log.info( "getCompany(): response.isOk()={} | response.getData() = {}", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }

    @Override
    public Company getCompanyOmitPrivileges(long id) throws RequestFailedException {
        log.info("getCompanyUnsafe(): id={}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Company> response = companyService.getCompanyOmitPrivileges(token, id);

        log.info("getCompanyUnsafe(): response.isOk()={} | response.getData() = {}", response.isOk(), response.getData());

        if (response.isError()) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }

    @Override
    public List< EntityOption > getCompanyOptionList(CompanyQuery query) throws RequestFailedException {
        log.info( "getCompanyOptionList(): query={}", query  );
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< List< EntityOption > > result = companyService.companyOptionList(token, query);

        log.info( "result status: {}, data-amount: {}", result.getStatus(), size(result.getData()) );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List<EntityOption> getSubcontractorOptionList(Long companyId, boolean isActive) throws RequestFailedException {
        log.info("getSubcontractorOptionList(): companyId={}, isActive={}", companyId, isActive);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<List<EntityOption>> result = companyService.subcontractorOptionListByCompanyIds(token, setOf(companyId), isActive);

        log.info("getSubcontractorOptionList(): {}", result.isOk() ? "ok" : result.getStatus());

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    @Override
    public List<EntityOption> getInitiatorOptionList(Long subcontractorId, boolean isActive) throws RequestFailedException {
        log.info("getInitiatorOptionList(): subcontractorId={}, isActive={}", subcontractorId, isActive);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<List<EntityOption>> result = companyService.companyOptionListBySubcontractorIds(token, setOf(subcontractorId), isActive);

        log.info("getInitiatorOptionList(): {}", result.isOk() ? "ok" : result.getStatus());

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    @Override
    public List< EntityOption > getCompanyOptionListIgnorePrivileges(CompanyQuery query) throws RequestFailedException {
        log.info( "getCompanyOptionListIgnorePrivileges(): query={}", query );
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< List< EntityOption > > result = companyService.companyOptionListIgnorePrivileges(token, query);

        log.info( "result status: {}, data-amount: {}", result.getStatus(), size(result.getData()) );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List< EntityOption > getGroupOptionList() throws RequestFailedException {

        log.info( "getGroupOptionList()" );
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< List< EntityOption > > result = companyService.groupOptionList(token);

        log.info( "result status: {}, data-amount: {}", result.getStatus(), size(result.getData()) );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List< En_CompanyCategory > getCategoryOptionList() throws RequestFailedException {

        log.info( "getCategoryOptionList()" );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Set<UserRole> availableRoles = token.getRoles();
        boolean hasOfficial = policyService.hasPrivilegeFor(En_Privilege.OFFICIAL_VIEW, availableRoles);

        Result< List< En_CompanyCategory > > result = companyService.categoryOptionList(token, hasOfficial);

        log.info( "result status: {}, data-amount: {}", result.getStatus(), size(result.getData()) );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List< CompanySubscription > getCompanySubscription( Long companyId ) throws RequestFailedException {
        log.info( "getCompanySubscription()" );
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< List< CompanySubscription > > result = companyService.getCompanySubscriptions( token, companyId );

        if ( result.isError() ) {
            throw new RequestFailedException( result.getStatus() );
        }
        return result.getData();
    }

    @Override
    public List<CompanySubscription> getCompanyWithParentCompanySubscriptions(Set<Long> companyIds) throws RequestFailedException {
        log.info("getCompanyWithParentCompanySubscriptions() companyIds={}", companyIds);
        AuthToken authToken = getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(companyService.getCompanyWithParentCompanySubscriptions(authToken, companyIds));
    }

    @Override
    public List<CaseState> getCompanyCaseStates(Long companyId) throws RequestFailedException {
        log.info("getCompanyCaseStates() companyId={}", companyId);
        return checkResultAndGetData( caseStateService.getCaseStatesForCompanyOmitPrivileges(companyId));
    }

    @Override
    public List<EntityOption> getAllHomeCompanies() throws RequestFailedException {
        log.info("getAllHomeCompanies()");
        AuthToken authToken = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(companyService.getAllHomeCompanies(authToken));
    }

    @Override
    public List<CompanyImportanceItem> getCompanyImportanceItems(Long companyId) throws RequestFailedException {
        log.info("getCompanyImportanceItems() companyId={}", companyId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        return checkResultAndGetData(companyService.getCompanyImportanceItems(token,companyId));
    }

    @Override
    public List<EntityOption> getSingleHomeCompanies() throws RequestFailedException {
        log.info("getSingleHomeCompanies()");
        AuthToken authToken = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(companyService.getSingleHomeCompanies(authToken));
    }

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CaseStateService caseStateService;

    @Autowired
    SessionService sessionService;

    @Autowired
    PolicyService policyService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(CompanyControllerImpl.class);

}
