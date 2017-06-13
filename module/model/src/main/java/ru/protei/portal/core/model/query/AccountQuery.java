package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Set;

/**
 * Фильтр по учетным записям
 */
public class AccountQuery extends BaseQuery {
    private Set< En_AuthType > types;

    public AccountQuery() {}

    public AccountQuery( Set< En_AuthType > types, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super ( searchString, sortField, sortDir );
        this.types = types;
        this.limit = 1000;
    }

    public Set<En_AuthType> getTypes() {
        return types;
    }

    public void setTypes( Set<En_AuthType> types ) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "AccountQuery{" +
                "types=" + types +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
