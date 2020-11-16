package ru.protei.portal.ui.absence.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AbsenceFilter;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.core.service.AbsenceFilterService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.AbsenceFilterController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

@Service( "AbsenceFilterController" )
public class AbsenceFilterControllerImpl implements AbsenceFilterController {

    @Override
    public List<FilterShortView> getShortViewList() throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        log.info( "getShortViewList(): loginId={}", token.getUserLoginId() );

        Result< List<FilterShortView> > response = absenceFilterService.getShortViewList( token.getUserLoginId() );

        return checkResultAndGetData(response);
    }

    @Override
    public AbsenceFilter getFilter(Long id) throws RequestFailedException {
        log.info("getFilter, id: {}", id);

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        Result<AbsenceFilter> response = absenceFilterService.getFilter( token, id );

        log.info("AbsenceFilter, id: {}, response: {} ", id, response.isError() ? "error" : response.getData());

        return checkResultAndGetData(response);
    }

    @Override
    public AbsenceFilter saveFilter(AbsenceFilter filter) throws RequestFailedException {
        log.info("saveFilter, filter: {}", filter);

        if (filter == null) {
            log.warn("Not null AbsenceFilter is required");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<AbsenceFilter> response = absenceFilterService.saveFilter(token, filter);

        log.info("saveFilter, result: {}", response.getStatus());

        return checkResultAndGetData(response);
    }

    @Override
    public Long removeFilter(Long id)  throws RequestFailedException {
        log.info( "removeFilter(): id={}", id );

        Result<Long> response = absenceFilterService.removeFilter( id );
        log.info( "removeFilter(): result={}", response.getStatus() );

        return checkResultAndGetData(response);
    }

    @Autowired
    AbsenceFilterService absenceFilterService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    SessionService sessionService;

    private static final Logger log = LoggerFactory.getLogger(AbsenceFilterControllerImpl.class);
}
