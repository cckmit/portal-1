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

/**
 * Реализация сервиса по работе с компаниями
 */
@Service( "CompanyService" )
public class CompanyServiceImpl extends RemoteServiceServlet implements CompanyService {

    @Override
    public List< Company > getCompanies( String searchPattern, Set< CompanyCategory > categories, CompanyGroup group, En_SortField sortField, Boolean dirSort ) throws RequestFailedException {

        List< Long > categoryIds = new ArrayList< Long >();
        if ( categories != null && !categories.isEmpty() ) {
            for ( CompanyCategory category : categories ) {
                if ( category != null )
                    categoryIds.add( category.getId() );
            }
        }

        log.debug( "getCompanies: searchPattern={}", searchPattern );

        log.debug( "getCompanies: categories={}", categoryIds.toString() );

        log.debug( "getCompanies: group={}", group != null ? group.getId() : null );

        log.debug( "getCompanies: sortField={}", sortField );

        log.debug( "getCompanies: dirSort={}", dirSort ? En_SortDir.ASC : En_SortDir.DESC );

        CompanyQuery query = new CompanyQuery( searchPattern, sortField, dirSort ? En_SortDir.ASC : En_SortDir.DESC );
        query.setGroupId( group != null ? group.getId() : null );
        query.setCategoryIds( categoryIds );

        HttpListResult< Company> result = companyService.list( query );

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

        log.debug( "saveCompany" );
        log.debug( "saveCompany: Id={}", company.getId() );
        log.debug( "saveCompany: name={}", company.getCname() );

        if (isCompanyNameExists(company.getCname(), company.getId()))
            throw new RequestFailedException();

        CoreResponse< Company > response;

        if ( company.getId() == null )
            response = companyService.createCompany( company, group );
        else
            response = companyService.updateCompany( company, group );

        if ( response.isError() ) throw new RequestFailedException();

        return response.getData() != null;
    }

    @Override
    public Boolean isCompanyNameExists(String name, Long id) throws RequestFailedException {

        log.debug( "isCompanyNameExists" );
        log.debug( "isCompanyNameExists: name={}", name );
        log.debug( "isCompanyNameExists: id={}", id );

        CoreResponse<Boolean> response = companyService.isCompanyNameExists(name, id);

        if (response.isError()) throw new RequestFailedException();

        return response.getData();
    }

    @Override
    public Boolean isGroupNameExists(String name, Long id) throws RequestFailedException {

        log.debug( "isGroupNameExists" );
        log.debug( "isGroupNameExists: name={}", name );
        log.debug( "isGroupNameExists: id={}", id );

        CoreResponse<Boolean> response = companyService.isGroupNameExists(name, id);

        if (response.isError()) throw new RequestFailedException();

        return response.getData();
    }

    @Autowired
    private ru.protei.portal.core.service.dict.CompanyService companyService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
