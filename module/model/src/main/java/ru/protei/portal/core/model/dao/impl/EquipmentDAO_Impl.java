package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.EquipmentDAO;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

/**
 * DAO оборудования
 */
public class EquipmentDAO_Impl extends PortalBaseJdbcDAO<Equipment> implements EquipmentDAO {

    private final static String DECIMAL_NUMBER_JOIN = "LEFT JOIN decimal_number ON decimal_number.entity_id = equipment.id";

    @Override
    public SearchResult<Equipment> getSearchResult(EquipmentQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @Override
    public Long count(DataQuery query) {
        StringBuilder sql = new StringBuilder("select count(*) from ").append(getTableName());

        SqlCondition whereCondition = createSqlCondition(query);

        if (!whereCondition.condition.isEmpty()) {
            sql.append(" where ").append(whereCondition.condition);
        }

        return jdbcTemplate.queryForObject(sql.toString(), Long.class, whereCondition.args.toArray());
    }

    private JdbcQueryParameters buildJdbcQueryParameters(EquipmentQuery query) {
        SqlCondition where = createSqlCondition(query);
        return new JdbcQueryParameters().
                withJoins(DECIMAL_NUMBER_JOIN).
                withCondition(where.condition, where.args).
                withDistinct(true).
                withOffset(query.getOffset()).
                withLimit(query.getLimit()).
                withSort(TypeConverters.createSort(query));
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(EquipmentQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if ( !StringUtils.isEmpty( query.getSearchString() ) ) {
                condition.append( " and ( equipment.name like ? or case_object.case_name like ? )" );
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }

            if ( !CollectionUtils.isEmpty( query.getTypes() ) ) {
                condition.append(" and equipment.type in (" + query.getTypes().stream().map((s) -> "\'" + s + "\'").collect( Collectors.joining(",")) + ")");
            }

            if ( !CollectionUtils.isEmpty( query.getOrganizationCodes() ) ) {
                condition.append(" and decimal_number.org_code in (" + query.getOrganizationCodes().stream().map((s) -> "\'" + s + "\'").collect( Collectors.joining(",")) + ")");
            }

            if ( !StringUtils.isEmpty( query.getClassifierCode() ) ) {
                condition.append(" and (LPAD(decimal_number.classifier_code, " + CrmConstants.ClassifierCode.MAX_SIZE + ", 0) like ?)");
                String likeArg = HelperFunc.makeLikeArg(query.getClassifierCode(), true);
                args.add(likeArg);
            }

            if ( !StringUtils.isEmpty( query.getRegisterNumber() ) ) {
                condition.append(" and (LPAD(decimal_number.reg_number, " + CrmConstants.RegistrationNumber.MAX_SIZE + ", 0) like ?)");
                String likeArg = HelperFunc.makeLikeArg(query.getRegisterNumber(), true);
                args.add(likeArg);
            }

            if ( query.getManagerId() != null ) {
                condition.append( " and manager_id=? " );
                args.add(query.getManagerId());
            }

            if ( query.getEquipmentId() != null ) {
                condition.append(" and linked_equipment_id=? ");
                args.add(query.getEquipmentId());
            }

            if (query.getProjectIds() != null) {
                condition.append(" and project_id in " + (query.getProjectIds().isEmpty() ? "('')" : makeInArg(query.getProjectIds(), false)));
            }
        });
    }

}
