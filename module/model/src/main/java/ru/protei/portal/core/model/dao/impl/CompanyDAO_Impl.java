package ru.protei.portal.core.model.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by michael on 01.04.16.
 */
public class CompanyDAO_Impl extends PortalBaseJdbcDAO<Company> implements CompanyDAO {

    @Override
    public Company getCompanyByName(String name) {
        return getByCondition(" cname=? ", name);
    }

    @Override
    public Map<Long, Long> mapLegacyId() {

        Map<Long, Long> result = new HashMap<>();

        partialGetAll("id", "old_id").forEach(company -> {
            if (company.getOldId() != null)
                result.put(company.getOldId(), company.getId());
        });

        return result;
    }

    @Override
    public boolean updateState(Company tempCompany) {
        return partialMerge(tempCompany, "is_deprecated");
    }

    @Override
    public List<Long> getAllHomeCompanyIds() {
        String query = "SELECT companyId FROM company_group_home";
        return jdbcTemplate.queryForList(query, Long.class);
    }

    @Override
    public List<Long> getAllHomeCompanyIdsWithoutSync() {
        String query = "SELECT companyId FROM company_group_home where synchronize_with_1c = false ";
        return jdbcTemplate.queryForList(query, Long.class);
    }

    @Override
    public List<Company> getAllHomeCompanies() {
        String sql = "company.id IN (SELECT companyId FROM company_group_home)";
        return getListByCondition(sql);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CompanyQuery query) {
        log.info( "createSqlCondition(): query={}", query );
        return new SqlCondition().build((condition, args) -> {

            condition.append("company.id").append(query.getOnlyHome() ? " in" : " not in").append(" ( select companyId from company_group_home where mainId is not null ");

            if (query.getShowHidden() != null && !query.getShowHidden()) {
                condition.append(" and (company.is_hidden = false or company.is_hidden is NULL)");
            }

            if (query.getSynchronizeWith1C() != null){
                condition.append("and synchronize_with_1c = ").append(query.getSynchronizeWith1C() ? "true" : "false");
            }

            condition.append(")");

            if (query.getCompanyIds() != null) {
                condition.append( " and company.id in " ).append( HelperFunc.makeInArg( query.getCompanyIds()) );
            }

            if (!CollectionUtils.isEmpty(query.getCategoryIds())) {
                condition.append(" and category_id in (")
                        .append(query.getCategoryIds().stream().map(Object::toString).collect(Collectors.joining(",")))
                        .append(")");
            }

            if(query.isOnlyParentCompanies()){
                condition.append( " and parent_company_id IS NULL" );
            }

            if(query.isSortHomeCompaniesAtBegin()){
                condition.append( " and (is_hidden is null or is_hidden = false)" );
            }

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                condition.append(" and cname like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }

            if (query.getShowDeprecated() != null && !query.getShowDeprecated()) {
                condition.append(" and is_deprecated = false");
            }
        });
    }

    private static final Logger log = LoggerFactory.getLogger( CompanyDAO_Impl.class );

}
