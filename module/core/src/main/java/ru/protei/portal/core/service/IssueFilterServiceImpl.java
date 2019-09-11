package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseFilterDAO;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.auth.AuthService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
/**
 * Реализация сервиса управления фильтрами обращений на DAO слое
 */
public class IssueFilterServiceImpl implements IssueFilterService {

    private static Logger log = LoggerFactory.getLogger( IssueFilterServiceImpl.class );

    @Autowired
    CaseFilterDAO caseFilterDAO;

    @Autowired
    AuthService authService;

    @Autowired
    PolicyService policyService;

    @Override
    public Result< List< CaseFilterShortView > > getIssueFilterShortViewList( Long loginId, En_CaseFilterType filterType ) {

        log.debug( "getIssueFilterShortViewList(): accountId={}, filterType={} ", loginId, filterType );

        List< CaseFilter > list = caseFilterDAO.getListByLoginIdAndFilterType( loginId, filterType );

        if ( list == null )
            return error(En_ResultStatus.GET_DATA_ERROR );

        List< CaseFilterShortView > result = list.stream().map( CaseFilter::toShortView ).collect( Collectors.toList() );

        return ok(result );
    }

    @Override
    public Result< CaseFilter > getIssueFilter( Long id ) {

        log.debug( "getIssueFilter(): id={} ", id );

        CaseFilter filter = caseFilterDAO.get( id );

        return filter != null ? ok( filter )
                : error( En_ResultStatus.NOT_FOUND );
    }

    @Override
    public Result<CaseFilter> saveIssueFilter( AuthToken token, CaseFilter filter) {

        log.debug("saveIssueFilter(): filter={} ", filter);

        if (isNotValid(filter)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        UserSessionDescriptor descriptor = authService.findSession(token);
        if (filter.getLoginId() == null) {
            filter.setLoginId(descriptor.getLogin().getId());
        }
        applyFilterByScope(descriptor, filter);

        if (caseFilterDAO.saveOrUpdate(filter)) {
            return ok(filter);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    public Result< Boolean > removeIssueFilter( Long id ) {

        log.debug( "removeIssueFilter(): id={} ", id );

        if ( caseFilterDAO.removeByKey( id ) ) {
            return ok(true );
        }

        return error(En_ResultStatus.INTERNAL_ERROR );
    }

    private boolean isNotValid( CaseFilter filter ) {
        return filter == null ||
                filter.getType() == null ||
                filter.getName() == null ||
                filter.getParams() == null;
    }

    private void applyFilterByScope(UserSessionDescriptor descriptor, CaseFilter filter) {
        Set<UserRole> roles = descriptor.getLogin().getRoles();
        if (!policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
            CaseQuery query = filter.getParams();
            query.setCompanyIds(acceptAllowedCompanies(query.getCompanyIds(), descriptor.getAllowedCompaniesIds()));
            query.setAllowViewPrivate(false);
        }
    }

    private List<Long> acceptAllowedCompanies( List<Long> companyIds, Collection<Long> allowedCompaniesIds ) {
        if( companyIds == null ) return new ArrayList<>( allowedCompaniesIds );
        ArrayList allowedCompanies = new ArrayList( companyIds );
        allowedCompanies.retainAll( allowedCompaniesIds );
        return allowedCompanies.isEmpty() ? new ArrayList<>( allowedCompaniesIds ) : allowedCompanies;
    }
}
