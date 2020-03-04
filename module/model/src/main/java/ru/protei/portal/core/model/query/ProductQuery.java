package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by michael on 12.10.16.
 */
public class ProductQuery extends BaseQuery {

    private En_DevUnitState state;
    private Set<En_DevUnitType> types;
    private Long directionId;

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

    public Long getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Long productDirectionId) {
        this.directionId = productDirectionId;
    }

    public void addType(En_DevUnitType type) {
        if (this.types == null) {
            this.types = new HashSet<>();
        }
        this.types.add(type);
    }

    public void addTypes(Set<En_DevUnitType> types) {
        if (this.types == null) {
            this.types = new HashSet<>();
        }

        if (types != null) {
            this.types.addAll(types);
        }
    }

}
