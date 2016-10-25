package ru.protei.portal.ui.company.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public void saveCompany(Company company) throws RequestFailedException {

    }

    @Override
    public Boolean isCompanyNameExists(String name) {
        return false;
    }

    @Override
    public List< Company > getCompanies( String searchPattern, Set< CompanyCategory > categories, CompanyGroup group, En_SortField sortField, Boolean dirSort ) throws RequestFailedException {

        List< Long > categoryIds = null;
        if ( categories != null ) {
            categoryIds = categories.stream()
                    .map( CompanyCategory::getId )
                    .collect( Collectors.toList() );
        }

        log.debug( "getCompanies: searchPattern={}", searchPattern );

        log.debug( "getCompanies: categories={}", categoryIds );

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

        log.debug( "getCompanyGroups: searchPattern={}", searchPattern );

        BaseQuery query = new BaseQuery( searchPattern, En_SortField.group_name, En_SortDir.ASC );

        HttpListResult< CompanyGroup > result = companyService.groupList( query );

        return result.getItems();
    }

    @Override
    public List<CompanyCategory> getCompanyCategories() throws RequestFailedException {

        log.debug( "getCompanyCategories");

        HttpListResult< CompanyCategory > result = companyService.categoryList();

        return result.getItems();
    }

    @Autowired
    private ru.protei.portal.core.service.dict.CompanyService companyService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
