package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.EmployeeRegistrationDAO;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcBaseDAO;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class EmployeeRegistrationDAO_Impl extends JdbcBaseDAO<Long, EmployeeRegistration> implements EmployeeRegistrationDAO {

    @Override
    public List<EmployeeRegistration> getListByQuery(EmployeeRegistrationQuery query) {
        SqlCondition where = createSqlCondition(query);
        JdbcQueryParameters queryParameters = new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query, "CO"))
                .withOffset(query.getOffset());
        if (query.limit > 0) {
            queryParameters = queryParameters.withLimit(query.getLimit());
        }
        return getList(queryParameters);
    }

    @Override
    public int countByQuery(EmployeeRegistrationQuery query) {
        SqlCondition where = createSqlCondition(query);
        return getObjectsCount(where.condition, where.args);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(EmployeeRegistrationQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");

            if (StringUtils.isNotEmpty(query.getSearchString())) {
                condition.append(" and (CO.CASE_NAME like ? or employee registration.position like ?)");
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }

            if (CollectionUtils.isNotEmpty(query.getStates())) {
                condition.append(" and CO.state in ");
                condition.append(HelperFunc.makeInArg(query.getStates(), s -> String.valueOf(s.getId())));
            }

            if (query.getCreatedFrom() != null) {
                condition.append(" and employee registration.employment_date >= ?");
                args.add(query.getCreatedFrom());
            }

            if (query.getCreatedTo() != null) {
                condition.append(" and employee registration.employment_date <= ?");
                args.add(query.getCreatedTo());
            }
        }));
    }
}