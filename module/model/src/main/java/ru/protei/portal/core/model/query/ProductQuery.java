package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Set;

/**
 * Created by michael on 12.10.16.
 */
public class ProductQuery extends BaseQuery {

    En_DevUnitState state;
    private Set<En_DevUnitType> types;

    public ProductQuery() {
        sortField = En_SortField.prod_name;
        sortDir = En_SortDir.ASC;
    }

    public ProductQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
    }

    public ProductQuery(En_DevUnitState state, String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        this.state = state;
    }

    public En_DevUnitState getState() {
        return state;
    }

    public void setState(En_DevUnitState state) {
        this.state = state;
    }

    public Set<En_DevUnitType> getTypes() {
        return types;
    }

    public void setTypes(Set<En_DevUnitType> types) {
        this.types = types;
    }
}
