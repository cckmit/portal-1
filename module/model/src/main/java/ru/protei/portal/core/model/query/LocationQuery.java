package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_LocationType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

/**
 * Запрос по локациям
 */
public class LocationQuery extends BaseQuery {

    En_LocationType type;

    public LocationQuery() {
        sortField = En_SortField.name;
        sortDir = En_SortDir.ASC;
    }

    public LocationQuery( En_LocationType type, En_SortField sortField, En_SortDir sortDir ) {
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.type = type;
    }

    public En_LocationType getType() {
        return type;
    }

    public void setType( En_LocationType type ) {
        this.type = type;
    }
}
