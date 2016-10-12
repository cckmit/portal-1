package ru.protei.portal.core.model.dict;

import ru.protei.winter.jdbc.JdbcSort;

/**
 * Created by michael on 10.10.16.
 */
public enum En_SortDir {

    ASC,
    DESC;


    public static En_SortDir parse (String value) {
        return value == null || value.isEmpty() ? ASC :
                DESC.name().equalsIgnoreCase(value.trim()) ? DESC : ASC;
    }

}
