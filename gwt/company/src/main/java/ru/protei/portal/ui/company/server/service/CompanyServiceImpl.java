package ru.protei.portal.ui.company.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.service.CompanyService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса по работе с компаниями
 */
@Service( "CompanyService" )
public class CompanyServiceImpl implements CompanyService {

    @Override
    public List<EntityOption> companyOptionList() throws RequestFailedException {
        log.debug( "get company option list" );

        CoreResponse< List<EntityOption> > result = companyService.companyOptionList();

        log.debug("result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0);

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    @Override
    public List< Company > getCompanies( CompanyQuery companyQuery) throws RequestFailedException {

        List< Long > categoryIds = companyQuery.getCategoryIds();

        log.debug( "getCompanies(): searchPattern={} | categories={} | group={} | sortField={} | sortDir={}",
                companyQuery.getSearchString(), categoryIds, companyQuery.getGroupId(),
                companyQuery.getSortField(), companyQuery.getSortDir() );

        CoreResponse< List<Company>> result = companyService.companyList(companyQuery);

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    @Override
    public List< CompanyGroup > getCompanyGroups( String searchPattern ) throws RequestFailedException {

        log.debug("getCompanyGroups: searchPattern={}", searchPattern);

        BaseQuery query = new BaseQuery( searchPattern, En_SortField.group_name, En_SortDir.ASC );

        CoreResponse< List<CompanyGroup> > result = companyService.groupList(query);

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    @Override
    public List<CompanyCategory> getCompanyCategories() throws RequestFailedException {

        log.debug( "getCompanyCategories" );

        CoreResponse< List<CompanyCategory> > result = companyService.categoryList();

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    @Override
    public Boolean saveCompany( Company company, CompanyGroup group ) throws RequestFailedException {

        log.debug( "saveCompany(): company={} | group={}", company, group );
        if (isCompanyNameExists(company.getCname(), company.getId()))
            throw new RequestFailedException();
        
        CoreResponse< Company > response;

        if ( company.getId() == null )
            response = companyService.createCompany( company, group );
        else
            response = companyService.updateCompany( company, group );

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
    public Company getCompanyById(long id) throws RequestFailedException {
        log.debug( "getCompanyById: id={}", id );

        CoreResponse<Company> response = companyService.getCompanyById(id);

        log.debug( "getCompanyById: response.isOk()={} | response.getData()", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }

    @Autowired
    private ru.protei.portal.core.service.CompanyService companyService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
