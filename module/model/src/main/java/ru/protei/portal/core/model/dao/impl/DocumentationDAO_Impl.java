package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DocumentationDAO;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.DocumentationQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class DocumentationDAO_Impl extends PortalBaseJdbcDAO<Documentation> implements DocumentationDAO {
    private static final String JOINS = "LEFT JOIN document_type DT ON DT.id = documentation.type_id";

    @Override
    public List<Documentation> getListByQuery(DocumentationQuery query) {
        SqlCondition where = createSqlCondition(query);
        return getList(new JdbcQueryParameters()
                .withJoins(JOINS)
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withOffset(query.getOffset())
                .withLimit(query.getLimit())
                .withSort(TypeConverters.createSort(query))
        );
    }

    @Override
    public Long countByQuery(DocumentationQuery query) {
        SqlCondition where = createSqlCondition(query);
        return (long) getObjectsCount(where.condition, where.args, JOINS, true);
    }

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(DataQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");
        }));
    }
}
