package ru.protei.portal.core.service.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.dao.CompanyGroupItemDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.CompanyGroupItem;
import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.core.utils.EntitySelector;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.winter.jdbc.JdbcSort;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.TimeUnit;


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
    public CoreResponse<CompanyGroup> createGroup(@RequestParam(name = "name") String name, @RequestParam(name = "info") String info) {

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
    public CoreResponse<CompanyGroupItem> addCompanyToGroup(@RequestParam(name = "group") Long groupId, @RequestParam(name = "company") Long companyId) {

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
    public CoreResponse<CompanyGroupItem> delCompanyFromGroup(@RequestParam(name = "group") Long groupId, @RequestParam(name = "company") Long companyId) {
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
    public Company getProfile(@PathVariable("id") Long id) {
        return complete(companyDAO.get(id));
    }

    /*@Override
    public HttpListResult<Company> list(@RequestParam(name = "q", required = false) String param,
                                        @RequestParam(name = "sortBy", required = false) En_SortField sortField,
                                        @RequestParam(name = "sortDir", required = false) String sortDir) {

        param = HelperFunc.makeLikeArg(param, true);

        if (sortField == null)
            sortField = En_SortField.comp_name;

        JdbcSort sort = new JdbcSort(En_SortDir.toWinter(sortDir), sortField.getFieldName());

        return new HttpListResult<>(companyDAO.getListByCondition("cname like ?", sort, param), false);
    }*/


    @Override
    public HttpListResult<Company> list(@RequestParam(name = "q", required = false) String param,
                                        @RequestParam(name = "group", required = false) Long groupId,
                                        @RequestParam(name = "sortBy", required = false) En_SortField sortField,
                                        @RequestParam(name = "sortDir", required = false) String sortDir) {
        if (sortField == null)
            sortField = En_SortField.comp_name;

        JdbcSort sort = new JdbcSort(En_SortDir.toWinter(sortDir), sortField.getFieldName());

        param = HelperFunc.makeLikeArg(param, true);

        return new HttpListResult<Company> (
               completeList(companyDAO.getList (param, groupId, sort)), false
        );

//        return new HttpListResult<>(
//                companyCache.collect(
//                            new CompanySearchSelector(param),
//                            new ArrayList<>()
//                ), false
//        );
    }

    @Override
    public HttpListResult<CompanyGroup> groupList(@RequestParam(name = "q", required = false) String param,
                                                  @RequestParam(name = "sortBy", required = false) En_SortField sortField,
                                                  @RequestParam(name = "sortDir", required = false) String sortDir) {

        if (sortField == null)
            sortField = En_SortField.comp_name;

        return new HttpListResult<>(
                companyGroupCache.collect(
                        new CompanyGroupSearchSelector(param),
                        new ArrayList<>()
                ), false
        );
    }


    /**
     * converters implementation
     */

//    private static class CompanyToViewConverter implements Converter<Company, CompanyView> {
//        @Override
//        public CompanyView convert(Company company) {
//            return new CompanyView(company, null);
//        }
//    }

//    /**
//     *
//     * predicates implementation
//     *
//     */
//    private static class CompanySearchSelector implements EntitySelector<Company> {
//
//        //private String expr;
//        private Pattern pattern;
//
//        public CompanySearchSelector(String expr) {
//            //this.expr = expr == null || expr.trim().isEmpty() ? null : expr.toLowerCase();
//            if (expr == null || expr.trim().isEmpty()) {
//                pattern = null;
//            }
//            else {
//                pattern = Pattern.compile(expr.toLowerCase().replace("%",".*").replace("_","."));
//            }
//        }
//
//        @Override
//        public boolean matches(Company company) {
//            return pattern == null || pattern.matcher(company.getCname().toLowerCase()).matches();
//        }
//    }


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
}
