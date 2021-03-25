package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.HasFilterEntityIds;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.filterwidget.AbstractFilterShortView;
import ru.protei.portal.core.model.view.filterwidget.DtoFilterQuery;
import ru.protei.portal.core.service.IssueFilterService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.IssueFilterController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с фильтрами обращений
 */
@Service( "IssueFilterController" )
public class IssueFilterControllerImpl implements IssueFilterController {

    @Override
    public List<AbstractFilterShortView> getIssueFilterShortViewList(En_CaseFilterType filterType ) throws RequestFailedException {

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        log.info( "getIssueFilterShortViewList(): accountId={}, filterType={} ", token.getUserLoginId(), filterType );

        Result< List< AbstractFilterShortView > > response = issueFilterService.getIssueFilterShortViewList( token.getUserLoginId(), filterType );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public <T extends DtoFilterQuery> CaseFilterDto<T> getIssueFilter(Long id ) throws RequestFailedException {
        log.info("getIssueFilter, id: {}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<CaseFilterDto<T>> response = issueFilterService.getIssueFilter(token, id);

        log.info("getIssueFilter, id: {}, response: {} ", id, response.isError() ? "error" : response.getData());

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public SelectorsParams getSelectorsParams( HasFilterEntityIds filterEntityIds ) throws RequestFailedException {
        log.info("getSelectorsParams, caseQuery: {}", filterEntityIds );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<SelectorsParams> response = issueFilterService.getSelectorsParams( token, filterEntityIds );

        log.info("getSelectorsParams, id: {}, response: {} ", filterEntityIds, response.isError() ? "error" : response.getData());

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public <T extends DtoFilterQuery> CaseFilterDto<T> saveIssueFilter(CaseFilterDto<T> filter) throws RequestFailedException {

        log.info("saveIssueFilter, filter: {}", filter);

        if (filter == null) {
            log.warn("Not null issueFilter is required");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<CaseFilterDto<T>> response = issueFilterService.saveIssueFilter(token, filter);

        log.info("saveIssueFilter, result: {}", response.getStatus());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Override
    public Long removeIssueFilter(Long id ) throws RequestFailedException {
        log.info( "removeIssueFilter(): id={}", id );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< Long > response = issueFilterService.removeIssueFilter( token, id );
        log.info( "removeIssueFilter(): result={}", response.getStatus() );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Autowired
    IssueFilterService issueFilterService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    SessionService sessionService;

    private static final Logger log = LoggerFactory.getLogger(IssueFilterControllerImpl.class);
}
