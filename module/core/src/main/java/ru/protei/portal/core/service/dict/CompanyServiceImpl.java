package ru.protei.portal.core.service.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.dao.CompanyGroupItemDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.CompanyGroupItem;
import ru.protei.portal.core.model.view.CompanyView;
import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.core.utils.EntitySelector;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.winter.core.utils.collections.Converter;
import ru.protei.winter.jdbc.JdbcSort;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
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

    EntityCache<Company> companyCache;
    EntityCache<CompanyGroup> companyGroupCache;
    EntityCache<CompanyGroupItem> companyGroupItemCache;

    /**
     * it will be called after auto-injection
     */
    @PostConstruct
    protected void init() {
        companyCache = new EntityCache<Company>(companyDAO, TimeUnit.MINUTES.toMillis(10));
        companyGroupCache = new EntityCache<CompanyGroup>(companyGroupDAO, TimeUnit.MINUTES.toMillis(10));
        companyGroupItemCache = new EntityCache<CompanyGroupItem>(companyGroupItemDAO, TimeUnit.MINUTES.toMillis(10));
    }

    /**
     * destructor implementation
     */
    @PreDestroy
    protected void destroy () {
        companyCache.destroy();
        companyGroupCache.destroy();
        companyGroupItemCache.destroy();
    }



    @Override
    public HttpListResult<Company> list(@RequestParam(name = "q", defaultValue = "") String param,
                                        @RequestParam(name = "sortBy", defaultValue = "") String sortField,
                                        @RequestParam(name = "sortDir", defaultValue = "") String sortDir) {

        param = HelperFunc.makeLikeArg(param, true);

        JdbcSort sort = new JdbcSort(En_SortDir.toWinter(sortDir), En_SortField.parse(sortField, En_SortField.comp_name).getFieldName());

        return new HttpListResult<>(companyDAO.getListByCondition("cname like ?", sort, param), false);
    }


    @Override
    public HttpListResult<CompanyView> listView(@RequestParam(name = "q", defaultValue = "") String param,
                                                @RequestParam(name = "sortBy", defaultValue = "") String sortField,
                                                @RequestParam(name = "sortDir", defaultValue = "") String sortDir) {

        return new HttpListResult<>(
                companyCache.collectAndConvert(
                            new CompanySearchSelector(param),
                            new ArrayList<>(),
                            new CompanyToViewConverter()
                ), false
        );
    }

    @Override
    public HttpListResult<CompanyGroup> groupList(@RequestParam(name = "q", defaultValue = "") String param,
                                                  @RequestParam(name = "sortBy", defaultValue = "") String sortField,
                                                  @RequestParam(name = "sortDir", defaultValue = "") String sortDir) {

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

    private static class CompanyToViewConverter implements Converter<Company, CompanyView> {
        @Override
        public CompanyView convert(Company company) {
            return new CompanyView(company, null);
        }
    }

    /**
     *
     * predicates implementation
     *
     */
    private static class CompanySearchSelector implements EntitySelector<Company> {

        private String expr;

        public CompanySearchSelector(String expr) {
            this.expr = expr == null || expr.trim().isEmpty() ? null : expr.toLowerCase();
        }

        @Override
        public boolean matches(Company company) {
            return expr == null || company.getCname().toLowerCase().contains(expr);
        }
    }


    private static class CompanyGroupSearchSelector implements EntitySelector<CompanyGroup> {

        private String expr;

        public CompanyGroupSearchSelector(String expr) {
            this.expr = expr == null || expr.trim().isEmpty() ? null : expr.toLowerCase();
        }

        @Override
        public boolean matches(CompanyGroup group) {
            return expr == null || group.getName().toLowerCase().contains(expr)
                        || group.getInfo().toLowerCase().contains(expr);
        }
    }
}
