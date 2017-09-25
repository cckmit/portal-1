package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import ru.protei.portal.core.model.dao.DecimalNumberDAO;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.struct.DecimalNumberFilter;

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
        String condition = "org_code=? and classifier_code=? and reg_number=?";
        List<Object> args = new ArrayList<>( Arrays.asList( number.getOrganizationCode().name(), number.getClassifierCode(), number.getRegisterNumber() ) );

        if ( number.getModification() != null ) {
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
        return getMaxValue( "modification_number", Integer.class, "org_code=? and classifier_code=? and reg_number=?", number.getOrganizationCode().name(),
                number.getClassifierCode(), number.getRegisterNumber() );
    }

    @Override
    public List< Long > getDecimalNumbersByEquipmentId( Long id ) {
        StringBuilder sql = new StringBuilder("select id from ").append(getTableName()).append( " where equipment_id=?" );
        return jdbcTemplate.queryForList(sql.toString(), Long.class, id);
    }

    @Override
    public Integer getNextAvailableRegNumber(DecimalNumber number) {

        String sql = "select min(a.reg_number) + 1 from (select reg_number from decimal_number union select 0) a " +
                "left join decimal_number b on b.reg_number = a.reg_number + 1 " +
                "and classifier_code=? and org_code=? where b.reg_number is null";
        return jdbcTemplate.queryForObject(sql, Integer.class, number.getClassifierCode(), number.getOrganizationCode().name());
    }

    @Override
    public Integer getNextAvailableRegNumberNotContainsInList(DecimalNumberFilter filter) {
        StringBuilder sql = new StringBuilder("select min(a.reg_number) + 1 from (select reg_number from decimal_number union select 0) a " +
                "left join decimal_number b on b.reg_number = a.reg_number + 1 " +
                "and classifier_code=? and org_code=? where b.reg_number is null");

        if (!filter.getExcludeNumbers().isEmpty()) {
            sql.append(" and a.reg_number not in (");

            List<Integer> excludeNumbers = filter.getExcludeNumbers();
            sql.append(excludeNumbers.stream().map(p -> String.valueOf(p-1)).collect(Collectors.joining(",")));
            sql.append(")");
        }
        return jdbcTemplate.queryForObject(sql.toString(), Integer.class, filter.getClassifierCode(), filter.getOrganizationCode());
    }

    @Override
    public Integer getNextAvailableModification(DecimalNumber number) {
        String sql = "select min(a.modification_number) + 1 from (select modification_number from decimal_number union select 0) a " +
                "left join decimal_number b on b.modification_number = a.modification_number + 1 " +
                "and classifier_code=? and org_code=? and reg_number=? where b.modification_number is null";
        return jdbcTemplate.queryForObject(sql, Integer.class, number.getClassifierCode(), number.getOrganizationCode().name(), number.getRegisterNumber());
    }

    @Override
    public Integer getNextAvailableRegisterNumberModificationNotContainsInList(DecimalNumberFilter filter) {
        StringBuilder sql = new StringBuilder("select min(a.modification_number) + 1 from (select modification_number from decimal_number union select 0) a " +
                "left join decimal_number b on b.modification_number = a.modification_number + 1 " +
                "and classifier_code=? and org_code=? and reg_number=? where b.modification_number is null");

        sql.append(" and a.modification_number not in (");

        List<Integer> excludeNumbers = filter.getExcludeNumbers();

        sql.append(excludeNumbers.stream().map(p -> String.valueOf(p-1)).collect(Collectors.joining(",")));
        sql.append(")");

        return jdbcTemplate.queryForObject(sql.toString(), Integer.class, filter.getClassifierCode(), filter.getOrganizationCode(), filter.getRegisterNumber());
    }
}
