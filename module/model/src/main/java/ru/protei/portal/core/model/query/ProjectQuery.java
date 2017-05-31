package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Set;

/**
 * Запрос по регионам
 */
public class ProjectQuery extends BaseQuery {

    Set<En_RegionState> states;

    Set<Long> districtIds;

    Long directionId;

    public ProjectQuery() {
        sortField = En_SortField.prod_name;
        sortDir = En_SortDir.ASC;
    }

    public ProjectQuery( String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
    }

    public ProjectQuery( Set<En_RegionState> state, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.states = state;
    }

    public Set<En_RegionState> getStates() {
        return states;
    }

    public void setStates(Set<En_RegionState> state) {
        this.states = state;
    }

    public Set<Long> getDistrictIds() {
        return districtIds;
    }

    public void setDistrictIds( Set<Long> districtIds ) {
        this.districtIds = districtIds;
    }

    public Long getDirectionId() {
        return directionId;
    }

    public void setDirectionId( Long directionId ) {
        this.directionId = directionId;
    }
}
