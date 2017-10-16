package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CompanyCategoryDAO;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления компаниями
 */
public class CompanyServiceImpl implements CompanyService {

    private static Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    CompanyGroupDAO companyGroupDAO;

    @Autowired
    CompanyCategoryDAO companyCategoryDAO;

    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PolicyService policyService;

    @Override
    public CoreResponse<Long> countCompanies(CompanyQuery query) {
        return new CoreResponse<Long>().success(companyDAO.count(query));
    }

    @Override
    public CoreResponse<Long> countGroups(CompanyGroupQuery query) {
        return new CoreResponse<Long>().success(companyGroupDAO.count(query));
    }


    @Override
    public CoreResponse<List<EntityOption>> companyOptionList(List<En_CompanyCategory> categories) {
        CompanyQuery query = new CompanyQuery("", En_SortField.comp_name, En_SortDir.ASC);
        List<Long> ids = categories.stream().map(En_CompanyCategory::getId).collect(Collectors.toList());
        query.setCategoryIds(ids);
        List<Company> list = companyDAO.getListByQuery(query);

        if (list == null)
            new CoreResponse<List<EntityOption>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<EntityOption> result = list.stream().map(Company::toEntityOption).collect(Collectors.toList());

        return new CoreResponse<List<EntityOption>>().success(result,result.size());
    }

    @Override
    public CoreResponse<CompanyGroup> createGroup(String name, String info) {

        CompanyGroup group = new CompanyGroup();
        group.setCreated(new Date());
        group.setInfo(info);
        group.setName(name);

        if (companyGroupDAO.persist(group) != null) {
            return new CoreResponse<CompanyGroup>().success(group);
        }

        return createUndefinedError();
    }

    @Override
    public CoreResponse< Boolean > updateCompanySubscriptions( Long companyId, List< CompanySubscription > subscriptions ) {
        if ( companyId == null ) {
            return new CoreResponse<Boolean>().error( En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean result = updateCompanySubscription(companyId, subscriptions);
        return new CoreResponse<Boolean>().success( result );
    }

    @Override
    public CoreResponse<List<CompanySubscription>> getCompanySubscriptions( Long companyId ) {
        if ( companyId == null ) {
            return new CoreResponse<List<CompanySubscription>>().error( En_ResultStatus.INCORRECT_PARAMS);
        }

        List<CompanySubscription> result = companySubscriptionDAO.listByCompanyId(companyId);
        return new CoreResponse<List<CompanySubscription>>().success( result );
    }

    private <T> CoreResponse<T> createUndefinedError() {
        return new CoreResponse<T>().error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    public CoreResponse<List<Company>> companyList( AuthToken token, CompanyQuery query ) {

        List<Company> list = companyDAO.getListByQuery(query);

        if (list == null)
            new CoreResponse<List<Company>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<Company>>().success(list);
    }

    @Override
    public CoreResponse<List<EntityOption>> groupOptionList() {
        List<CompanyGroup> list = companyGroupDAO.getListByQuery(new CompanyGroupQuery(null, En_SortField.group_name, En_SortDir.ASC));

        if (list == null)
            new CoreResponse<List<EntityOption>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<EntityOption> result = list.stream().map(CompanyGroup::toEntityOption).collect(Collectors.toList());

        return new CoreResponse<List<EntityOption>>().success(result,result.size());
    }

    @Override
    public CoreResponse<List<CompanyGroup>> groupList(CompanyGroupQuery query) {
        return new CoreResponse<List<CompanyGroup>>().success(
                companyGroupDAO.getListByQuery(query)
        );
    }

    @Override
    public CoreResponse<List<EntityOption>> categoryOptionList(boolean hasOfficial) {

        List<CompanyCategory> list;

        if(!hasOfficial) {
            list = companyCategoryDAO.getListByKeys(Arrays.asList(1l, 2l, 3l));
        } else {
            list = companyCategoryDAO.getAll();
        }

        if (list == null)
            new CoreResponse<List<EntityOption>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<EntityOption> result = list.stream().map(CompanyCategory::toEntityOption).collect(Collectors.toList());

        return new CoreResponse<List<EntityOption>>().success(result,result.size());
    }

    @Override
    public CoreResponse<Company> getCompany( AuthToken token, Long id ) {

        if (id == null) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Company company = companyDAO.get(id);
        jdbcManyRelationsHelper.fillAll( company );

        if (company == null) {
            return new CoreResponse().error(En_ResultStatus.NOT_FOUND);
        }

        return new CoreResponse<Company>().success(company);
    }

    @Override
    public CoreResponse<Company> createCompany( AuthToken token, Company company ) {

        if (!isValidCompany(company)) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        company.setCreated(new Date());
        Long companyId = companyDAO.persist(company);

        if (companyId == null) {
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);
        }

        updateCompanySubscription(company.getId(), company.getSubscriptions());
        return new CoreResponse<Company>().success(company);
    }

    @Override
    public CoreResponse<Company> updateCompany( AuthToken token, Company company ) {

        if (!isValidCompany(company)) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Boolean result = companyDAO.merge(company);

        if ( !result )
            new CoreResponse().error(En_ResultStatus.NOT_UPDATED);

        updateCompanySubscription(company.getId(), company.getSubscriptions());
        return new CoreResponse<Company>().success(company);
    }

    @Override
    public CoreResponse<Boolean> isCompanyNameExists(String name, Long excludeId) {

        if (name == null || name.trim().isEmpty())
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        return new CoreResponse<Boolean>().success(checkCompanyExists(name, excludeId));
    }

    @Override
    public CoreResponse<Boolean> isGroupNameExists(String name, Long excludeId) {

        if (name == null || name.trim().isEmpty())
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        return new CoreResponse<Boolean>().success(checkGroupExists(name, excludeId));
    }


    private boolean updateCompanySubscription( Long companyId, List<CompanySubscription> companySubscriptions ) {
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

        toRemoveNumberIds.removeAll( oldSubscriptions.stream().map(CompanySubscription::getId).collect( Collectors.toList() ) );
        if ( !CollectionUtils.isEmpty( toRemoveNumberIds ) ) {
            log.info( "remove company subscriptions = {} for companyId = {}", toRemoveNumberIds, companyId );
            int countRemoved = companySubscriptionDAO.removeByKeys( toRemoveNumberIds );
            if ( countRemoved != toRemoveNumberIds.size() ) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidCompany(Company company) {
        return company != null
                && company.getCname() != null
                && !company.getCname().trim().isEmpty()
                && isValidContactInfo(company)
                && !checkCompanyExists(company.getCname(), company.getId());
    }

    private boolean isValidContactInfo (Company company) {
        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(company.getContactInfo());

        return HelperFunc.isNotEmpty(infoFacade.getLegalAddress()) &&
                HelperFunc.isNotEmpty(infoFacade.getFactAddress());
    }

    private boolean isValidGroup(CompanyGroup group) {
        return group != null &&
                group.getName() != null && !group.getName().trim().isEmpty() &&
                !checkGroupExists(group.getName(), group.getId());
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
}