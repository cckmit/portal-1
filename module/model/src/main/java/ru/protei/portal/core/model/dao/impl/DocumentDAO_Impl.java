package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class DocumentDAO_Impl extends PortalBaseJdbcDAO<Document> implements DocumentDAO {
    private static final String JOINS = "LEFT JOIN document_type DT ON DT.id = document.type_id";

    @Override
    public List<Document> getListByQuery(DocumentQuery query) {
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
    public Long countByQuery(DocumentQuery query) {
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
