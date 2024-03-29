package ru.protei.portal.core.model.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.winter.core.utils.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

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
    public List<Long> getAllHomeCompanyIdsWithoutSync() {
        String query = "SELECT companyId FROM company_group_home where synchronize_with_1c = false ";
        return jdbcTemplate.queryForList(query, Long.class);
    }

    @Override
    public List<Company> getSingleHomeCompanies() {
        String condition = "company.id IN (SELECT companyId FROM company_group_home where mainId is NULL)";
        return getListByCondition(condition);
    }

    @Override
    public List<Company> getAllHomeCompanies() {
        String condition = "company.id IN (SELECT companyId FROM company_group_home)";
        return getListByCondition(condition);
    }

    @Override
    public boolean isEmployeeInHomeCompanies(long companyId) {
        Query query = query()
                .where("company.id").equal(companyId)
                    .and("company.id").in(query().select("companyId").from("company_group_home")).asQuery();
        return checkExistsByCondition(query.buildSql(), query.args());
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CompanyQuery query) {
        log.info( "createSqlCondition(): query={}", query );
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getHomeGroupFlag() != null) {
                if (query.getHomeGroupFlag()) {
                    condition.append(" and company.id in ( select companyId from company_group_home where companyId != " + CrmConstants.Company.HOME_COMPANY_ID + " ) ");
                } else {
                    condition.append(" and company.id not in").append(" (select companyId from company_group_home where mainId is not null) ");
                }
            }

            if (query.getSynchronizeWith1C() != null){
                condition.append(" and company.id").append(" in (select companyId from company_group_home where synchronize_with_1c = ").append(query.getSynchronizeWith1C() ? "true" : "false").append(")");
            }

            if (query.getShowHidden() != null && !query.getShowHidden()) {
                condition.append(" and (company.is_hidden = false or company.is_hidden is NULL)");
            }

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
                condition.append(" and (cname like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));

                if (query.getAlternativeSearchString() != null) {
                    condition.append(" or cname like ?");
                    args.add(HelperFunc.makeLikeArg(query.getAlternativeSearchString(), true));
                }
                condition.append(" )");
            }

            if (query.getShowDeprecated() != null && !query.getShowDeprecated()) {
                condition.append(" and is_deprecated = false");
            }
        });
    }

    private static final Logger log = LoggerFactory.getLogger( CompanyDAO_Impl.class );

}
