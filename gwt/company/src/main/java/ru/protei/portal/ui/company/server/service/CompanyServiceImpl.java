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
import ru.protei.portal.core.service.PolicyService;
import ru.protei.portal.ui.common.client.service.CompanyService;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * Реализация сервиса по работе с компаниями
 */
@Service( "CompanyService" )
public class CompanyServiceImpl implements CompanyService {

    @Override
    public List< Company > getCompanies( CompanyQuery companyQuery) throws RequestFailedException {

        List< Long > categoryIds = companyQuery.getCategoryIds();

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug( "getCompanies(): searchPattern={} | categories={} | group={} | sortField={} | sortDir={}",
                companyQuery.getSearchString(), categoryIds, companyQuery.getGroupId(),
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
            throw new RequestFailedException();
        
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
        //TODO используется для отображения карточки компании, думаю проверка роли COMPANY_VIEW логична

        CoreResponse<Company> response = companyService.getCompany( descriptor.makeAuthToken(),  id );

        log.debug( "getCompany(): response.isOk()={} | response.getData()", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }

    @Override
    public List< EntityOption > getCompanyOptionList(List<En_CompanyCategory> categories) throws RequestFailedException {

        log.debug( "getCompanyOptionList()" );

        CoreResponse< List< EntityOption > > result = companyService.companyOptionList(categories);

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
    public void updateSelfCompanySubscription( List< CompanySubscription > value ) throws RequestFailedException {
        log.debug( "updateSelfCompanySubscription()" );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse< Boolean > result = companyService.updateCompanySubscriptions( descriptor.getCompany().getId(), value );
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
    private ru.protei.portal.core.service.CompanyService companyService;

    @Autowired
    SessionService sessionService;

    @Autowired
    PolicyService policyService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
