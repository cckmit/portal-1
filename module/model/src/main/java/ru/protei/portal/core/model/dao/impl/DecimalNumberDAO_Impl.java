package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import ru.protei.portal.core.model.dao.DecimalNumberDAO;
import ru.protei.portal.core.model.dict.En_DecimalNumberEntityType;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO децимальных номеров
 */
public class DecimalNumberDAO_Impl extends PortalBaseJdbcDAO<DecimalNumber > implements DecimalNumberDAO {

    @Override
    public boolean checkIfExist( DecimalNumber number ) {
        String condition = "org_code=? and classifier_code=? AND reg_number=? ";
        List<Object> args = new ArrayList<>( Arrays.asList( number.getOrganizationCode().name(), number.getClassifierCode(), number.getRegisterNumber() ) );

        if ( number.getModification() != null ) {
            condition += " AND modification_number=?";
            args.add( number.getModification() );
        } else {
            condition += " AND modification_number IS NULL";
        }

        List< DecimalNumber > numbers = getListByCondition( condition, args );
        return CollectionUtils.size( numbers ) > 0;
    }

    @Override
    public List< Long > getDecimalNumbersByEquipmentId( Long id ) {
        StringBuilder sql = new StringBuilder("SELECT id FROM ").append(getTableName()).append( " WHERE entity_id=? AND entity_type='EQUIPMENT'" );
        return jdbcTemplate.queryForList(sql.toString(), Long.class, id);
    }

    @Override
    public Integer getNextAvailableRegNumber(DecimalNumberQuery filter) {
        StringBuilder sql = new StringBuilder("SELECT MIN(a.reg_number) " +
                "FROM (SELECT reg_number+1 AS reg_number" +
                "      FROM decimal_number WHERE org_code=? AND classifier_code=? " +
                "      UNION SELECT 1 ");

        if (!filter.getExcludeNumbers().isEmpty()) {
            filter.getExcludeNumbers().forEach( regNum -> {
                sql.append(" UNION SELECT ");
                sql.append( regNum+1 );
            });
        }

        sql.append( ") a " +
                "     LEFT JOIN decimal_number b ON b.reg_number = a.reg_number" +
                "                                   AND org_code=? " +
                "                                   AND classifier_code=? " +
                "WHERE b.reg_number IS NULL");

        if (!filter.getExcludeNumbers().isEmpty()) {
            sql.append(" AND a.reg_number NOT IN (");
            sql.append(filter.getExcludeNumbers().stream().map( String::valueOf ).collect(Collectors.joining(",")));
            sql.append(")");
        }
        return jdbcTemplate.queryForObject(sql.toString(), Integer.class,
                filter.getNumber().getOrganizationCode().name(), filter.getNumber().getClassifierCode(),
                filter.getNumber().getOrganizationCode().name(), filter.getNumber().getClassifierCode());
    }

    @Override
    public Integer getNextAvailableModification(DecimalNumberQuery filter) {
        StringBuilder sql = new StringBuilder("SELECT MIN(a.modification_number) " +
                "FROM (SELECT modification_number+1 AS modification_number " +
                "      FROM decimal_number WHERE org_code=? AND classifier_code=? AND reg_number=? " +
                "      UNION SELECT 1 ");

        if (!filter.getExcludeNumbers().isEmpty()) {
            filter.getExcludeNumbers().forEach( mod -> {
                sql.append(" UNION SELECT ");
                sql.append( mod+1 );
            });
        }

        sql.append( ") a " +
                "     LEFT JOIN decimal_number b ON b.modification_number = a.modification_number " +
                "                                   AND org_code=? " +
                "                                   AND classifier_code=? " +
                "                                   AND reg_number=? " +
                "WHERE b.modification_number IS NULL");

        if (!filter.getExcludeNumbers().isEmpty()) {
            sql.append(" AND a.modification_number NOT IN (");
            sql.append(filter.getExcludeNumbers().stream().map( String::valueOf ).collect(Collectors.joining(",")));
            sql.append(")");
        }

        return jdbcTemplate.queryForObject(sql.toString(), Integer.class,
                filter.getNumber().getOrganizationCode().name(), filter.getNumber().getClassifierCode(), filter.getNumber().getRegisterNumber(),
                filter.getNumber().getOrganizationCode().name(), filter.getNumber().getClassifierCode(), filter.getNumber().getRegisterNumber());
   }

    @Override
    public void updateAllNumbersWithEmptyEntityType() {
        jdbcTemplate.batchUpdate("UPDATE " + getTableName() + " SET entity_type='" + En_DecimalNumberEntityType.EQUIPMENT.name() + "' WHERE entity_type IS NULL OR entity_type=''");
    }
}
