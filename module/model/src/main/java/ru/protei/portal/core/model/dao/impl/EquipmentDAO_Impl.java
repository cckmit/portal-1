package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.EquipmentDAO;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO оборудования
 */
public class EquipmentDAO_Impl extends PortalBaseJdbcDAO<Equipment> implements EquipmentDAO {

    @Override
    public List<Equipment> getListByQuery(EquipmentQuery query) {
        SqlCondition where = createSqlCondition(query);

        String join = "INNER JOIN decimal_number DN ON DN.equipment_id = equipment.id";

        return getList(
                new JdbcQueryParameters().
                        withJoins(join).
                        withCondition(where.condition, where.args).
                        withDistinct(true).
                        withOffset(query.getOffset()).
                        withLimit(query.getLimit())
        );
    }

    @Override
    public Long countByQuery( EquipmentQuery query ) {
        SqlCondition where = createSqlCondition(query);
        String join = "INNER JOIN decimal_number DN ON DN.equipment_id = equipment.id";

        return (long) getObjectsCount( where.condition, where.args, join, true );
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

            if ( !StringUtils.isEmpty( query.getName() ) ) {
                condition.append( " and equipment.name_by_spec like ? " );
                String likeArg = HelperFunc.makeLikeArg(query.getName(), true);
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
                String likeArg = HelperFunc.makeLikeArg( query.getRegisterNumber(), true);
                args.add(likeArg);
            }
        });
    }

}
