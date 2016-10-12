package ru.protei.portal.ui.company.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.company.client.service.CompanyService;

import java.util.List;

/**
 * Реализация сервиса по работе с компаниями
 */
@Service( "CompanyService" )
public class CompanyServiceImpl extends RemoteServiceServlet implements CompanyService {

    @Override
    public List<Company> getCompanies( String param ) throws RequestFailedException {

        log.debug( "getCompanies: param={}", param );

        HttpListResult<Company> result = companyService.list( param, null, En_SortField.comp_name, "asc" );

        return result.getItems();

    }

    @Autowired
    private ru.protei.portal.core.service.dict.CompanyService companyService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
