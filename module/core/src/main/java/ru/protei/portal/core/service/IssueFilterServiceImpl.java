package ru.protei.portal.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseFilterDAO;
import ru.protei.portal.core.model.dao.LocationDAO;
import ru.protei.portal.core.model.dao.PersonCaseFilterDAO;
import ru.protei.portal.core.model.dao.PlanDAO;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.model.query.HasFilterEntityIds;
import ru.protei.portal.core.model.view.*;
import ru.protei.portal.core.model.view.filterwidget.AbstractFilterShortView;
import ru.protei.portal.core.model.view.filterwidget.DtoFilterQuery;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

/**
 * Реализация сервиса управления фильтрами обращений на DAO слое
 */
public class IssueFilterServiceImpl implements IssueFilterService {

    private static Logger log = LoggerFactory.getLogger( IssueFilterServiceImpl.class );

    @Autowired
    CaseFilterDAO caseFilterDAO;
    @Autowired
    PersonCaseFilterDAO personCaseFilterDAO;
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
    @Autowired
    CaseTagService caseTagService;
    @Autowired
    PlanDAO planDAO;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LocationDAO locationDAO;

    @Override
    public Result< List<AbstractFilterShortView> > getIssueFilterShortViewList(Long loginId, En_CaseFilterType filterType ) {

        log.debug( "getIssueFilterShortViewList(): accountId={}, filterType={} ", loginId, filterType );

        List< CaseFilter > list = caseFilterDAO.getListByLoginIdAndFilterType( loginId, filterType );

        if ( list == null )
            return error(En_ResultStatus.GET_DATA_ERROR );

        List< AbstractFilterShortView > result = list.stream().map( CaseFilter::toShortView ).collect( Collectors.toList() );

        return ok(result);
    }

    @Override
    public <T extends DtoFilterQuery> Result<CaseFilterDto<T>> getIssueFilter(AuthToken token, Long id) {
        log.debug( "getIssueFilter(): id={} ", id );

        CaseFilter filter = caseFilterDAO.get( id );

        if (filter == null) {
            return error( En_ResultStatus.NOT_FOUND );
        }

        T query;
        try {
            query = (T) objectMapper.readValue(filter.getParams(), filter.getType().getQueryClass());
        } catch (IOException e) {
            log.warn("processMailNotification: cannot read filter params: caseFilter={}", filter);
            e.printStackTrace();
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        Result<SelectorsParams> selectorsParams = getSelectorsParams(token, query);

        if (selectorsParams.isError()) {
            return error( selectorsParams.getStatus() );
        }

        filter.setSelectorsParams(selectorsParams.getData());

        return ok( new CaseFilterDto<>(filter, query) );
    }

    @Override
    public Result<SelectorsParams> getSelectorsParams( AuthToken token, HasFilterEntityIds filterEntityIds ) {
        log.debug( "getSelectorsParams(): filterEntityIds={} ", filterEntityIds );
        SelectorsParams selectorsParams = new SelectorsParams();

        List<Long> companyIds = filterEntityIds.getAllCompanyIds();
        if (!isEmpty(companyIds)) {
            Result<List<EntityOption>> result = companyService.companyOptionListByIds( token, filterToList(companyIds, Objects::nonNull ));
            if (result.isOk()) {
                selectorsParams.setCompanyEntityOptions(result.getData());
            } else {
                return error(result.getStatus(), "Error at getCompanyIds" );
            }
        }

        List<Long> personIds = filterEntityIds.getAllPersonIds();
        if (!isEmpty( personIds )) {
            Result<List<PersonShortView>> result = personService.shortViewListByIds( personIds );
            if (result.isOk()) {
                selectorsParams.setPersonShortViews(result.getData());
            } else {
                return error(result.getStatus(), "Error at getPersonIds" );
            }
        }

        if (!isEmpty( filterEntityIds.getAllProductIds() )) {
            Result<List<ProductShortView>> result = productService.shortViewListByIds( token, filterToList( filterEntityIds.getAllProductIds(), Objects::nonNull ) );
            if (result.isOk()) {
                selectorsParams.setProductShortViews(result.getData());
            } else {
                return error(result.getStatus(), "Error at getProductIds" );
            }
        }

        List<Long> tagIds = filterToList( filterEntityIds.getAllTagIds(), Objects::nonNull );
        if (!isEmpty( tagIds )) {
            CaseTagQuery caseTagQuery = new CaseTagQuery();
            caseTagQuery.setIds( tagIds );

            Result<List<CaseTag>> result = caseTagService.getTags(token, caseTagQuery );
            if (result.isOk()) {
                selectorsParams.setCaseTags(result.getData());
            } else {
                return error(result.getStatus(), "Can't get tags by ids." );
            }
        }

        List<Long> regionIds = filterToList( filterEntityIds.getAllRegionIds(), Objects::nonNull );
        if (!isEmpty( regionIds )) {
            selectorsParams.setRegions(toList(locationDAO.getListByKeys(regionIds), Location::toEntityOption));
        }

        List<Long> directionIds = filterToList(filterEntityIds.getAllDirectionIds(), Objects::nonNull);
        if (isNotEmpty(directionIds)) {
            Result<List<ProductDirectionInfo>> result = productService.productDirectionList( token, directionIds );
            if (result.isOk()) {
                selectorsParams.setProductDirectionInfos(result.getData());
            } else {
                return error(result.getStatus(), "Can't get directions by directionIds" );
            }
        }

        if (filterEntityIds.getPlanId() != null) {
            Plan plan = planDAO.get(filterEntityIds.getPlanId());
            selectorsParams.setPlanOption(new PlanOption(plan.getId(), plan.getName(), plan.getCreatorId()));
        }

        return ok(selectorsParams);
    }

    @Override
    @Transactional
    public <T extends DtoFilterQuery> Result<CaseFilterDto<T>> saveIssueFilter(AuthToken token, CaseFilterDto<T> caseFilterDto) {
        log.debug("saveIssueFilter(): filter={} ", caseFilterDto);

        if (isNotValid(caseFilterDto)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseFilter caseFilter = caseFilterDto.getCaseFilter();

        if (caseFilter.getLoginId() == null) {
            caseFilter.setLoginId(token.getUserLoginId());
        }

        if (CaseQuery.class.equals(caseFilter.getType().getQueryClass())) {
            caseFilterDto.setQuery(applyFilterByScope(token, caseFilterDto.getQuery()));
        }

        T query = caseFilterDto.getQuery();

        String params;

        try {
            params = objectMapper.writeValueAsString(query);
        } catch (JsonProcessingException e) {
            log.warn("saveIssueFilter(): cannot write filter params: caseFilter={}", caseFilter);
            e.printStackTrace();
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        caseFilter.setParams(params);
        caseFilter.setName(caseFilter.getName().trim());

        if (!isUniqueFilter(caseFilter.getName(), caseFilter.getLoginId(), caseFilter.getType(), caseFilter.getId())) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        if (caseFilterDAO.saveOrUpdate(caseFilter)) {
            caseFilterDto.setCaseFilter(caseFilter);
            return ok(caseFilterDto);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    @Transactional
    public Result<Long> removeIssueFilter(AuthToken token, Long id ) {
        log.debug( "removeIssueFilter(): id={} ", id );

        if (personCaseFilterDAO.isUsed(id)) {
            return error(En_ResultStatus.ISSUE_FILTER_IS_USED);
        }

        if (!caseFilterDAO.removeByKey(id)) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(id);
    }

    private boolean isNotValid( CaseFilterDto<?> caseFilterDto ) {
        return caseFilterDto == null ||
                caseFilterDto.getCaseFilter() == null ||
                caseFilterDto.getCaseFilter().getType() == null ||
                HelperFunc.isEmpty(caseFilterDto.getCaseFilter().getName()) ||
                caseFilterDto.getQuery() == null;
    }

    private <T extends FilterQuery> T applyFilterByScope(AuthToken token, T typedCaseQuery) {
        CaseQuery caseQuery = (CaseQuery) typedCaseQuery;

        caseQuery.setCompanyIds(Arrays.asList(111L,222L,333L));

        Set<UserRole> roles = token.getRoles();
        if (policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
            return typedCaseQuery;
        }

        Company company = companyService.getCompanyOmitPrivileges(token, token.getCompanyId()).getData();
        if (company.getCategory() == En_CompanyCategory.SUBCONTRACTOR) {
            caseQuery.setManagerCompanyIds(
                    acceptAllowedCompanies(caseQuery.getManagerCompanyIds(), token.getCompanyAndChildIds()));
        } else {
            caseQuery.setCompanyIds(
                    acceptAllowedCompanies(caseQuery.getCompanyIds(), token.getCompanyAndChildIds()));
        }
        caseQuery.setAllowViewPrivate(false);

        log.info("applyFilterByScope(): CaseQuery modified: {}", caseQuery);

        return typedCaseQuery;
    }

    private List<Long> acceptAllowedCompanies( List<Long> companyIds, Collection<Long> allowedCompaniesIds ) {
        if( companyIds == null ) return new ArrayList<>( allowedCompaniesIds );
        ArrayList<Long> allowedCompanies = new ArrayList<>( companyIds );
        allowedCompanies.retainAll( allowedCompaniesIds );
        return allowedCompanies.isEmpty() ? new ArrayList<>( allowedCompaniesIds ) : allowedCompanies;
    }

    private boolean isUniqueFilter( String name, Long loginId, En_CaseFilterType type, Long excludeId ) {
        CaseFilter caseFilter = caseFilterDAO.checkExistsByParams( name, loginId, type );
        return caseFilter == null || caseFilter.getId().equals( excludeId );
    }
}
