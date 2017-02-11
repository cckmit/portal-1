package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import ru.protei.portal.core.model.dao.DecimalNumberDAO;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.HelperFunc;
import sun.swing.BakedArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DAO децимальных номеров
 */
public class DecimalNumberDAO_Impl extends PortalBaseJdbcDAO<DecimalNumber > implements DecimalNumberDAO {

    @Override
    public boolean checkIfExist( DecimalNumber number ) {
        String condition = "org_code=? and classifier_code=? and reg_number=?";
        List<Object> args = new ArrayList<>( Arrays.asList( number.getOrganizationCode().name(), number.getClassifierCode(), number.getRegisterNumber() ) );

        if ( number.getModification() != null && !number.getModification().isEmpty() ) {
            condition += " and modification_number=?";
            args.add( number.getModification() );
        }

        List< DecimalNumber > numbers = getListByCondition( condition, args );
        return CollectionUtils.size( numbers ) > 0;
    }

    @Override
    public Integer getMaxRegisterNumber( DecimalNumber number ) {
        return getMaxValue( "reg_number", Integer.class, "org_code=? and classifier_code=?", number.getOrganizationCode().name(),
                number.getClassifierCode() );
    }

    @Override
    public Integer getMaxModification( DecimalNumber number ) {
        return getMaxValue( "modification_number", Integer.class, "org_code=? and classifier_code=? and reg_number", number.getOrganizationCode().name(),
                number.getClassifierCode(), number.getRegisterNumber() );
    }
}
