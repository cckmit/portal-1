package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_CompanyCategory.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;

/**
 * Реализация сервиса управления компаниями
 */
public class CompanyServiceImpl implements CompanyService {

    private static Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);
    private boolean YOUTRACK_INTEGRATION_ENABLED;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    CompanyImportanceItemDAO companyImportanceItemDAO;

    @Autowired
    CompanyGroupDAO companyGroupDAO;

    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Autowired
    YoutrackService youtrackService;

    @Autowired
    PortalConfig portalConfig;

    @PostConstruct
    public void setYoutrackIntergationEnabled () {
        YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackEnabled();
    }

    @Override
    public Result<SearchResult<Company>> getCompanies( AuthToken token, CompanyQuery query) {

        applyFilterByScope(token, query);

        SearchResult<Company> sr = companyDAO.getSearchResultByQuery(query);

        return ok(sr);
    }

    @Override
    public Result<List<EntityOption>> companyOptionList( AuthToken token, CompanyQuery query) {
        List<Company> list = getCompanyList(token, query);


        if (list == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        List<EntityOption> result = list.stream()
                .sorted(( o1, o2 ) -> placeHomeCompaniesAtBegin( query, o1, o2 ) )
                .map(Company::toEntityOption).collect(Collectors.toList());

        if(query.isReverseOrder()!=null&&query.isReverseOrder()){
            Collections.reverse(result);
        }

        return ok(result);
    }

    @Override
    public Result<List<EntityOption>> companyOptionListByIds( List<Long> ids ) {
        List<Company> list = companyDAO.getListByKeys(ids);

        if (list == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        return ok(list.stream()
                .map(Company::toEntityOption)
                .collect(Collectors.toList()));
    }

    @Override
    public Result<List<CompanySubscription>> getCompanySubscriptions( Long companyId ) {
        if ( companyId == null ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<CompanySubscription> result = companySubscriptionDAO.listByCompanyId(companyId);
        return ok(result );
    }

    @Override
    public Result<List<CompanySubscription>> getCompanyWithParentCompanySubscriptions( AuthToken authToken, Long companyId ) {
        if ( companyId == null ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Company company = companyDAO.get( companyId );
        if (company == null || company.getParentCompanyId() == null) return getCompanySubscriptions( companyId );

        List<CompanySubscription> result = companySubscriptionDAO.listByCompanyIds( new HashSet<>( Arrays.asList( companyId, company.getParentCompanyId() ) ) );
        return ok(result );
    }

    @Override
    public Result<?> updateState( AuthToken makeAuthToken, Long companyId, boolean isDeprecated) {
        if (companyId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Company company = companyDAO.get(companyId);

        if (company == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        company.setArchived(isDeprecated);

        if (companyDAO.updateState(company)) {
            return ok();
        } else {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<List<EntityOption>> groupOptionList() {
        List<CompanyGroup> list = companyGroupDAO.getListByQuery(new CompanyGroupQuery(null, En_SortField.group_name, En_SortDir.ASC));

        if (list == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        List<EntityOption> result = list.stream().map(CompanyGroup::toEntityOption).collect(Collectors.toList());

        return ok(result);
    }

    @Override
    public Result<List<CompanyGroup>> groupList( CompanyGroupQuery query) {
        return Result.ok(
                companyGroupDAO.getListByQuery(query)
        );
    }

    @Override
    public Result<List<En_CompanyCategory>> categoryOptionList( boolean hasOfficial) {

        List<En_CompanyCategory> list;

        if(!hasOfficial) {
            list = listOf( CUSTOMER, PARTNER, SUBCONTRACTOR );
        } else {
            list = listOf( En_CompanyCategory.values() );
        }

        return ok(list);
    }

    @Override
    public Result<Company> getCompany( AuthToken token, Long id ) {

        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Company company = companyDAO.get(id);

        if (company == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }
        jdbcManyRelationsHelper.fillAll( company );

        if (company.getParentCompanyId() != null) {
            Company parentCompany = companyDAO.partialGet( company.getParentCompanyId(), "cname" );
            if (parentCompany != null) {
                company.setParentCompanyName( parentCompany.getCname() );
            }
        }

        return ok(company);
    }

    @Override
    public Result<Company> getCompanyUnsafe(AuthToken token, Long id) {
        return getCompany(token, id);
    }

    @Override
    public Result<Company> createCompany( AuthToken token, Company company ) {

        if (!isValidCompany(company)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        company.setCreated(new Date());
        Long companyId = companyDAO.persist(company);

        if (companyId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        updateCompanySubscription(company.getId(), company.getSubscriptions());
        addCommonImportanceLevels(companyId);

        if(YOUTRACK_INTEGRATION_ENABLED) {
            youtrackService.createCompany(company.getCname())
                    .ifOk(newCompanyId ->
                            log.info("createCompany(): added new company to youtrack. CompanyId = {}", newCompanyId))
                    .ifError(errorResult ->
                            log.info("createCompany(): Can't create company in youtrack. {}", errorResult)
                    );
        }

        return ok(company);
    }

    @Override
    public Result<Company> updateCompany( AuthToken token, Company company ) {

        if (!isValidCompany(company)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        String oldName = null;
        if (YOUTRACK_INTEGRATION_ENABLED && StringUtils.isNotEmpty(company.getCname())) {
            Company oldCompany = companyDAO.get(company.getId());
            if (oldCompany == null) {
                return error(En_ResultStatus.NOT_FOUND);
            }
            oldName = oldCompany.getCname();
        }

        Boolean result = companyDAO.merge(company);

        if ( !result )
            return error(En_ResultStatus.NOT_UPDATED);

        if (YOUTRACK_INTEGRATION_ENABLED && StringUtils.isNotEmpty(company.getCname()) && !company.getCname().equals(oldName)) {
            youtrackService.getCompanyByName(oldName)
                    .ifOk(companyIdByName -> {
                        youtrackService.updateCompany(companyIdByName, company.getCname())
                                .ifOk(companyId ->
                                        log.info("updateCompany(): updated company in youtrack. YoutrackCompanyId = {}", companyId))
                                .ifError(errorResult ->
                                        log.info("updateCompany(): Can't update company in youtrack. {}", errorResult)
                                );
                    })
                    .ifError(errorResult ->
                            log.info("getCompanyByName(): Can't get company in youtrack. {}", errorResult)
                    );
        }

        updateCompanySubscription(company.getId(), company.getSubscriptions());
        return ok(company);
    }

    @Override
    public Result<Boolean> isCompanyNameExists( String name, Long excludeId) {

        if (name == null || name.trim().isEmpty())
            return error(En_ResultStatus.INCORRECT_PARAMS);

        return ok(checkCompanyExists(name, excludeId));
    }

    @Override
    public Result<Boolean> isGroupNameExists( String name, Long excludeId) {

        if (name == null || name.trim().isEmpty())
            return error(En_ResultStatus.INCORRECT_PARAMS);

        return ok(checkGroupExists(name, excludeId));
    }

    @Override
    public Result<List<Long>> getAllHomeCompanyIds(AuthToken token) {
        return ok(companyDAO.getAllHomeCompanyIds());
    }

    @Override
    public Result<List<CompanyImportanceItem>> getImportanceLevels(Long companyId) {
        List<CompanyImportanceItem> result = companyImportanceItemDAO.getSortedImportanceLevels(companyId);
        return ok(result);
    }

    private void addCommonImportanceLevels(Long companyId) {
        log.info( "adding common importance levels for companyId = {}", companyId );

        List<CompanyImportanceItem> importanceItems = new ArrayList<>();

        for (En_ImportanceLevel level : En_ImportanceLevel.values(true)) {
            importanceItems.add(new CompanyImportanceItem(companyId, level.getId(), level.getId()));
        }
        companyImportanceItemDAO.persistBatch(importanceItems);
    }

    private boolean updateCompanySubscription(Long companyId, List<CompanySubscription> companySubscriptions ) {
        log.info( "binding update to linked company subscription for companyId = {}", companyId );

        List<Long> toRemoveNumberIds = companySubscriptionDAO.listIdsByCompanyId( companyId );
        if ( CollectionUtils.isEmpty(companySubscriptions) && CollectionUtils.isEmpty(toRemoveNumberIds) ) {
            return true;
        }

        List<CompanySubscription> newSubscriptions = new ArrayList<>();
        List<CompanySubscription> oldSubscriptions = new ArrayList<>();
        companySubscriptions.forEach( subscription -> {
            if ( subscription.getId() == null ) {
                subscription.setCompanyId( companyId );
                newSubscriptions.add( subscription );
            } else {
                oldSubscriptions.add( subscription );
            }
        } );

        toRemoveNumberIds.removeAll( oldSubscriptions.stream().map(CompanySubscription::getId).collect( Collectors.toList() ) );
        if ( !CollectionUtils.isEmpty( toRemoveNumberIds ) ) {
            log.info( "remove company subscriptions = {} for companyId = {}", toRemoveNumberIds, companyId );
            int countRemoved = companySubscriptionDAO.removeByKeys( toRemoveNumberIds );
            if ( countRemoved != toRemoveNumberIds.size() ) {
                return false;
            }
        }

        if ( !CollectionUtils.isEmpty( newSubscriptions ) ) {
            log.info( "persist company subscriptions = {} for companyId = {}", newSubscriptions, companyId );
            companySubscriptionDAO.persistBatch( newSubscriptions );
        }

        if ( !CollectionUtils.isEmpty( oldSubscriptions ) ) {
            log.info( "merge company subscriptions = {} for companyId = {}", oldSubscriptions, companyId );
            int countMerged = companySubscriptionDAO.mergeBatch( oldSubscriptions );
            if ( countMerged != oldSubscriptions.size() ) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidCompany(Company company) {
        return company != null
                && company.getCname() != null
                && !company.getCname().trim().isEmpty()
                && (company.getParentCompanyId() == null || isEmpty(company.getChildCompanies()) )
                /*&& isValidContactInfo(company)*/
                && !checkCompanyExists(company.getCname(), company.getId());
    }

    private List<Company> getCompanyList( AuthToken token, CompanyQuery query ) {
        applyFilterByScope( token, query );
        return companyDAO.listByQuery(query);
    }

    private boolean checkCompanyExists (String name, Long excludeId) {

        Company company = companyDAO.getCompanyByName(name);

        if (company == null)
            return false;

        if (excludeId != null && company.getId().equals(excludeId))
            return false;

        return true;
    }

    private boolean checkGroupExists (String name, Long excludeId) {

        CompanyGroup group = companyGroupDAO.getGroupByName(name);

        if (group == null)
            return false;

        if (excludeId != null && group.getId().equals(excludeId))
            return false;

        return true;
    }

    private int placeHomeCompaniesAtBegin( CompanyQuery query, Company o1,  Company o2 )  {
        if (!query.isSortHomeCompaniesAtBegin()) return 0;
        return Objects.equals( En_CompanyCategory.HOME, o1.getCategory() ) ? -1 : Objects.equals( En_CompanyCategory.HOME, o2.getCategory() ) ? 1 : 0;
    }

    private void applyFilterByScope( AuthToken token, CompanyQuery query ) {
        Set< UserRole > roles = token.getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.COMPANY_VIEW ) ) {
            query.setCompanyIds( acceptAllowedCompanies(query.getCompanyIds(), token.getCompanyAndChildIds() ) );
        }
    }

    private List<Long> acceptAllowedCompanies( List<Long> companyIds, Collection<Long> allowedCompaniesIds ) {
        if( companyIds == null ) return new ArrayList<>( allowedCompaniesIds );
        ArrayList allowedCompanies = new ArrayList( companyIds );
        allowedCompanies.retainAll( allowedCompaniesIds );
        return allowedCompanies.isEmpty() ? new ArrayList<>( allowedCompaniesIds ) : allowedCompanies;
    }
}