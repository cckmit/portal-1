package ru.protei.portal.core.model.util.sqlcondition;

import ru.protei.portal.core.model.dict.En_SortDir;

import java.io.Serializable;

public class SortField implements Serializable {
    public SortField( String fieldName, En_SortDir sortDirection ) {
        this.fieldName = fieldName;
        this.sortDirection = sortDirection;
    }

    public SortField() {

    }

    public String fieldName;
    public En_SortDir sortDirection;

    @Override
    public String toString() {
        return "SortField{" +
                "sortDirection=" + sortDirection +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}
