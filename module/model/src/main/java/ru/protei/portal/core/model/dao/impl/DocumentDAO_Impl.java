package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentDAO_Impl extends PortalBaseJdbcDAO<Document> implements DocumentDAO {
    private static final String JOINS = "LEFT JOIN decimal_number DN ON DN.id = document.decimal_number_id";

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

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(DocumentQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");

            if (StringUtils.isNotEmpty(query.getSearchString())) {
                condition.append(" and (document.name like ? or document.project like ?)");
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }

            if (CollectionUtils.isNotEmpty(query.getKeywords())) {
                condition.append(" and document.tags in ");
                condition.append(HelperFunc.makeInArg(query.getKeywords()));
            }

            if (query.getFrom() != null) {
                condition.append(" and document.created >= ?");
                args.add(query.getFrom());
            }

            if (query.getTo() != null) {
                condition.append(" and document.created < ?");
                args.add(query.getTo());
            }

            if (query.getDocumentType() != null) {
                condition.append(" and document.type_id=?");
                args.add(query.getDocumentType().getId());
            }

            if (query.getManagerId() != null) {
                condition.append(" and document.manager_id=?");
                args.add(query.getManagerId());
            }

            if (CollectionUtils.isNotEmpty(query.getOrganizationCodes())) {
                String orgCodes = HelperFunc.makeInArg(query
                        .getOrganizationCodes()
                        .stream()
                        .map(Enum::toString)
                        .collect(Collectors.toSet()));
                condition.append(" and DN.org_code in " + orgCodes);
            }
        }));
    }
}
