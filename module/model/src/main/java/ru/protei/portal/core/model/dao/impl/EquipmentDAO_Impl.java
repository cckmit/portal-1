package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.EquipmentDAO;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO оборудования
 */
public class EquipmentDAO_Impl extends PortalBaseJdbcDAO<Equipment> implements EquipmentDAO {

    private final static String DECIMAL_NUMBER_JOIN = "LEFT JOIN decimal_number DN ON DN.entity_id = equipment.id AND DN.entity_type = 'EQUIPMENT'";

    @Override
    public List<Equipment> getListByQuery(EquipmentQuery query) {
        SqlCondition where = createSqlCondition(query);
        return getList(
                new JdbcQueryParameters().
                        withJoins(DECIMAL_NUMBER_JOIN).
                        withCondition(where.condition, where.args).
                        withDistinct(true).
                        withOffset(query.getOffset()).
                        withLimit(query.getLimit()).
                        withSort( TypeConverters.createSort( query ) )
        );
    }

    @Override
    public Long countByQuery( EquipmentQuery query ) {
        SqlCondition where = createSqlCondition(query);
        return (long) getObjectsCount( where.condition, where.args, DECIMAL_NUMBER_JOIN, true );
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

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(EquipmentQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if ( !StringUtils.isEmpty( query.getSearchString() ) ) {
                condition.append( " and ( equipment.name like ? or equipment.project like ? )" );
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }

            if ( !CollectionUtils.isEmpty( query.getStages() ) ) {
                condition.append(" and equipment.dev_stage in (" + query.getStages().stream().map((s) -> "\'" + s + "\'").collect( Collectors.joining(",")) + ")");
            }

            if ( !CollectionUtils.isEmpty( query.getTypes() ) ) {
                condition.append(" and equipment.type in (" + query.getTypes().stream().map((s) -> "\'" + s + "\'").collect( Collectors.joining(",")) + ")");
            }

            if ( !CollectionUtils.isEmpty( query.getOrganizationCodes() ) ) {
                condition.append(" and DN.org_code in (" + query.getOrganizationCodes().stream().map((s) -> "\'" + s + "\'").collect( Collectors.joining(",")) + ")");
            }

            if ( !StringUtils.isEmpty( query.getClassifierCode() ) ) {
                condition.append( " and DN.classifier_code like ? " );
                String likeArg = HelperFunc.makeLikeArg(query.getClassifierCode(), true);
                args.add(likeArg);
            }

            if ( !StringUtils.isEmpty( query.getRegisterNumber() ) ) {
                condition.append( " and DN.reg_number like ? " );
                Integer regNum = Integer.parseInt( query.getRegisterNumber() );
                String likeArg = HelperFunc.makeLikeArg( regNum.toString(), true);
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
        });
    }

}
