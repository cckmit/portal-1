package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.io.Serializable;

/**
 * Created by michael on 12.10.16.
 */
public class ProductQuery extends BaseQuery implements Serializable {

    En_DevUnitState state;

    public ProductQuery() {
        sortField = En_SortField.prod_name;
        sortDir = En_SortDir.ASC;
    }

    public En_DevUnitState getState() {
        return state;
    }

    public void setState(En_DevUnitState state) {
        this.state = state;
    }
}
