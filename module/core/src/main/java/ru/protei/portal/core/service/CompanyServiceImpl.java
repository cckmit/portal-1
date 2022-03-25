package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_CompanyCategory.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

/**
 * Реализация сервиса управления компаниями
 */
public class CompanyServiceImpl implements CompanyService {

    private static Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    CompanyImportanceItemDAO companyImportanceItemDAO;

    @Autowired
    CompanyGroupDAO companyGroupDAO;

    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    @Autowired
    CommonManagerDAO commonManagerDAO;

    @Autowired
    ContactItemDAO contactItemDAO;

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

    @Autowired
    ProjectDAO projectDAO;

    @Override
    public Result<SearchResult<Company>> getCompanies(AuthToken token, CompanyQuery query) {

        applyFilterByScope(token, query);

        SearchResult<Company> sr = companyDAO.getSearchResultByQuery(query);
        jdbcManyRelationsHelper.fill(sr.getResults(), Company.Fields.CONTACT_ITEMS);
        jdbcManyRelationsHelper.fill(sr.getResults(), Company.Fields.COMMON_MANAGER_LIST);

        return ok(sr);
    }

    @Override
    public Result<List<EntityOption>> companyOptionList(AuthToken token, CompanyQuery query) {
        applyFilterByScope(token, query);
        List<Company> list = companyDAO.listByQuery(query);
        if (list == null)
            return error(En_ResultStatus.GET_DATA_ERROR);
        return ok(companyListToEntityOption(list, query));
    }

    @Override
    public Result<List<EntityOption>> companyOptionListIgnorePrivileges(AuthToken token, CompanyQuery query) {
        List<Company> list = companyDAO.listByQuery(query);

        if (list == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        return ok(companyListToEntityOption(list, query));
    }

    @Override
    public Result<List<EntityOption>> companyOptionListByIds(AuthToken token, List<Long> ids) {
        List<Company> list = companyDAO.getListByKeys(ids);

        if (list == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        return ok(list.stream()
                .map(Company::toEntityOption)
                .collect(Collectors.toList()));
    }

    @Override
    public Result<List<EntityOption>> subcontractorOptionListByCompanyIds(AuthToken token, Collection<Long> companyIds, boolean isActive) {

        if (CollectionUtils.isEmpty(companyIds)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        ProjectQuery query = new ProjectQuery();
        query.setInitiatorCompanyIds(setOf(companyIds));
        query.setDeleted(CaseObject.NOT_DELETED);
        query.setActive(isActive);
        Collection<Project> list = projectDAO.getProjects(query);
        if (list == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        jdbcManyRelationsHelper.fill(list, "subcontractors");

        List<Company> companies = list.stream()
                .map(Project::getSubcontractors)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        List<Company> singleHomeCompanies = companyDAO.getSingleHomeCompanies();
        if (singleHomeCompanies == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        companies.addAll(0, singleHomeCompanies);

        return ok(companies.stream()
                .map(Company::toEntityOption)
                .collect(Collectors.toList()));
    }

    @Override
    public Result<List<EntityOption>> companyOptionListBySubcontractorIds(AuthToken token, Collection<Long> subcontractorIds, boolean isActive) {

        if (CollectionUtils.isEmpty(subcontractorIds)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        ProjectQuery query = new ProjectQuery();
        query.setSubcontractorIds(setOf(subcontractorIds));
        query.setDeleted(CaseObject.NOT_DELETED);
        query.setActive(isActive);
        Collection<Project> list = projectDAO.getProjects(query);
        if (list == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        List<Company> companies = list.stream()
                .map(Project::getCustomer)
                .distinct()
                .collect(Collectors.toList());

        List<Company> singleHomeCompanies = companyDAO.getSingleHomeCompanies();
        if (singleHomeCompanies == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        companies.addAll(0, singleHomeCompanies);

        return ok(companies.stream()
                .map(Company::toEntityOption)
                .collect(Collectors.toList()));
    }

    @Override
    public Result<List<CompanySubscription>> getCompanySubscriptions( AuthToken token, Long companyId ) {
        if ( companyId == null ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<CompanySubscription> result = companySubscriptionDAO.listByCompanyId(companyId);
        return ok(result );
    }

    @Override
    public Result<List<CompanySubscription>> getCompanyWithParentCompanySubscriptions(AuthToken authToken, Set<Long> companyIds) {
        if (isEmpty(companyIds)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Set<Long> companyAndParentCompanyIds = new HashSet<>();
        companyAndParentCompanyIds.addAll(companyIds);
        companyAndParentCompanyIds.addAll(collectParentCompanyIds(companyDAO.getListByKeys(companyIds)));

        return ok(companySubscriptionDAO.listByCompanyIds(companyAndParentCompanyIds));
    }

    @Override
    @Transactional
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
            final boolean YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackCompanySyncEnabled();
            if (YOUTRACK_INTEGRATION_ENABLED) {
                youtrackService.getCompanyByName(company.getCname())
                        .flatMap(companyIdByName -> youtrackService.updateCompanyArchived(companyIdByName, isDeprecated)
                            .ifOk(youtrackCompanyId -> log.info("updateState(): updated company state in youtrack. YoutrackCompanyId = {}", youtrackCompanyId))
                            .ifError(errorResult -> log.warn("updateState(): Can't update company state in youtrack. {}", errorResult)))
                        .ifError(errorResult -> log.warn("getCompanyByName(): Can't get company in youtrack. {}", errorResult)
                        );
            }
            return ok();
        } else {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<List<EntityOption>> groupOptionList(AuthToken token) {
        List<CompanyGroup> list = companyGroupDAO.getListByQuery(new CompanyGroupQuery(null, En_SortField.group_name, En_SortDir.ASC));

        if (list == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        List<EntityOption> result = list.stream().map(CompanyGroup::toEntityOption).collect(Collectors.toList());

        return ok(result);
    }

    @Override
    public Result<List<CompanyGroup>> groupList(AuthToken token, CompanyGroupQuery query) {
        return Result.ok(
                companyGroupDAO.getListByQuery(query)
        );
    }

    @Override
    public Result<List<En_CompanyCategory>> categoryOptionList(AuthToken token, boolean hasOfficial) {

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
    public Result<Company> getCompanyOmitPrivileges(AuthToken token, Long id) {
        return getCompany(token, id);
    }

    @Override
    @Transactional
    public Result<Company> createCompany( AuthToken token, Company company ) {

        if (!isValidCompany(company)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        company.setCreated(new Date());
        Long companyId = companyDAO.persist(company);
        if (companyId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        contactItemDAO.saveOrUpdateBatch(company.getContactItems());
        jdbcManyRelationsHelper.persist(company, Company.Fields.CONTACT_ITEMS);
        jdbcManyRelationsHelper.persist(company, "commonManagerList");

        updateCompanySubscription(company.getId(), company.getSubscriptions());
        addCommonImportanceLevels(companyId);
        updateCommonManager(company.getId(), company.getCommonManagerList());

        final boolean YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackCompanySyncEnabled();

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
    @Transactional
    public Result<Company> updateCompany( AuthToken token, Company company ) {

        if (!isValidCompany(company)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        final boolean YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackCompanySyncEnabled();

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

        contactItemDAO.saveOrUpdateBatch(company.getContactItems());
        jdbcManyRelationsHelper.persist(company, Company.Fields.CONTACT_ITEMS);

        updateCompanySubscription(company.getId(), company.getSubscriptions());
        updateCommonManager(company.getId(), company.getCommonManagerList());

        if (YOUTRACK_INTEGRATION_ENABLED && StringUtils.isNotEmpty(company.getCname()) && !company.getCname().equals(oldName)) {
            youtrackService.getCompanyByName(oldName)
                    .flatMap(companyIdByName -> youtrackService.updateCompanyName(companyIdByName, company.getCname())
                            .ifOk(companyId -> log.info("updateCompany(): updated company in youtrack. YoutrackCompanyId = {}", companyId))
                            .ifError(errorResult -> log.warn("updateCompany(): Can't update company in youtrack. {}", errorResult)))
                    .ifError(errorResult -> log.warn("getCompanyByName(): Can't get company in youtrack. {}", errorResult)
                    );
        }

        return ok(company);
    }

    @Override
    public Result<Boolean> isCompanyNameExists(AuthToken token, String name, Long excludeId) {

        if (name == null || name.trim().isEmpty())
            return error(En_ResultStatus.INCORRECT_PARAMS);

        return ok(checkCompanyExists(name, excludeId));
    }

    @Override
    public Result<Boolean> isGroupNameExists(AuthToken token, String name, Long excludeId) {

        if (name == null || name.trim().isEmpty())
            return error(En_ResultStatus.INCORRECT_PARAMS);

        return ok(checkGroupExists(name, excludeId));
    }

    @Override
    public Result<List<EntityOption>> getAllHomeCompanies(AuthToken token) {
        List<Company> companies = companyDAO.getAllHomeCompanies();
        jdbcManyRelationsHelper.fill(companies, Company.Fields.CONTACT_ITEMS);
        return ok(companies.stream()
                .map(Company::toEntityOption)
                .collect(Collectors.toList()));
    }

    @Override
    public Result<List<CompanyImportanceItem>> getCompanyImportanceItems(AuthToken token, Long companyId) {
        List<CompanyImportanceItem> result = companyImportanceItemDAO.getSortedImportanceLevels(companyId);
        return ok(result);
    }

    @Override
    public Result<List<EntityOption>> getSingleHomeCompanies(AuthToken token) {
        List<Company> companies = companyDAO.getSingleHomeCompanies();
        jdbcManyRelationsHelper.fill(companies, Company.Fields.CONTACT_ITEMS);
        return ok(companies.stream()
                .map(Company::toEntityOption)
                .collect(Collectors.toList()));
    }

    private List<Long> collectParentCompanyIds(List<Company> companies) {
        return emptyIfNull(companies)
                .stream()
                .map(Company::getParentCompanyId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void addCommonImportanceLevels(Long companyId) {
        log.info( "adding common importance levels for companyId = {}", companyId );

        List<CompanyImportanceItem> importanceItems = new ArrayList<>();

        for (Integer nextDefaultImportanceId : CrmConstants.ImportanceLevel.commonImportanceLevelIds) {
            importanceItems.add(new CompanyImportanceItem(companyId, nextDefaultImportanceId, nextDefaultImportanceId));
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

    private boolean updateCommonManager(Long companyId, List<CommonManager> commonManagers ) {
        log.info( "binding update to linked company common managers for companyId = {}", companyId );

        List<Long> toRemoveNumberIds = commonManagerDAO.getIdsByCompany( companyId );
        if ( CollectionUtils.isEmpty(commonManagers) && CollectionUtils.isEmpty(toRemoveNumberIds) ) {
            return true;
        }

        List<CommonManager> newManagers = new ArrayList<>();
        List<CommonManager> oldManagers = new ArrayList<>();
        commonManagers.forEach( commonManager -> {
            if ( commonManager.getId() == null ) {
                commonManager.setCompanyId( companyId );
                newManagers.add( commonManager );
            } else {
                oldManagers.add( commonManager );
            }
        } );

        toRemoveNumberIds.removeAll( oldManagers.stream().map(CommonManager::getId).collect( Collectors.toList() ) );
        if ( !CollectionUtils.isEmpty( toRemoveNumberIds ) ) {
            log.info( "remove company common managers = {} for companyId = {}", toRemoveNumberIds, companyId );
            int countRemoved = commonManagerDAO.removeByKeys( toRemoveNumberIds );
            if ( countRemoved != toRemoveNumberIds.size() ) {
                return false;
            }
        }

        if ( !CollectionUtils.isEmpty( newManagers ) ) {
            log.info( "persist company common managers = {} for companyId = {}", newManagers, companyId );
            commonManagerDAO.persistBatch( newManagers );
        }

        if ( !CollectionUtils.isEmpty( oldManagers ) ) {
            log.info( "merge company common managers = {} for companyId = {}", oldManagers, companyId );
            int countMerged = commonManagerDAO.mergeBatch( oldManagers );
            if ( countMerged != oldManagers.size() ) {
                return false;
            }
        }

        return true;
    }


    private boolean isValidCompany(Company company) {
        return company != null
                && company.getCname() != null
                && !company.getCname().matches(CrmConstants.Masks.COMPANY_NAME_ILLEGAL_CHARS)
                && !company.getCname().trim().isEmpty()
                && (company.getParentCompanyId() == null || isEmpty(company.getChildCompanies()))
                && !checkCompanyExists(company.getCname(), company.getId())
                && isValidSubscriptionsLangCode(company);
    }

    private boolean isValidSubscriptionsLangCode(Company company) {
        return company.getSubscriptions() == null || company.getSubscriptions().stream()
                .allMatch(sub -> sub.getLangCode() == null || "ru".equals(sub.getLangCode()) || "en".equals(sub.getLangCode()));
    }

    private boolean checkCompanyExists (String name, Long excludeId) {

        Company company = companyDAO.getCompanyByName(name.trim());

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

    private void applyFilterByScope(AuthToken token, CompanyQuery query) {
        if (!policyService.hasSystemScope(token.getRoles())) {
            query.setCompanyIds(acceptAllowedCompanies(query.getCompanyIds(), token.getCompanyAndChildIds()));
        }
    }

    private List<Long> acceptAllowedCompanies( List<Long> companyIds, Collection<Long> allowedCompaniesIds ) {
        if( companyIds == null ) return new ArrayList<>( allowedCompaniesIds );
        ArrayList allowedCompanies = new ArrayList( companyIds );
        allowedCompanies.retainAll( allowedCompaniesIds );
        return allowedCompanies.isEmpty() ? new ArrayList<>( allowedCompaniesIds ) : allowedCompanies;
    }

    private List<EntityOption> companyListToEntityOption(List<Company> list, CompanyQuery query) {
        List<EntityOption> result = list.stream()
                .sorted(( o1, o2 ) -> placeHomeCompaniesAtBegin( query, o1, o2 ) )
                .map(Company::toEntityOption).collect(Collectors.toList());

        if(query.isReverseOrder() != null && query.isReverseOrder()){
            Collections.reverse(result);
        }

        return result;
    }
}
