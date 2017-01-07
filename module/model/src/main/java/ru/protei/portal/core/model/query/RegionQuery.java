package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

/**
 * Запрос по регионам
 */
public class RegionQuery extends BaseQuery {

    En_RegionState state;

    public RegionQuery() {
        sortField = En_SortField.prod_name;
        sortDir = En_SortDir.ASC;
    }

    public RegionQuery( String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
    }

    public RegionQuery( En_RegionState state, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.state = state;
    }

    public En_RegionState getState() {
        return state;
    }

    public void setState(En_RegionState state) {
        this.state = state;
    }

}
