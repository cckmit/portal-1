package ru.protei.portal.ui.company.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.company.client.service.CompanyService;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Реализация сервиса по работе с компаниями
 */
@Service( "CompanyService" )
public class CompanyServiceImpl extends RemoteServiceServlet implements CompanyService {

    @Override
    public List< Company > getCompanies( String searchPattern, Set< CompanyCategory > categories, CompanyGroup group, En_SortField sortField, Boolean dirSort ) throws RequestFailedException {

        List< Long > categoryIds = null;
        if ( categories != null ) {
            categoryIds = categories.stream()
                    .map( CompanyCategory::getId )
                    .collect( Collectors.toList() );
        }

        log.debug( "getCompanies(): searchPattern={} | categories={} | group={} | sortField={} | dirSort={}",
                searchPattern, categoryIds, (group != null ? group.getId() : null),
                sortField, (dirSort ? En_SortDir.ASC : En_SortDir.DESC) );

        CompanyQuery query = new CompanyQuery( searchPattern, sortField, dirSort ? En_SortDir.ASC : En_SortDir.DESC );
        query.setGroupId( group != null ? group.getId() : null );
        query.setCategoryIds( categoryIds );

        HttpListResult< Company> result = companyService.companyList(query);

        return result.getItems();
    }

    @Override
    public List< CompanyGroup > getCompanyGroups( String searchPattern ) throws RequestFailedException {

        log.debug("getCompanyGroups: searchPattern={}", searchPattern);

        BaseQuery query = new BaseQuery( searchPattern, En_SortField.group_name, En_SortDir.ASC );

        HttpListResult< CompanyGroup > result = companyService.groupList(query);

        return result.getItems();
    }

    @Override
    public List<CompanyCategory> getCompanyCategories() throws RequestFailedException {

        log.debug( "getCompanyCategories" );

        HttpListResult< CompanyCategory > result = companyService.categoryList();

        return result.getItems();
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

        if ( response.isError() ) throw new RequestFailedException();

        return response.getData() != null;
    }

    @Override
    public Boolean isCompanyNameExists( String name, Long excludeId ) throws RequestFailedException {

        log.debug( "isCompanyNameExists(): name={} | excludeId={}", name, excludeId );

        CoreResponse<Boolean> response = companyService.isCompanyNameExists( name, excludeId );

        log.debug( "isCompanyNameExists(): response.isOk()={} | response.getData()", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException();

        return response.getData();
    }

    @Override
    public Boolean isGroupNameExists(String name, Long excludeId) throws RequestFailedException {

        log.debug( "isGroupNameExists(): name={} | excludeId={}", name, excludeId );

        CoreResponse<Boolean> response = companyService.isGroupNameExists(name, excludeId);

        log.debug( "isGroupNameExists(): response.isOk()={} | response.getData()", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException();

        return response.getData();
    }

    @Autowired
    private ru.protei.portal.core.service.dict.CompanyService companyService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
