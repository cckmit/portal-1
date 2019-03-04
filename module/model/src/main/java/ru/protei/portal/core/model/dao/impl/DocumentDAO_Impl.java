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
    private static final String JOINS = " LEFT JOIN case_object CO ON CO.id = document.project_id ";

    @Override
    public List<Document> getListByQuery(DocumentQuery query) {
        SqlCondition where = createSqlCondition(query);
        JdbcQueryParameters queryParameters = new JdbcQueryParameters()
                .withJoins(JOINS)
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query))
                .withOffset(query.getOffset());
        if (query.limit > 0) {
            queryParameters = queryParameters.withLimit(query.getLimit());
        }
        return getList(queryParameters);
    }

    @Override
    public int countByQuery(DocumentQuery query) {
        SqlCondition where = createSqlCondition(query);
        return getObjectsCount(where.condition, where.args, JOINS, true);
    }

    @Override
    public boolean checkInventoryNumberExists(long inventoryNumber) {
        return checkExistsByCondition(" inventory_number=?", inventoryNumber);
    }

    @Override
    public boolean checkDecimalNumberExists(String decimalNumber) {
        return checkExistsByCondition(" decimal_number=?", decimalNumber);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(DocumentQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            if (query.getOnlyIds() != null && query.getOnlyIds().isEmpty()) {
                condition.append("false");
                return;
            }

            condition.append("1=1");

            if (StringUtils.isNotEmpty(query.getSearchString())) {
                condition.append(" and (document.name like ? or CO.case_name like ?)");
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
                condition.append(" and (document.registrar_id=? or document.contractor_id=?) ");
                args.add(query.getManagerId());
                args.add(query.getManagerId());
            }

            if (query.getProjectId() != null) {
                condition.append(" and document.project_id= ?");
                args.add(query.getProjectId());
            }

            if (CollectionUtils.isNotEmpty(query.getOrganizationCodes())) {
                condition.append(" and (");
                condition.append(query.getOrganizationCodes()
                        .stream()
                        .map(oc -> " document.decimal_number like ? ")
                        .collect(Collectors.joining(" or ")));
                condition.append(" ) ");
                query.getOrganizationCodes().forEach(oc -> {
                    switch (oc) {
                        case PAMR:
                            args.add("ПАМР%");
                            break;
                        case PDRA:
                            args.add("ПДРА%");
                            break;
                    }
                });
            }

            if (query.getOnlyIds() != null) {
                String ids = HelperFunc.makeInArg(query.getOnlyIds(), false);
                condition.append(" and document.id in " + ids);
            }

            if (query.getApproved() != null) {
                condition.append(" and document.is_approved=?");
                args.add(query.getApproved());
            }

            if (CollectionUtils.isNotEmpty(query.getDecimalNumbers())) {
                condition.append(" and (");
                condition.append(query.getDecimalNumbers()
                        .stream()
                        .map(dn -> " document.decimal_number like ? ")
                        .collect(Collectors.joining(" or "))
                );
                condition.append(" ) ");
                query.getDecimalNumbers().forEach(dn -> args.add(HelperFunc.makeLikeArg(dn, false)));
            }

            if (CollectionUtils.isNotEmpty(query.getEquipmentIds())) {
                condition.append(" and document.equipment_id in ");
                condition.append(HelperFunc.makeInArg(query.getEquipmentIds(), false));
            }
        }));
    }
}