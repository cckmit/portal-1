package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseFilterDAO;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.IssueFilterParams;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

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
    @Autowired
    CompanyService companyService;
    @Autowired
    PersonService personService;
    @Autowired
    ProductService productService;

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
    public Result< IssueFilterParams > getIssueFilter( Long id ) {
        log.debug( "getIssueFilter(): id={} ", id );

        CaseFilter filter = caseFilterDAO.get( id );

        if (filter == null) {
            return error( En_ResultStatus.NOT_FOUND );
        }

        CaseQuery caseQuery = filter.getParams();
        IssueFilterParams filterParams = caseQuery.getIssueFilterParams();

        filterParams.setName(filter.getName());
        filterParams.setCaseQuery(caseQuery);

        filterParams.setSearchString(caseQuery.getSearchString());
        filterParams.setSearchStringAtComments(caseQuery.isSearchStringAtComments());
        filterParams.setViewPrivate(caseQuery.isViewPrivate());
        filterParams.setSortDir(caseQuery.getSortDir());
        filterParams.setSortField(caseQuery.getSortField());
        filterParams.setCreatedFrom(caseQuery.getCreatedFrom());
        filterParams.setCreatedTo(caseQuery.getCreatedTo());
        filterParams.setModifiedFrom(caseQuery.getModifiedFrom());
        filterParams.setModifiedTo(caseQuery.getModifiedTo());
        filterParams.setImportanceIds(caseQuery.getImportanceIds());
        filterParams.setStateIds(caseQuery.getStateIds());
        filterParams.setCaseTagsIds(caseQuery.getCaseTagsIds());

        if (!isEmpty(caseQuery.getCompanyIds())) {
            Result<List<EntityOption>> result = companyService.companyOptionListByIds(caseQuery.getCompanyIds());
            if (result.isOk()) {
                filterParams.setCompanyEntityOptions(result.getData());
            } else {
                return error(result.getStatus(), "Error at getCompanyIds" );
            }
        }

        if (!isEmpty(caseQuery.getManagerIds())) {
            Result<List<PersonShortView>> result = personService.shortViewListByIds(caseQuery.getManagerIds());
            if (result.isOk()) {
                filterParams.setManagerPersonShortView(result.getData());
            } else {
                return error(result.getStatus(), "Error at getManagerIds" );
            }
        }

        if (!isEmpty(caseQuery.getInitiatorIds())) {
            Result<List<PersonShortView>> result = personService.shortViewListByIds(caseQuery.getInitiatorIds());
            if (result.isOk()) {
                filterParams.setInitiatorPersonShortView(result.getData());
            } else {
                return error(result.getStatus(), "Error at getInitiatorIds" );
            }
        }

        if (!isEmpty(caseQuery.getProductIds())) {
            Result<List<ProductShortView>> result = productService.shortViewListByIds(new ArrayList<>(caseQuery.getProductIds()));
            if (result.isOk()) {
                filterParams.setProductShortView(result.getData());
            } else {
                return error(result.getStatus(), "Error at getProductIds" );
            }
        }

        if (!isEmpty(caseQuery.getCommentAuthorIds())) {
            Result<List<PersonShortView>> result = personService.shortViewListByIds(caseQuery.getCommentAuthorIds());
            if (result.isOk()) {
                filterParams.setCommentPersonShortView(result.getData());
            } else {
                return error(result.getStatus(), "Error at getCommentAuthorIds" );
            }
        }

        return  ok( filterParams );
    }

    @Override
    public Result<CaseFilter> saveIssueFilter(AuthToken token, CaseFilter filter) {

        log.debug("saveIssueFilter(): filter={} ", filter);

        if (isNotValid(filter)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (filter.getLoginId() == null) {
            filter.setLoginId(token.getUserLoginId());
        }
        applyFilterByScope(token, filter);

        filter.setName(filter.getName().trim());

        if (!isUniqueFilter(filter.getName(), filter.getLoginId(), filter.getType(), filter.getId())) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

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
                HelperFunc.isEmpty(filter.getName()) ||
                filter.getParams() == null;
    }

    private void applyFilterByScope(AuthToken token, CaseFilter filter) {
        Set<UserRole> roles = token.getRoles();
        if (!policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
            CaseQuery query = filter.getParams();
            query.setCompanyIds(acceptAllowedCompanies(query.getCompanyIds(), token.getCompanyAndChildIds()));
            query.setAllowViewPrivate(false);
        }
    }

    private List<Long> acceptAllowedCompanies( List<Long> companyIds, Collection<Long> allowedCompaniesIds ) {
        if( companyIds == null ) return new ArrayList<>( allowedCompaniesIds );
        ArrayList allowedCompanies = new ArrayList( companyIds );
        allowedCompanies.retainAll( allowedCompaniesIds );
        return allowedCompanies.isEmpty() ? new ArrayList<>( allowedCompaniesIds ) : allowedCompanies;
    }

    private boolean isUniqueFilter( String name, Long loginId, En_CaseFilterType type, Long excludeId ) {
        CaseFilter caseFilter = caseFilterDAO.checkExistsByParams( name, loginId, type );
        return caseFilter == null || caseFilter.getId().equals( excludeId );
    }
}
