package ru.protei.portal.ui.company.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.CaseStateService;
import ru.protei.portal.core.service.CompanyService;
import ru.protei.portal.core.service.PolicyService;
import ru.protei.portal.ui.common.client.service.CompanyController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

/**
 * Реализация сервиса по работе с компаниями
 */
@Service( "CompanyController" )
public class CompanyControllerImpl implements CompanyController {

    @Override
    public List< Company > getCompanies( CompanyQuery companyQuery) throws RequestFailedException {

        List< Long > categoryIds = companyQuery.getCategoryIds();

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug( "getCompanies(): searchPattern={} | categories={} | sortField={} | sortDir={}",
                companyQuery.getSearchString(), categoryIds,
                companyQuery.getSortField(), companyQuery.getSortDir() );

        CoreResponse< List<Company>> result = companyService.companyList( descriptor.makeAuthToken(), companyQuery );

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    @Override
    public List< CompanyGroup > getCompanyGroups( String searchPattern ) throws RequestFailedException {

        log.debug("getCompanyGroups: searchPattern={}", searchPattern);

        CompanyGroupQuery query = new CompanyGroupQuery( searchPattern, En_SortField.group_name, En_SortDir.ASC );

        CoreResponse< List<CompanyGroup> > result = companyService.groupList(query);

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    @Override
    public Boolean saveCompany( Company company ) throws RequestFailedException {

        log.debug( "saveCompany(): company={}", company );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        if (isCompanyNameExists(company.getCname(), company.getId()))
            throw new RequestFailedException(En_ResultStatus.ALREADY_EXIST);
        
        CoreResponse< Company > response;

        if ( company.getId() == null )
            response = companyService.createCompany( descriptor.makeAuthToken(), company );
        else
            response = companyService.updateCompany( descriptor.makeAuthToken(), company );

        log.debug( "saveCompany(): response.isOk()={}", response.isOk() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData() != null;
    }

    @Override
    public Boolean isCompanyNameExists( String name, Long excludeId ) throws RequestFailedException {

        log.debug( "isCompanyNameExists(): name={} | excludeId={}", name, excludeId );

        CoreResponse<Boolean> response = companyService.isCompanyNameExists( name, excludeId );

        log.debug( "isCompanyNameExists(): response.isOk()={} | response.getData()", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }

    @Override
    public Boolean isGroupNameExists(String name, Long excludeId) throws RequestFailedException {

        log.debug( "isGroupNameExists(): name={} | excludeId={}", name, excludeId );

        CoreResponse<Boolean> response = companyService.isGroupNameExists(name, excludeId);

        log.debug( "isGroupNameExists(): response.isOk()={} | response.getData()", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }


    @Override
    public Company getCompany( long id ) throws RequestFailedException {
        log.debug( "getCompany(): id={}", id );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Company> response = companyService.getCompany( descriptor.makeAuthToken(),  id );

        log.debug( "getCompany(): response.isOk()={} | response.getData()", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }

    @Override
    public List< EntityOption > getCompanyOptionList(CompanyQuery query) throws RequestFailedException {
        log.debug( "getCompanyOptionList()" );
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< List< EntityOption > > result = companyService.companyOptionList( descriptor.makeAuthToken(), query);

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List< EntityOption > getGroupOptionList() throws RequestFailedException {

        log.debug( "getGroupOptionList()" );

        CoreResponse< List< EntityOption > > result = companyService.groupOptionList();

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List< EntityOption > getCategoryOptionList() throws RequestFailedException {

        log.debug( "getCategoryOptionList()" );

        Set<UserRole> availableRoles = getDescriptorAndCheckSession().getLogin().getRoles();
        boolean hasOfficial = policyService.hasPrivilegeFor(En_Privilege.OFFICIAL_VIEW, availableRoles);

        CoreResponse< List< EntityOption > > result = companyService.categoryOptionList(hasOfficial);

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List<CompanySubscription> updateSelfCompanySubscription( List< CompanySubscription > value ) throws RequestFailedException {
        log.debug( "updateSelfCompanySubscription()" );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse< Boolean > updateResult = companyService.updateCompanySubscriptions( descriptor.getCompany().getId(), value );

        if ( updateResult.isError() ) {
            throw new RequestFailedException( updateResult.getStatus() );
        }

        CoreResponse< List<CompanySubscription> > companySubscriptionResult = companyService.getCompanySubscriptions( descriptor.getCompany().getId() );
        if ( companySubscriptionResult.isError() ) {
            throw new RequestFailedException( updateResult.getStatus() );
        }

        descriptor.getCompany().setSubscriptions(companySubscriptionResult.getData());

        return companySubscriptionResult.getData();
    }

    @Override
    public long getCompaniesCount(CompanyQuery query) throws RequestFailedException{
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug( "getCompaniesCount(): query={}", query );
        CoreResponse<Long> result = companyService.countCompanies( descriptor.makeAuthToken(), query );
        return result.isOk() ? result.getData() : 0L;
    }

    @Override
    public List< CompanySubscription > getCompanySubscription( Long companyId ) throws RequestFailedException {
        log.debug( "getCompanySubscription()" );

        CoreResponse< List< CompanySubscription > > result = companyService.getCompanySubscriptions( companyId );

        if ( result.isError() ) {
            throw new RequestFailedException( result.getStatus() );
        }
        return result.getData();
    }

    @Override
    public List< CompanySubscription > getCompanyWithParentCompanySubscriptions( Long companyId ) throws RequestFailedException {
        log.info( "getCompanyWithParentCompanySubscriptions() companyId={}", companyId );
        AuthToken authToken = getAuthToken( sessionService, httpServletRequest );
        return ServiceUtils.checkResultAndGetData( companyService.getCompanyWithParentCompanySubscriptions( authToken, companyId ));
    }

    @Override
    public List<CaseState> getCompanyCaseStates(Long companyId) throws RequestFailedException {
        AuthToken authToken = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData( caseStateService.getCaseStatesForCompanyOmitPrivileges(companyId));
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        log.info( "userSessionDescriptor={}", descriptor );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
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
