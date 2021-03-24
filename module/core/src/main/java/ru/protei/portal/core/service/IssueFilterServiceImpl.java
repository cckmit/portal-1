package ru.protei.portal.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseFilterDAO;
import ru.protei.portal.core.model.dao.PersonCaseFilterDAO;
import ru.protei.portal.core.model.dao.PlanDAO;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.CaseTagQuery;
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
    public Result<CaseFilterDto<CaseQuery>> getIssueFilter(AuthToken token, Long id) {
        log.debug( "getIssueFilter(): id={} ", id );

        CaseFilter filter = caseFilterDAO.get( id );

        if (filter == null) {
            return error( En_ResultStatus.NOT_FOUND );
        }

        if (!filter.getType().getQueryClass().equals(CaseQuery.class)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseQuery caseQuery;
        try {
            caseQuery = objectMapper.readValue(filter.getParams(), CaseQuery.class);
        } catch (IOException e) {
            log.warn("processMailNotification: cannot read filter params: caseFilter={}", filter);
            e.printStackTrace();
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        Result<SelectorsParams> selectorsParams = getSelectorsParams(token, caseQuery);

        if (selectorsParams.isError()) {
            return error( selectorsParams.getStatus() );
        }

        filter.setSelectorsParams(selectorsParams.getData());

        return ok( new CaseFilterDto<>(filter, caseQuery) );
    }

    @Override
    public Result<SelectorsParams> getSelectorsParams( AuthToken token, CaseQuery caseQuery ) {
        log.debug( "getSelectorsParams(): caseQuery={} ", caseQuery );
        SelectorsParams selectorsParams = new SelectorsParams();

        List<Long> companyIds = collectCompanyIds(caseQuery);
        if (!isEmpty(companyIds)) {
            Result<List<EntityOption>> result = companyService.companyOptionListByIds( token, filterToList(companyIds, Objects::nonNull ));
            if (result.isOk()) {
                selectorsParams.setCompanyEntityOptions(result.getData());
            } else {
                return error(result.getStatus(), "Error at getCompanyIds" );
            }
        }

        List<Long> personIds = collectPersonIds( caseQuery );
        if (!isEmpty( personIds )) {
            Result<List<PersonShortView>> result = personService.shortViewListByIds( personIds );
            if (result.isOk()) {
                selectorsParams.setPersonShortViews(result.getData());
            } else {
                return error(result.getStatus(), "Error at getPersonIds" );
            }
        }


        if (!isEmpty( caseQuery.getProductIds() )) {
            Result<List<ProductShortView>> result = productService.shortViewListByIds( token, filterToList( caseQuery.getProductIds(), Objects::nonNull ) );
            if (result.isOk()) {
                selectorsParams.setProductShortViews(result.getData());
            } else {
                return error(result.getStatus(), "Error at getProductIds" );
            }
        }

        List<Long> tagIds = filterToList( caseQuery.getCaseTagsIds(), Objects::nonNull );
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

        if (caseQuery.getPlanId() != null) {
            Plan plan = planDAO.get(caseQuery.getPlanId());
            selectorsParams.setPlanOption(new PlanOption(plan.getId(), plan.getName(), plan.getCreatorId()));
        }

        return ok(selectorsParams);
    }

    @Override
    @Transactional
    public Result<CaseFilterDto<CaseQuery>> saveIssueFilter(AuthToken token, CaseFilterDto<CaseQuery> caseFilterDto) {
        log.debug("saveIssueFilter(): filter={} ", caseFilterDto);

        CaseFilter caseFilter = caseFilterDto.getCaseFilter();

        if (isNotValid(caseFilterDto)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (caseFilter.getLoginId() == null) {
            caseFilter.setLoginId(token.getUserLoginId());
        }

        CaseQuery caseQuery = applyFilterByScope(token, caseFilterDto.getQuery());

        String params;

        try {
            params = objectMapper.writeValueAsString(caseQuery);
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

    private boolean isNotValid( CaseFilterDto<CaseQuery> caseFilterDto ) {
        return caseFilterDto == null ||
                caseFilterDto.getCaseFilter().getType() == null ||
                HelperFunc.isEmpty(caseFilterDto.getCaseFilter().getName()) ||
                caseFilterDto.getQuery() == null;
    }

    private CaseQuery applyFilterByScope(AuthToken token, CaseQuery caseQuery) {
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

    private List<Long> collectPersonIds(CaseQuery caseQuery){
        ArrayList<Long> personsIds = new ArrayList<>();
        personsIds.addAll(emptyIfNull(caseQuery.getManagerIds()));
        personsIds.addAll(emptyIfNull(caseQuery.getInitiatorIds()));
        personsIds.addAll(emptyIfNull(caseQuery.getCommentAuthorIds()));
        personsIds.addAll(emptyIfNull(caseQuery.getCreatorIds()));
        return personsIds;
    }

    private List<Long> collectCompanyIds(CaseQuery caseQuery) {
        List<Long> companyIds = new ArrayList<>();
        companyIds.addAll(emptyIfNull(caseQuery.getCompanyIds()));
        companyIds.addAll(emptyIfNull(caseQuery.getManagerCompanyIds()));

        return companyIds;
    }
}
