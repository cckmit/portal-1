package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DecimalNumberDAO;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO децимальных номеров
 */
public class DecimalNumberDAO_Impl extends PortalBaseJdbcDAO<DecimalNumber > implements DecimalNumberDAO {

    @Override
    public boolean checkExists(DecimalNumber number ) {
        SqlCondition condition = createSqlCondition(number);
        return checkExistsByCondition(condition.condition, condition.args);
    }

    @Override
    public List< Long > getDecimalNumbersByEquipmentId( Long id ) {
        StringBuilder sql = new StringBuilder("SELECT id FROM ").append(getTableName()).append( " WHERE entity_id=?" );
        return jdbcTemplate.queryForList(sql.toString(), Long.class, id);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(DecimalNumber number) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("org_code=? and classifier_code=? AND reg_number=? ");
            args.add(number.getOrganizationCode().name());
            args.add(number.getClassifierCode());
            args.add(number.getRegisterNumber());

            if ( number.getModification() != null ) {
                condition.append(" AND modification_number=?");
                args.add( number.getModification() );
            } else {
                condition.append(" AND modification_number IS NULL");
            }
        });
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
    public DecimalNumber find(DecimalNumber decimalNumber) {
        SqlCondition condition = createSqlCondition(decimalNumber);
        List<DecimalNumber> numbers = getListByCondition(condition.condition, condition.args);
        if (numbers == null || numbers.size() != 1) {
            return null;
        }
        return numbers.get(0);
    }
}
