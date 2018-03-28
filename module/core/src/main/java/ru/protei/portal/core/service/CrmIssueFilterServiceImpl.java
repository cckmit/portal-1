package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.IssueFilterDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.IssueFilter;
import ru.protei.portal.core.model.view.IssueFilterShortView;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления фильтрами обращений на DAO слое
 */
public class CrmIssueFilterServiceImpl implements CrmIssueFilterService {

    private static Logger log = LoggerFactory.getLogger(CrmIssueFilterServiceImpl.class);

    @Autowired
    IssueFilterDAO issueFilterDAO;

    @Override
    public CoreResponse< List< IssueFilterShortView > > getIssueFilterShortViewList( Long loginId ) {

        log.debug( "getIssueFilterShortViewList(): accountId={} ", loginId );

        List<IssueFilter> list = issueFilterDAO.getListByCondition( "login_id=?", loginId );

        if (list == null)
            new CoreResponse<List<IssueFilterShortView>>().error(En_ResultStatus.GET_DATA_ERROR);

        List< IssueFilterShortView > result = list.stream().map( IssueFilter::toShortView ).collect( Collectors.toList() );

        return new CoreResponse<List<IssueFilterShortView>>().success(result);
    }

    @Override
    public CoreResponse< IssueFilter > getIssueFilter( Long id ) {

        log.debug( "getIssueFilter(): id={} ", id );

        IssueFilter filter = issueFilterDAO.get(id);

        return filter != null ? new CoreResponse<IssueFilter>().success(filter)
                : new CoreResponse<IssueFilter>().error( En_ResultStatus.NOT_FOUND);
    }

    @Override
    public CoreResponse< IssueFilter > saveIssueFilter( IssueFilter filter ) {

        log.debug( "saveIssueFilter(): filter={} ", filter );

        if ( issueFilterDAO.saveOrUpdate(filter)) {
            return new CoreResponse<IssueFilter>().success(filter);
        }

        return new CoreResponse<IssueFilter>().error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    public CoreResponse< Boolean > removeIssueFilter( Long id ) {

        log.debug( "removeIssueFilter(): id={} ", id );

        if ( issueFilterDAO.removeByKey( id ) ) {
            return new CoreResponse< Boolean >().success( true );
        }

        return new CoreResponse< Boolean >().error( En_ResultStatus.INTERNAL_ERROR );
    }
}
