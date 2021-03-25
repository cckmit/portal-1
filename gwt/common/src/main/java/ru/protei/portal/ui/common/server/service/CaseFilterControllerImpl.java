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
import ru.protei.portal.core.model.query.HasFilterQueryIds;
import ru.protei.portal.core.model.view.filterwidget.AbstractFilterShortView;
import ru.protei.portal.core.service.CaseFilterService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.CaseFilterController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с фильтрами обращений
 */
@Service( "CaseFilterController" )
public class CaseFilterControllerImpl implements CaseFilterController {

    @Override
    public List<AbstractFilterShortView> getCaseFilterShortViewList(En_CaseFilterType filterType) throws RequestFailedException {

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        log.info( "getCaseFilterShortViewList(): accountId={}, filterType={} ", token.getUserLoginId(), filterType );

        Result< List< AbstractFilterShortView > > response = caseFilterService.getCaseFilterShortViewList( token.getUserLoginId(), filterType );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public <T extends HasFilterQueryIds> CaseFilterDto<T> getCaseFilter(Long id) throws RequestFailedException {
        log.info("getCaseFilter, id: {}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<CaseFilterDto<T>> response = caseFilterService.getCaseFilterDto(token, id);

        log.info("getCaseFilter, id: {}, response: {} ", id, response.isError() ? "error" : response.getData());

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public SelectorsParams getSelectorsParams( HasFilterQueryIds filterEntityIds ) throws RequestFailedException {
        log.info("getSelectorsParams, caseQuery: {}", filterEntityIds );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<SelectorsParams> response = caseFilterService.getSelectorsParams( token, filterEntityIds );

        log.info("getSelectorsParams, id: {}, response: {} ", filterEntityIds, response.isError() ? "error" : response.getData());

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public <T extends HasFilterQueryIds> CaseFilterDto<T> saveCaseFilter(CaseFilterDto<T> filter) throws RequestFailedException {

        log.info("saveCaseFilter, caseFilter: {}", filter);

        if (filter == null) {
            log.warn("Not null caseFilter is required");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<CaseFilterDto<T>> response = caseFilterService.saveCaseFilter(token, filter);

        log.info("saveCaseFilter, result: {}", response.getStatus());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Override
    public Long removeCaseFilter(Long id) throws RequestFailedException {
        log.info( "removeCaseFilter(): id={}", id );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< Long > response = caseFilterService.removeCaseFilter( token, id );
        log.info( "removeCaseFilter(): result={}", response.getStatus() );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Autowired
    CaseFilterService caseFilterService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    SessionService sessionService;

    private static final Logger log = LoggerFactory.getLogger(CaseFilterControllerImpl.class);
}
