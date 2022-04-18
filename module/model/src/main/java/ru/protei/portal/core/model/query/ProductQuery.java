package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.List;
import java.util.Set;

/**
 * Created by michael on 12.10.16.
 */
public class ProductQuery extends BaseQuery {

    private List<Long> ids;
    private En_DevUnitState state;
    private Set<En_DevUnitType> types;
    private Set<Long> directionIds;
    private Set<Long> platformIds;

    public ProductQuery() {
        sortField = En_SortField.prod_name;
        sortDir = En_SortDir.ASC;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
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

    public Set<Long> getDirectionIds() {
        return directionIds;
    }

    public void setDirectionIds(Set<Long> productDirectionId) {
        this.directionIds = productDirectionId;
    }

    public Set<Long> getPlatformIds() {
        return platformIds;
    }

    public void setPlatformIds(Set<Long> platformIds) {
        this.platformIds = platformIds;
    }

    @Override
    public String toString() {
        return "ProductQuery{" +
                "ids=" + ids +
                "state=" + state +
                ", types=" + types +
                ", directionIds=" + directionIds +
                ", platformIds=" + platformIds +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
