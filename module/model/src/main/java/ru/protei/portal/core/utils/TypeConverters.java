package ru.protei.portal.core.utils;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.winter.jdbc.JdbcSort;

/**
 * Created by michael on 12.10.16.
 */
public class TypeConverters {
    public static JdbcSort.Direction toWinter (En_SortDir value) {
        return toWinter(value, null);
    }

    public static JdbcSort.Direction toWinter (En_SortDir value, JdbcSort.Direction def) {
        return value == null ? def : value == En_SortDir.ASC ? JdbcSort.Direction.ASC : JdbcSort.Direction.DESC;
    }

    public static JdbcSort.Direction toWinter (String value) {
        return En_SortDir.parse(value) == En_SortDir.DESC ? JdbcSort.Direction.DESC : JdbcSort.Direction.ASC;
    }

    public static JdbcSort createSort (DataQuery query) {
        return createSort(query, null);
    }

    public static JdbcSort createSort (DataQuery query, String sortFieldAlias) {
        return query.getSortField() == null ? null : new JdbcSort(
                toWinter(query.getSortDir(), JdbcSort.Direction.ASC),
                (sortFieldAlias != null ? sortFieldAlias + "." : "") + query.getSortField().getFieldName()
        );
    }
}
