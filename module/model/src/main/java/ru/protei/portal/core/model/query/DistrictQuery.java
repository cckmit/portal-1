package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Set;

/**
 * Запрос по федеральным округам
 */
public class DistrictQuery extends BaseQuery {
    public DistrictQuery() {
        sortField = En_SortField.prod_name;
        sortDir = En_SortDir.ASC;
    }

    public DistrictQuery( String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
    }
}
