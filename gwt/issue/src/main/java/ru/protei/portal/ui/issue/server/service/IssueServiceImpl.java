package ru.protei.portal.ui.issue.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.issue.client.service.IssueService;

import java.util.List;

/**
 * Реализация сервиса по работе с обращениями
 */
@Service( "IssueService" )
public class IssueServiceImpl implements IssueService {

    @Override
    public List< CaseObject > getIssues( CaseQuery query ) throws RequestFailedException {
        log.debug( "companyId={} | searchPattern={} | sortField={} | sortDir={} | caseService={}", query.getCompanyId(), query.getSearchString(), query.getSortField(), query.getSortDir(), caseService );
        CoreResponse<List<CaseObject>> response = caseService.caseObjectList( query );
        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public CaseObject getIssue( long id ) {
        return null;
    }

    @Override
    public CaseObject saveIssue( CaseObject p ) {
        return null;
    }

    @Autowired
    CaseService caseService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
