package ru.protei.portal.ui.dutylog.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DutyLogFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.core.service.DutyLogFilterService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.DutyLogFilterController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

@Service( "DutyLogFilterController" )
public class DutyLogFilterControllerImpl implements DutyLogFilterController {

    @Override
    public List<FilterShortView> getShortViewList() throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        log.info( "getShortViewList(): loginId={}", token.getUserLoginId() );

        Result< List<FilterShortView> > response = dutyLogFilterService.getShortViewList( token.getUserLoginId() );

        return checkResultAndGetData(response);
    }

    @Override
    public DutyLogFilter getFilter(Long id) throws RequestFailedException {
        log.info("getFilter, id: {}", id);

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        Result<DutyLogFilter> response = dutyLogFilterService.getFilter( token, id );

        log.info("DutyLogFilter, id: {}, response: {} ", id, response.isError() ? "error" : response.getData());

        return checkResultAndGetData(response);
    }

    @Override
    public SelectorsParams getSelectorsParams(DutyLogQuery caseQuery) throws RequestFailedException {
        log.info("getSelectorsParams, selectorsParamsRequest: {}", caseQuery );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<SelectorsParams> response = dutyLogFilterService.getSelectorsParams( token, caseQuery );

        log.info("getSelectorsParams, id: {}, response: {} ", caseQuery, response.isError() ? "error" : response.getData());

        return checkResultAndGetData(response);
    }

    @Override
    public DutyLogFilter saveFilter(DutyLogFilter filter) throws RequestFailedException {
        log.info("saveFilter, filter: {}", filter);

        if (filter == null) {
            log.warn("Not null DutyFilter is required");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<DutyLogFilter> response = dutyLogFilterService.saveFilter(token, filter);

        log.info("saveFilter, result: {}", response.getStatus());

        return checkResultAndGetData(response);
    }

    @Override
    public Long removeFilter(Long id)  throws RequestFailedException {
        log.info( "removeFilter(): id={}", id );

        Result<Long> response = dutyLogFilterService.removeFilter( id );
        log.info( "removeFilter(): result={}", response.getStatus() );

        return checkResultAndGetData(response);
    }

    @Autowired
    DutyLogFilterService dutyLogFilterService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    SessionService sessionService;

    private static final Logger log = LoggerFactory.getLogger(DutyLogFilterControllerImpl.class);
}
