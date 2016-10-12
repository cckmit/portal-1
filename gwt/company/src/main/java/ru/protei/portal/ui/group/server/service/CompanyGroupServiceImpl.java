package ru.protei.portal.ui.group.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.group.client.service.CompanyGroupService;

import java.util.List;

/**
 * Реализация сервиса по работе с группами компаний
 */
@Service( "CompanyGroupService" )
public class CompanyGroupServiceImpl extends RemoteServiceServlet implements CompanyGroupService {

    @Override
    public List<CompanyGroup> getCompanyGroups(String param) throws RequestFailedException {
        log.debug( "getCompanyGroups: param={}", param );

        //HttpListResult<CompanyGroup> result = companyService.list( param, En_SortField.comp_name.toString(), En_SortDir.ASC.toString());

        //return result.getItems();
        return null;
    }

    @Autowired
    private ru.protei.portal.core.service.dict.CompanyService companyService;

    private static final Logger log = LoggerFactory.getLogger("web");

}
