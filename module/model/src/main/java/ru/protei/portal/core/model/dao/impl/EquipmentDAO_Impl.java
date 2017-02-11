package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.EquipmentDAO;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO оборудования
 */
public class EquipmentDAO_Impl extends PortalBaseJdbcDAO<Equipment> implements EquipmentDAO {

    @Override
    public List<Equipment> getListByQuery(EquipmentQuery query) {
        return listByQuery(query);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(EquipmentQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getName() != null && !query.getName().trim().isEmpty()) {
                condition.append( " and name_by_spec like ? " );
                String likeArg = HelperFunc.makeLikeArg(query.getName(), true);
                args.add(likeArg);
            }
//
//            if (query.getClassifierCode() != null && !query.getClassifierCode().trim().isEmpty()) {
//                condition.append( " and classifier_code like ? " );
//                String likeArg = HelperFunc.makeLikeArg(query.getClassifierCode(), true);
//                args.add(likeArg);
//            }
//
//            if ( query.getPAMR_RegisterNumber() != null && !query.getPAMR_RegisterNumber().trim().isEmpty() ) {
//                condition.append( " and pamr_reg_num like ? " );
//                String likeArg = HelperFunc.makeLikeArg(query.getPAMR_RegisterNumber(), true);
//                args.add(likeArg);
//            }
//
//            if ( query.getPDRA_RegisterNumber() != null && !query.getPDRA_RegisterNumber().trim().isEmpty() ) {
//                condition.append( " and pdra_reg_num like ? " );
//                String likeArg = HelperFunc.makeLikeArg(query.getPDRA_RegisterNumber(), true);
//                args.add(likeArg);
//            }

            if ( query.getStages() != null && !query.getStages().isEmpty() ) {
                condition.append(" and dev_stage in (" + query.getStages().stream().map((s) -> "\'" + s + "\'").collect( Collectors.joining(",")) + ")");
            }

            if ( query.getTypes() != null && !query.getTypes().isEmpty() ) {
                condition.append(" and type in (" + query.getTypes().stream().map((s) -> "\'" + s + "\'").collect( Collectors.joining(",")) + ")");
            }

        });
    }

}
