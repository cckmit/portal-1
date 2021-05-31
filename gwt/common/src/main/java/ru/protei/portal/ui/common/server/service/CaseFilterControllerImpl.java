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
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.HasFilterQueryIds;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.view.FilterShortView;
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
    public List<FilterShortView> getCaseFilterShortViewList(En_CaseFilterType filterType) throws RequestFailedException {

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        log.info( "getCaseFilterShortViewList(): accountId={}, filterType={} ", token.getUserLoginId(), filterType );

        Result< List< FilterShortView > > response = caseFilterService.getCaseFilterShortViewList( token.getUserLoginId(), filterType );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public CaseFilterDto<HasFilterQueryIds> getCaseFilter(Long id) throws RequestFailedException {
        log.info("getCaseFilter, id: {}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<CaseFilterDto<HasFilterQueryIds>> response = caseFilterService.getCaseFilterDto(token, id);

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
    public CaseFilterDto<ProjectQuery> saveProjectFilter(CaseFilterDto<ProjectQuery> caseFilterDto) throws RequestFailedException {
        log.info("saveProjectFilter, caseFilterDto: {}", caseFilterDto);

        if (caseFilterDto == null || caseFilterDto.getCaseFilter() == null) {
            log.warn("Not null caseFilter is required");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<CaseFilterDto<ProjectQuery>> response = caseFilterService.saveProjectFilter(token, caseFilterDto);

        log.info("saveProjectFilter, result: {}", response.getStatus());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Override
    public CaseFilterDto<DeliveryQuery> saveDeliveryFilter(CaseFilterDto<DeliveryQuery> caseFilterDto) throws RequestFailedException {
        log.info("saveDeliveryFilter, caseFilterDto: {}", caseFilterDto);

        if (caseFilterDto == null || caseFilterDto.getCaseFilter() == null) {
            log.warn("Not null caseFilter is required");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<CaseFilterDto<DeliveryQuery>> response = caseFilterService.saveDeliveryFilter(token, caseFilterDto);

        log.info("saveDeliveryFilter, result: {}", response.getStatus());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Override
    public CaseFilterDto<CaseQuery> saveIssueFilter(CaseFilterDto<CaseQuery> caseFilterDto) throws RequestFailedException {
        log.info("saveIssueFilter, caseFilterDto: {}", caseFilterDto);

        if (caseFilterDto == null || caseFilterDto.getCaseFilter() == null) {
            log.warn("Not null caseFilter is required");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<CaseFilterDto<CaseQuery>> response = caseFilterService.saveIssueFilter(token, caseFilterDto);

        log.info("saveIssueFilter, result: {}", response.getStatus());

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
