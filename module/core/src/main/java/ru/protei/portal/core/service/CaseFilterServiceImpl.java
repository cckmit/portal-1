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
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.view.*;
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
public class CaseFilterServiceImpl implements CaseFilterService {

    private static Logger log = LoggerFactory.getLogger( CaseFilterServiceImpl.class );

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
    @Autowired
    SiteFolderService siteFolderService;

    @Override
    public Result<List<FilterShortView>> getCaseFilterShortViewList(Long loginId, En_CaseFilterType filterType ) {

        log.debug( "getIssueFilterShortViewList(): accountId={}, filterType={} ", loginId, filterType );

        List< CaseFilter > list = caseFilterDAO.getListByLoginIdAndFilterType( loginId, filterType );

        if ( list == null )
            return error(En_ResultStatus.GET_DATA_ERROR );

        List< FilterShortView > result = list.stream().map( CaseFilter::toShortView ).collect( Collectors.toList() );

        return ok(result);
    }

    @Override
    public Result<CaseFilterDto<HasFilterQueryIds>> getCaseFilterDto(AuthToken token, Long id) {
        log.debug( "getIssueFilter(): id={} ", id );

        CaseFilter filter = caseFilterDAO.get( id );

        if (filter == null) {
            return error( En_ResultStatus.NOT_FOUND );
        }

        Result<? extends HasFilterQueryIds> readValueResult
                = readValue(filter.getParams(), filter.getType().getQueryClass());

        if (readValueResult.isError()) {
            return error(readValueResult.getStatus());
        }

        HasFilterQueryIds query = readValueResult.getData();

        Result<SelectorsParams> selectorsParams = getSelectorsParams(token, query);

        if (selectorsParams.isError()) {
            return error( selectorsParams.getStatus() );
        }

        filter.setSelectorsParams(selectorsParams.getData());

        return ok( new CaseFilterDto<>(filter, query) );
    }

    @Override
    public Result<SelectorsParams> getSelectorsParams( AuthToken token, HasFilterQueryIds filterEntityIds ) {
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

        if (!isEmpty( filterEntityIds.getAllPlatformIds() )) {
            Result<List<PlatformOption>> result = siteFolderService.listPlatformsOptionList( token, new PlatformQuery() );
            if (result.isOk()) {
                selectorsParams.setPlatforms(result.getData());
            } else {
                return error(result.getStatus(), "Error at getPlatforms" );
            }
        }

        return ok(selectorsParams);
    }

    @Override
    @Transactional
    public Result<CaseFilterDto<ProjectQuery>> saveProjectFilter(AuthToken token, CaseFilterDto<ProjectQuery> caseFilterDto) {
        log.debug("saveProjectFilter(): caseFilterDto={} ", caseFilterDto);

        if (isNotValid(caseFilterDto)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return saveCaseFilter(token, caseFilterDto);
    }

    @Override
    @Transactional
    public Result<CaseFilterDto<DeliveryQuery>> saveDeliveryFilter(AuthToken token, CaseFilterDto<DeliveryQuery> caseFilterDto) {
        log.debug("saveDeliveryFilter(): caseFilterDto={} ", caseFilterDto);

        if (isNotValid(caseFilterDto)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return saveCaseFilter(token, caseFilterDto);
    }

    @Override
    @Transactional
    public Result<CaseFilterDto<CaseQuery>> saveIssueFilter(AuthToken token, CaseFilterDto<CaseQuery> caseFilterDto) {
        log.debug("saveIssueFilter(): caseFilterDto={} ", caseFilterDto);

        if (isNotValid(caseFilterDto)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        caseFilterDto.setQuery(applyCaseQueryByScope(token, caseFilterDto.getQuery()));

        return saveCaseFilter(token, caseFilterDto);
    }

    @Override
    @Transactional
    public Result<Long> removeCaseFilter(AuthToken token, Long id ) {
        log.debug( "removeIssueFilter(): id={} ", id );

        if (personCaseFilterDAO.isUsed(id)) {
            return error(En_ResultStatus.ISSUE_FILTER_IS_USED);
        }

        if (!caseFilterDAO.removeByKey(id)) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(id);
    }

    private <T extends HasFilterQueryIds> Result<CaseFilterDto<T>> saveCaseFilter(AuthToken token, CaseFilterDto<T> caseFilterDto) {
        CaseFilter caseFilter = caseFilterDto.getCaseFilter();

        if (caseFilter.getLoginId() == null) {
            caseFilter.setLoginId(token.getUserLoginId());
        }

        T query = caseFilterDto.getQuery();

        Result<String> writeValueResult = writeValueAsString(query);

        if (writeValueResult.isError()) {
            return error(writeValueResult.getStatus());
        }

        String params = writeValueResult.getData();

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

    private <T> Result<T> readValue(String params, Class<T> clazz) {
        try {
            return ok(objectMapper.readValue(params, clazz));
        } catch (IOException e) {
            log.warn("readValue(): cannot deserialize params. params={}, class={}", params, clazz);
            e.printStackTrace();
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
    }

    private Result<String> writeValueAsString(Object object) {
        try {
            return ok(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            log.warn("writeValueAsString(): cannot serialize object. object={}", object);
            e.printStackTrace();
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    private boolean isNotValid(CaseFilterDto<?> caseFilterDto) {
        return !isValid(caseFilterDto);
    }

    private boolean isValid(CaseFilterDto<?> caseFilterDto ) {
        if (caseFilterDto == null) {
            return false;
        }

        CaseFilter caseFilter = caseFilterDto.getCaseFilter();

        if (caseFilter == null) {
            return false;
        }

        if (caseFilter.getType() == null) {
            return false;
        }

        if (StringUtils.isBlank(caseFilter.getName())) {
            return false;
        }

        if (caseFilterDto.getQuery() == null) {
            return false;
        }

        if (!caseFilter.getType().getQueryClass().equals(caseFilterDto.getQuery().getClass())) {
            return false;
        }

        return true;
    }

    private CaseQuery applyCaseQueryByScope(AuthToken token, CaseQuery caseQuery) {
        Set<UserRole> roles = token.getRoles();
        if (policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
            return caseQuery;
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

        return caseQuery;
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
