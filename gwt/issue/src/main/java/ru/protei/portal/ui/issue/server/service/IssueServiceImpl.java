package ru.protei.portal.ui.issue.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.issue.client.service.IssueService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с обращениями
 */
@Service( "IssueService" )
public class IssueServiceImpl implements IssueService {

    @Override
    public List< CaseObject > getIssues( CaseQuery query ) throws RequestFailedException {
        log.debug( "getIssues(): companyId={} | searchPattern={} | sortField={} | sortDir={} | caseService={}", query.getCompanyId(), query.getSearchString(), query.getSortField(), query.getSortDir(), caseService );
        CoreResponse<List<CaseObject>> response = caseService.caseObjectList( query );
        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public CaseObject getIssue( long id ) {
        log.debug("getIssue(): id: {}", id);
        CoreResponse<CaseObject> response = caseService.getCaseObject( id );
        log.debug("getIssue(), id: {} -> {} ", id, response.isError() ? "error" : response.getData().getCaseNumber());
        return response.getData();
    }

    @Override
    public Boolean saveIssue( CaseObject caseObject ) throws RequestFailedException{
        log.debug( "saveIssue(): case={}", caseObject );

        CoreResponse< CaseObject > response;
        if ( caseObject.getId() == null ) {
            caseObject.setTypeId(En_CaseType.CRM_SUPPORT.getId());
//            caseObject.setCreatorId(getCurrentPerson().getId());
            caseObject.setCreatorId(1L);

            response = caseService.saveCaseObject(caseObject);
        }else
            response = caseService.updateCaseObject(caseObject);

        log.debug( "saveIssue(): response.isOk()={}", response.isOk() );
        if ( response.isError() ) throw new RequestFailedException(response.getStatus());
        return response.getData() != null;
    }

    @Override
    public List<En_CaseState> getStateList() throws RequestFailedException {
        En_CaseType type = En_CaseType.CRM_SUPPORT;

        log.debug( "getStatesByCaseType: caseType={} ", type );

        CoreResponse< List<En_CaseState> > result = caseService.getStateList(type);

        log.debug("result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0);

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    private Person getCurrentPerson(){
        return null;
//        return sessionService.getUserSessionDescriptor(request).getPerson();
    }

    @Autowired
    CaseService caseService;

//    @Autowired
//    SessionService sessionService;

    @Autowired
    HttpServletRequest request;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
