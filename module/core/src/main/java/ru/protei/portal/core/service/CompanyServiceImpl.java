package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CompanyCategoryDAO;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.dao.CompanyGroupItemDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.CompanyGroupItem;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.core.utils.EntitySelector;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * Created by michael on 27.09.16.
 */
public class CompanyServiceImpl implements CompanyService {


    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    CompanyGroupDAO companyGroupDAO;

//    @Autowired
//    CompanyGroupItemDAO companyGroupItemDAO;

    @Autowired
    CompanyCategoryDAO companyCategoryDAO;
    /**
     * caches
     */

//    EntityCache<Company> companyCache;
//    EntityCache<CompanyGroup> companyGroupCache;
//    EntityCache<CompanyGroupItem> companyGroupItemCache;
//
//    private Map<Long,List<CompanyGroup>> company2group;

    /**
     * it will be called after auto-injection
     */
    @PostConstruct
    protected void init() {
//        companyCache = new EntityCache<Company>(companyDAO, TimeUnit.MINUTES.toMillis(10));
//        companyGroupCache = new EntityCache<CompanyGroup>(companyGroupDAO, TimeUnit.MINUTES.toMillis(10));

        // it maps company-id to its groups list (where that company is a member)
//        company2group = new HashMap<>();

//        companyGroupItemCache = new EntityCache<CompanyGroupItem>(companyGroupItemDAO, TimeUnit.MINUTES.toMillis(10), new EntityCache.OnRebuild<CompanyGroupItem>() {
//            @Override
//            public void onCacheRebuild(Map<Long, CompanyGroupItem> map) {
//                company2group.clear();
//                for (CompanyGroupItem link : map.values()) {
//                    List<CompanyGroup> groups = company2group.get(link.getCompanyId());
//                    if (groups == null) {
//                        groups = new ArrayList<>(1);
//                        company2group.put(link.getCompanyId(), groups);
//                    }
//                    groups.add(companyGroupCache.get(link.getGroupId()));
//                }
//            }
//        });
    }

    /**
     * destructor implementation
     */
    @PreDestroy
    protected void destroy () {
//        companyCache.destroy();
//        companyGroupCache.destroy();
//        companyGroupItemCache.destroy();
//        company2group.clear();
    }


    @Override
    public CoreResponse<List<EntityOption>> companyOptionList() {
        List<EntityOption> result = companyDAO.getListByQuery(new CompanyQuery("", En_SortField.comp_name, En_SortDir.ASC))
                .stream().map(Company::toEntityOption).collect(Collectors.toList());

        return new CoreResponse<List<EntityOption>>().success(result,result.size());
    }

    @Override
    public CoreResponse<CompanyGroup> createGroup(String name, String info) {

        CompanyGroup group = new CompanyGroup();
        group.setCreated(new Date());
        group.setInfo(info);
        group.setName(name);

        if (companyGroupDAO.persist(group) != null) {
//            companyGroupCache.putIfNotExists(group);
            return new CoreResponse<CompanyGroup>().success(group);
        }

        return createUndefinedError();
    }

    private <T> CoreResponse<T> createUndefinedError() {
        return new CoreResponse<T>().error(En_ResultStatus.INTERNAL_ERROR);
    }

//    @Override
//    public CoreResponse<CompanyGroupItem> addCompanyToGroup(Long groupId, Long companyId) {
//
//        CompanyGroupItem link = new CompanyGroupItem();
//        link.setCompanyId(companyId);
//        link.setGroupId(groupId);
//        if (companyGroupItemDAO.persist(link) != null) {
//            companyGroupItemCache.putIfNotExists(link);
//            return new CoreResponse<CompanyGroupItem>().success(link);
//        }
//
//        return createUndefinedError();
//    }
//
//    @Override
//    public CoreResponse<CompanyGroupItem> delCompanyFromGroup(Long groupId, Long companyId) {
//        CompanyGroupItem link = companyGroupItemDAO.getByCondition("company_id=? and group_id=?", companyId, groupId);
//        if (link != null) {
//            companyGroupItemDAO.remove(link);
//            companyGroupItemCache.remove(link);
//            return new CoreResponse<CompanyGroupItem>().success(link);
//        }
//
//        return createUndefinedError();
//    }



    @Override
    public CoreResponse<List<Company>> companyList(CompanyQuery query) {
        return new CoreResponse<List<Company>> ()
                .success(companyDAO.getListByQuery (query));
    }

    @Override
    public CoreResponse<List<CompanyGroup>> groupList(BaseQuery query) {
        return new CoreResponse<List<CompanyGroup>>().success(
                companyGroupDAO.getListByQuery(query)
        );
    }

    @Override
    public CoreResponse<List<CompanyCategory>> categoryList() {
        return new CoreResponse<List<CompanyCategory>>().success(companyCategoryDAO.getAll());
    }

    @Override
    public CoreResponse<Company> getCompanyById(Long id) {

        if (id == null) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Company company = companyDAO.get(id);

        if (company == null) {
            return new CoreResponse().error(En_ResultStatus.NOT_FOUND);
        }

        return new CoreResponse<Company>().success(company);
    }

    @Override
    public CoreResponse<Company> createCompany(Company company, CompanyGroup group) {

        try {
            return new CoreResponse<Company>().success(createCompanyImpl(company, group));
        }
        catch (Exception e) {
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);
        }
    }

    @Override
    public CoreResponse<Company> updateCompany(Company company, CompanyGroup group) {

        try {
            return new CoreResponse<Company>().success(updateCompanyImpl(company, group));
        } catch (Exception e) {
            return new CoreResponse().error(En_ResultStatus.NOT_UPDATED);
        }
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

//    /**
//     * company-group search
//     */
//    private static class CompanyGroupSearchSelector implements EntitySelector<CompanyGroup> {
//
//        private String expr;
//
//        public CompanyGroupSearchSelector(String expr) {
//            this.expr = expr == null || expr.trim().isEmpty() ? null : expr.toLowerCase().replace("%","");
//        }
//
//        @Override
//        public boolean matches(CompanyGroup group) {
//            return expr == null || group.getName().toLowerCase().contains(expr)
//                        || group.getInfo().toLowerCase().contains(expr);
//        }
//    }

    @Transactional
    private Company createCompanyImpl(Company company, CompanyGroup group) throws Exception {

        if (!isValidCompany(company) || companyDAO.persist(company) == null) {
            throw new Exception();
        }

//        linkGroup(company, group);

        return company;
    }

    @Transactional
    private Company updateCompanyImpl(Company company, CompanyGroup group) throws Exception {

        company.setCompanyGroup(group);

        if (!isValidCompany(company) || !companyDAO.merge(company))
            throw new Exception();

//        companyGroupItemDAO.getCompanyToGroupLinks(company.getId(), null).forEach(link -> {
//            companyGroupItemDAO.remove(link);
//            companyGroupItemCache.remove(link);
//        });
//        linkGroup(company, group);

        return company;
    }

    private boolean isValidCompany(Company company) {
        return company != null &&
                company.getCname() != null && !company.getCname().trim().isEmpty() &&
                company.getAddressDejure() != null && !company.getAddressDejure().trim().isEmpty() &&
                company.getAddressFact() != null && !company.getAddressFact().trim().isEmpty() &&
                !checkCompanyExists(company.getCname(), company.getId());
    }

    private boolean isValidGroup(CompanyGroup group) {
        return group != null &&
                group.getName() != null && !group.getName().trim().isEmpty() &&
                !checkGroupExists(group.getName(), group.getId());
    }

//    private void linkGroup(Company company, CompanyGroup group) throws Exception {
//
//        if (group == null) {
//            return;
//        }
//
//        if (!isValidGroup(group)) {
//            throw new Exception();
//        }
//
//        if (group.getId() == null && companyGroupDAO.persist(group) == null) {
//            throw new Exception();
//        }
//
//        companyGroupCache.putIfNotExists(group);
//
//        linkCompanyToGroup(company.getId(), group.getId());
//    }

//    private void linkCompanyToGroup(Long companyId, Long groupId) throws Exception {
//
//        CompanyGroupItem link = new CompanyGroupItem();
//        link.setCompanyId(companyId);
//        link.setGroupId(groupId);
//
//        if (companyGroupItemDAO.persist(link) == null)
//            throw new Exception();
//
//        companyGroupItemCache.putIfNotExists(link);
//    };

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