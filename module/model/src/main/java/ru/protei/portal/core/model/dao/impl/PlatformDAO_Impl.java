package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.PlatformDAO;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.winter.jdbc.JdbcHelper;

import java.util.stream.Collectors;

public class PlatformDAO_Impl extends PortalBaseJdbcDAO<Platform> implements PlatformDAO {

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(PlatformQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getPlatformId() != null) {
                condition.append(" and platform.id = ?");
                args.add(query.getPlatformId());
            }

            if (query.getCompanyIds() != null && !query.getCompanyIds().isEmpty()) {
                condition.append(" and (platform.company_id in ")
                         .append(JdbcHelper.makeSqlStringCollection(query.getCompanyIds(), args, null))
                        .append(" or platform.project_id in (select case_object.ID from case_object left outer join platform on platform.project_id = case_object.ID where case_object.initiator_company in ")
                        .append(JdbcHelper.makeSqlStringCollection(query.getCompanyIds(), args, null))
                        .append("))");
            }

            if (query.getManagerIds() != null && !query.getManagerIds().isEmpty()) {
                if (query.getManagerIds().remove(CrmConstants.Employee.UNDEFINED)) {
                    condition.append(" and (platform.manager_id is null")
                            .append(" and (platform.project_id is null or platform.project_id in (select case_object.ID from case_object left outer join platform on platform.project_id = case_object.ID where case_object.MANAGER is null))");
                    if (!query.getManagerIds().isEmpty()) {
                        condition.append(" or platform.manager_id in ")
                                .append(JdbcHelper.makeSqlStringCollection(query.getManagerIds(), args, null))
                                .append(" or platform.project_id in (select case_object.ID from case_object left outer join platform on platform.project_id = case_object.ID where case_object.MANAGER in ")
                                .append(JdbcHelper.makeSqlStringCollection(query.getManagerIds(), args, null))
                                .append(")");
                    }
                    condition.append(")");
                } else {
                    condition.append(" and (platform.manager_id in ")
                            .append(JdbcHelper.makeSqlStringCollection(query.getManagerIds(), args, null))
                            .append(" or platform.project_id in (select case_object.ID from case_object left outer join platform on platform.project_id = case_object.ID where case_object.MANAGER in ")
                            .append(JdbcHelper.makeSqlStringCollection(query.getManagerIds(), args, null))
                            .append("))");
                }
            }

            if (query.getSearchString() != null && !query.getSearchString().isEmpty()) {
                condition.append(" and platform.name like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }

            if (query.getParams() != null && !query.getParams().isEmpty()) {
                condition.append(" and platform.parameters like ?");
                args.add(HelperFunc.makeLikeArg(query.getParams(), true));
            }

            if (query.getComment() != null && !query.getComment().isEmpty()) {
                condition.append(" and platform.comment like ?");
                args.add(HelperFunc.makeLikeArg(query.getComment(), true));
            }
        });
    }

    @Override
    public Platform getByProjectId(Long id) {
        return (id == null ? null : getByCondition("project_id=?", id));
    }
}
