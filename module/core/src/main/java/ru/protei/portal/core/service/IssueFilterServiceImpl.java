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
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
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
    public Result<CaseFilter> getIssueFilter(Long id ) {
        log.debug( "getIssueFilter(): id={} ", id );

        CaseFilter filter = caseFilterDAO.get( id );

        if (filter == null) {
            return error( En_ResultStatus.NOT_FOUND );
        }

        Result<SelectorsParams> selectorsParams = getSelectorsParams(makeSelectorsParamsRequest(filter.getParams()));

        if (selectorsParams.isError()) {
            return error( selectorsParams.getStatus() );
        }

        filter.setSelectorsParams(selectorsParams.getData());

        return  ok( filter );
    }

    @Override
    public Result<SelectorsParams> getSelectorsParams(SelectorsParamsRequest request) {
        log.debug( "getSelectorsParams(): request={} ", request );
        SelectorsParams selectorsParams = new SelectorsParams();

        if (!isEmpty(request.getCompanyIds())) {
            Result<List<EntityOption>> result = companyService.companyOptionListByIds(request.getCompanyIds());
            if (result.isOk()) {
                selectorsParams.setCompanyEntityOptions(result.getData());
            } else {
                return error(result.getStatus(), "Error at getCompanyIds" );
            }
        }

        if (!isEmpty(request.getPersonIds())) {
            Result<List<PersonShortView>> result = personService.shortViewListByIds(request.getPersonIds());
            if (result.isOk()) {
                selectorsParams.setPersonShortViews(result.getData());
            } else {
                return error(result.getStatus(), "Error at getPersonIds" );
            }
        }


        if (!isEmpty(request.getProductIds())) {
            Result<List<ProductShortView>> result = productService.shortViewListByIds(new ArrayList<>(request.getProductIds()));
            if (result.isOk()) {
                selectorsParams.setProductShortViews(result.getData());
            } else {
                return error(result.getStatus(), "Error at getProductIds" );
            }
        }

        return ok(selectorsParams);
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

    private SelectorsParamsRequest makeSelectorsParamsRequest(CaseQuery caseQuery) {
        SelectorsParamsRequest request = new SelectorsParamsRequest();

        request.setCompanyIds(emptyIfNull(caseQuery.getCompanyIds()).stream().filter(Objects::nonNull).collect(Collectors.toList()));

        Set<Long> personsIds = new HashSet<>();
        personsIds.addAll(emptyIfNull(caseQuery.getManagerIds()));
        personsIds.addAll(emptyIfNull(caseQuery.getInitiatorIds()));
        personsIds.addAll(emptyIfNull(caseQuery.getCommentAuthorIds()));
        request.setPersonIds(personsIds.stream().filter(Objects::nonNull).collect(Collectors.toList()));

        request.setProductIds(emptyIfNull(caseQuery.getProductIds()).stream().filter(Objects::nonNull).collect(Collectors.toList()));

        return request;
    }
}
