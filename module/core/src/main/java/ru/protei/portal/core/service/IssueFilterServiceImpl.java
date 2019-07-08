package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseFilterDAO;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления фильтрами обращений на DAO слое
 */
public class IssueFilterServiceImpl implements IssueFilterService {

    private static Logger log = LoggerFactory.getLogger( IssueFilterServiceImpl.class );

    @Autowired
    CaseFilterDAO caseFilterDAO;

    @Override
    public CoreResponse< List< CaseFilterShortView > > getIssueFilterShortViewList( Long loginId, En_CaseFilterType filterType ) {

        log.debug( "getIssueFilterShortViewList(): accountId={}, filterType={} ", loginId, filterType );

        List< CaseFilter > list = caseFilterDAO.getListByLoginIdAndFilterType( loginId, filterType );

        if ( list == null )
            return new CoreResponse< List< CaseFilterShortView > >().error( En_ResultStatus.GET_DATA_ERROR );

        List< CaseFilterShortView > result = list.stream().map( CaseFilter::toShortView ).collect( Collectors.toList() );

        return new CoreResponse< List< CaseFilterShortView > >().success( result );
    }

    @Override
    public CoreResponse< CaseFilter > getIssueFilter( Long id ) {

        log.debug( "getIssueFilter(): id={} ", id );

        CaseFilter filter = caseFilterDAO.get( id );

        return filter != null ? new CoreResponse< CaseFilter >().success( filter )
                : new CoreResponse< CaseFilter >().error( En_ResultStatus.NOT_FOUND );
    }

    @Override
    public CoreResponse< CaseFilter > saveIssueFilter( CaseFilter filter ) {

        if ( isNotValid( filter ) ) {
            return new CoreResponse().error( En_ResultStatus.INCORRECT_PARAMS );
        }

        log.debug( "saveIssueFilter(): filter={} ", filter );

        if ( caseFilterDAO.saveOrUpdate( filter ) ) {
            return new CoreResponse< CaseFilter >().success( filter );
        }

        return new CoreResponse< CaseFilter >().error( En_ResultStatus.INTERNAL_ERROR );
    }

    @Override
    public CoreResponse< Boolean > removeIssueFilter( Long id ) {

        log.debug( "removeIssueFilter(): id={} ", id );

        if ( caseFilterDAO.removeByKey( id ) ) {
            return new CoreResponse< Boolean >().success( true );
        }

        return new CoreResponse< Boolean >().error( En_ResultStatus.INTERNAL_ERROR );
    }

    private boolean isNotValid( CaseFilter filter ) {
        return filter != null ||
                filter.getType() == null ||
                filter.getLoginId() == null ||
                filter.getName() == null ||
                filter.getParams() == null;
    }
}
