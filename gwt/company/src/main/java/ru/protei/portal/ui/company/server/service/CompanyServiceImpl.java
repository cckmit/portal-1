package ru.protei.portal.ui.company.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.company.client.service.CompanyService;

import java.util.List;

/**
 * Реализация сервиса по работе с компаниями
 */
@Service( "CompanyService" )
public class CompanyServiceImpl extends RemoteServiceServlet implements CompanyService {

    @Override
    public List< Company > getCompanies( String searchPattern, CompanyGroup group, En_SortField sortField ) throws RequestFailedException {

        Long groupId = null;
        if ( group != null ) groupId = group.getId();

        log.debug( "getCompanies: searchPattern={}", searchPattern );

        log.debug( "getCompanies: group={}", groupId );

        log.debug( "getCompanies: sortField={}", sortField );

        HttpListResult< Company > result = companyService.list( searchPattern, groupId, sortField, "asc" );

        log.debug( "getCompanies: result={}", result != null && result.getItems() != null ? result.getItems().size() : null);

        return result.getItems();
    }

    @Override
    public List< CompanyGroup > getCompanyGroups( String searchPattern ) throws RequestFailedException {

        log.debug( "getCompanyGroups: searchPattern={}", searchPattern );

        HttpListResult< CompanyGroup > result = companyService.groupList( searchPattern, En_SortField.group_name, "asc" );

        log.debug( "getCompanyGroups: result={}", result != null && result.getItems() != null ? result.getItems().size() : null);

        return result.getItems();

    }

    @Autowired
    private ru.protei.portal.core.service.dict.CompanyService companyService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
