package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
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

    @Override
    public boolean checkIfExistPAMR_RegNum( String classifierCode, String registerNumber ) {
        List< Equipment > equipments = getListByCondition( "Equipment.classifier_code=? and pamr_reg_num=?", classifierCode, registerNumber );
        return CollectionUtils.size( equipments ) > 0;
    }

    @Override
    public boolean checkIfExistPDRA_RegNum( String classifierCode, String registerNumber ) {
        List< Equipment > equipments = getListByCondition( "Equipment.classifier_code=? and pdra_reg_num=?", classifierCode, registerNumber );
        return CollectionUtils.size( equipments ) > 0;
    }

    @Override
    public String getMaxPDRA_RegNum( String classifierCode ) {
        return getMaxValue("SUBSTRING(pdra_reg_num, 1, 3)", String.class, "classifier_code=?", classifierCode);
    }

    @Override
    public String getMaxPAMR_RegNum( String classifierCode ) {
        return getMaxValue("SUBSTRING(pamr_reg_num, 1, 3)", String.class, "classifier_code=?", classifierCode);
    }

    @Override
    public String getMaxPAMR_RegNumModification( String classifierCode, String registerNumber ) {
        return getMaxValue("SUBSTRING(pamr_reg_num, 4, 7)", String.class, "classifier_code=? and SUBSTRING(pamr_reg_num, 1, 3)=?", classifierCode, registerNumber.substring( 1, 3 ));
    }

    @Override
    public String getMaxPDRA_RegNumModification( String classifierCode, String registerNumber ) {
        return getMaxValue("SUBSTRING(pdra_reg_num, 4, 7)", String.class, "classifier_code=? and SUBSTRING(pdra_reg_num, 1, 3)=?", classifierCode, registerNumber.substring( 1, 3 ));
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

            if (query.getClassifierCode() != null && !query.getClassifierCode().trim().isEmpty()) {
                condition.append( " and classifier_code like ? " );
                String likeArg = HelperFunc.makeLikeArg(query.getClassifierCode(), true);
                args.add(likeArg);
            }
            
            if ( query.getPAMR_RegisterNumber() != null && !query.getPAMR_RegisterNumber().trim().isEmpty() ) {
                condition.append( " and pamr_reg_num like ? " );
                String likeArg = HelperFunc.makeLikeArg(query.getPAMR_RegisterNumber(), true);
                args.add(likeArg);
            }

            if ( query.getPDRA_RegisterNumber() != null && !query.getPDRA_RegisterNumber().trim().isEmpty() ) {
                condition.append( " and pdra_reg_num like ? " );
                String likeArg = HelperFunc.makeLikeArg(query.getPDRA_RegisterNumber(), true);
                args.add(likeArg);
            }

            if ( query.getStages() != null && !query.getStages().isEmpty() ) {
                condition.append(" and dev_stage in (" + query.getStages().stream().map((s) -> "\'" + s + "\'").collect( Collectors.joining(",")) + ")");
            }

            if ( query.getTypes() != null && !query.getTypes().isEmpty() ) {
                condition.append(" and type in (" + query.getTypes().stream().map((s) -> "\'" + s + "\'").collect( Collectors.joining(",")) + ")");
            }

        });
    }

}
