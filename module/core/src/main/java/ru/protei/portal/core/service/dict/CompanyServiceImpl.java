package ru.protei.portal.core.service.dict;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dao.CompanyCategoryDAO;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.dao.CompanyGroupItemDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.CompanyGroupItem;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.core.utils.EntitySelector;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * Created by michael on 27.09.16.
 */
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    CompanyGroupDAO companyGroupDAO;

    @Autowired
    CompanyGroupItemDAO companyGroupItemDAO;

    @Autowired
    CompanyCategoryDAO companyCategoryDAO;
    /**
     * caches
     */

//    EntityCache<Company> companyCache;
    EntityCache<CompanyGroup> companyGroupCache;
    EntityCache<CompanyGroupItem> companyGroupItemCache;

    private Map<Long,List<CompanyGroup>> company2group;

    /**
     * it will be called after auto-injection
     */
    @PostConstruct
    protected void init() {
//        companyCache = new EntityCache<Company>(companyDAO, TimeUnit.MINUTES.toMillis(10));
        companyGroupCache = new EntityCache<CompanyGroup>(companyGroupDAO, TimeUnit.MINUTES.toMillis(10));

        // it maps company-id to its groups list (where that company is a member)
        company2group = new HashMap<>();

        companyGroupItemCache = new EntityCache<CompanyGroupItem>(companyGroupItemDAO, TimeUnit.MINUTES.toMillis(10), new EntityCache.OnRebuild<CompanyGroupItem>() {
            @Override
            public void onCacheRebuild(Map<Long, CompanyGroupItem> map) {
                company2group.clear();
                for (CompanyGroupItem link : map.values()) {
                    List<CompanyGroup> groups = company2group.get(link.getCompanyId());
                    if (groups == null) {
                        groups = new ArrayList<>(1);
                        company2group.put(link.getCompanyId(), groups);
                    }
                    groups.add(companyGroupCache.get(link.getGroupId()));
                }
            }
        });
    }

    /**
     * destructor implementation
     */
    @PreDestroy
    protected void destroy () {
//        companyCache.destroy();
        companyGroupCache.destroy();
        companyGroupItemCache.destroy();
        company2group.clear();
    }


    @Override
    public CoreResponse<CompanyGroup> createGroup(String name, String info) {

        CompanyGroup group = new CompanyGroup();
        group.setCreated(new Date());
        group.setInfo(info);
        group.setName(name);

        if (companyGroupDAO.persist(group) != null) {
            companyGroupCache.putIfNotExists(group);
            return new CoreResponse<CompanyGroup>().success(group);
        }

        return createUndefinedError();
    }

    private <T> CoreResponse<T> createUndefinedError() {
        return new CoreResponse<T>().error("undefined error", "internal_error");
    }

    @Override
    public CoreResponse<CompanyGroupItem> addCompanyToGroup(Long groupId, Long companyId) {

        CompanyGroupItem link = new CompanyGroupItem();
        link.setCompanyId(companyId);
        link.setGroupId(groupId);
        if (companyGroupItemDAO.persist(link) != null) {
            companyGroupItemCache.putIfNotExists(link);
            return new CoreResponse<CompanyGroupItem>().success(link);
        }

        return createUndefinedError();
    }

    @Override
    public CoreResponse<CompanyGroupItem> delCompanyFromGroup(Long groupId, Long companyId) {
        CompanyGroupItem link = companyGroupItemDAO.getByCondition("company_id=? and group_id=?", companyId, groupId);
        if (link != null) {
            companyGroupItemDAO.remove(link);
            companyGroupItemCache.remove(link);
            return new CoreResponse<CompanyGroupItem>().success(link);
        }

        return createUndefinedError();
    }

    /**
     * complete nested fields, list, etc
     *
     * @param company
     * @return
     */
    protected Company complete(Company company) {
        company.setGroups(company2group.get(company.getId()));
        return company;
    }

    protected List<Company> completeList (List<Company> companies) {
        for (Company company : companies)
            complete(company);
        return companies;
    }

    @Override
    public Company getProfile(Long id) {

        return complete(companyDAO.get(id));

    }

    @Override
    public HttpListResult<Company> list(CompanyQuery query) {

        return new HttpListResult<Company> (
               companyDAO.getListByQuery (query), false
        );
    }

    @Override
    public HttpListResult<CompanyGroup> groupList(BaseQuery query) {

        return new HttpListResult<>(
                companyGroupCache.collect(
                        new CompanyGroupSearchSelector(query.getSearchString()),
                        new ArrayList<>()
                ), false
        );
    }

    @Override
    public HttpListResult<CompanyCategory> categoryList() {

        return new HttpListResult<>(companyCategoryDAO.getAll(), false);
    }

    @Override
    public CoreResponse<Company> createCompany(Company company, CompanyGroup group) {

        if (!isValidCompany(company) || companyDAO.persist(company) == null)
            return createUndefinedError();

        if (group != null) {

            if (!isValidGroup(group))
                return createUndefinedError();

            if (group.getId() == null && companyGroupDAO.persist(group) == null)
                return createUndefinedError();

            if (linkCompanyToGroup(company.getId(), group.getId()).isError()) return createUndefinedError();
        }

        return new CoreResponse<Company>().success(company);
    }

    @Override
    public CoreResponse<Company> updateCompany(Company company, CompanyGroup group) {

        if (!isValidCompany(company) || !companyDAO.merge(company))
            return createUndefinedError();

        companyGroupItemDAO.getCompanyToGroupLinks(company.getId(), null).forEach(link -> {
            companyGroupItemDAO.remove(link);
            companyGroupItemCache.remove(link);
        });

        if (group != null) {

            if (!isValidGroup(group))
                return createUndefinedError();

            if (group.getId() == null && companyGroupDAO.persist(group) == null)
                return createUndefinedError();

            if (linkCompanyToGroup(company.getId(), group.getId()).isError()) return createUndefinedError();
        }

        return new CoreResponse<Company>().success(company);
    }

    @Override
    public CoreResponse<Boolean> isCompanyNameExists(String name, Long id) {
        return new CoreResponse<Boolean>().success(companyDAO.checkExistsCompanyByName(name, id));
    }

    @Override
    public CoreResponse<Boolean> isGroupNameExists(String name, Long id) {
        return new CoreResponse<Boolean>().success(companyGroupDAO.checkExistsGroupByName(name, id));
    }

    /**
     * company-group search
     */
    private static class CompanyGroupSearchSelector implements EntitySelector<CompanyGroup> {

        private String expr;

        public CompanyGroupSearchSelector(String expr) {
            this.expr = expr == null || expr.trim().isEmpty() ? null : expr.toLowerCase().replace("%","");
        }

        @Override
        public boolean matches(CompanyGroup group) {
            return expr == null || group.getName().toLowerCase().contains(expr)
                        || group.getInfo().toLowerCase().contains(expr);
        }
    }

    private boolean isValidCompany(Company company) {
        return company != null && !company.getCname().trim().isEmpty()
                && !company.getAddressDejure().trim().isEmpty() && !company.getAddressFact().trim().isEmpty() &&
                !companyDAO.checkExistsCompanyByName(company.getCname(), company.getId());
    }

    private boolean isValidGroup(CompanyGroup group) {
        return !group.getName().trim().isEmpty() &&
                !companyGroupDAO.checkExistsGroupByName(group.getName(), group.getId());
    }

    private CoreResponse<CompanyGroupItem> linkCompanyToGroup(Long companyId, Long groupId) {

        CompanyGroupItem link = new CompanyGroupItem();
        link.setCompanyId(companyId);
        link.setGroupId(groupId);

        if (companyGroupItemDAO.persist(link) == null)
            return createUndefinedError();

        companyGroupItemCache.putIfNotExists(link);

        return new CoreResponse<CompanyGroupItem>().success(link);
    };
}