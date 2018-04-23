package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.service.IssueFilterService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с фильтрами обращений
 */
@Service( "IssueFilterService" )
public class IssueFilterServiceImpl implements ru.protei.portal.ui.common.client.service.IssueFilterService {

    @Override
    public List< CaseFilterShortView > getIssueFilterShortViewListByCurrentUser() throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug( "getIssueFilterShortViewListByCurrentUser(): accountId={} ", descriptor.getLogin().getId() );

        CoreResponse<List<CaseFilterShortView >> response = issueFilterService.getIssueFilterShortViewList( descriptor.getLogin().getId() );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public CaseFilter getIssueFilter( Long id ) throws RequestFailedException {
        log.debug("getIssueFilter, id: {}", id);

        CoreResponse<CaseFilter > response = issueFilterService.getIssueFilter( id );

        log.debug("getIssueFilter, id: {}, response: {} ", id, response.isError() ? "error" : response.getData());

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public CaseFilter saveIssueFilter( CaseFilter filter ) throws RequestFailedException {

        log.debug("saveIssueFilter, filter: {}", filter);

        if (filter == null) {
            log.warn("Not null issueFilter is required");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        if (filter.getLoginId() == null){
            UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
            filter.setLoginId( descriptor.getLogin().getId() );
        }

        filter.getParams().setSortField( En_SortField.creation_date );
        CoreResponse<CaseFilter > response = issueFilterService.saveIssueFilter( filter );

        log.debug("saveIssueFilter, result: {}", response.getStatus());

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public boolean removeIssueFilter( Long id ) throws RequestFailedException {
        log.debug( "removeIssueFilter(): id={}", id );

        CoreResponse< Boolean > response = issueFilterService.removeIssueFilter( id );
        log.debug( "removeIssueFilter(): result={}", response.getStatus() );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        log.info( "userSessionDescriptor={}", descriptor );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
    }

    @Autowired
    IssueFilterService issueFilterService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    SessionService sessionService;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}
