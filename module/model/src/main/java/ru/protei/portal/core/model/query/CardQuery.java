package ru.protei.portal.core.model.query;

import java.util.List;
import java.util.Objects;

public class CardQuery extends BaseQuery {

    private List<Long> typeIds;

    private List<Long> stateIds;

    private List<Long> managerIds;

    public CardQuery() {
    }

    public List<Long> getTypeIds() {
        return typeIds;
    }

    public void setTypeIds(List<Long> typeIds) {
        this.typeIds = typeIds;
    }

    public List<Long> getStateIds() {
        return stateIds;
    }

    public void setStateIds(List<Long> stateIds) {
        this.stateIds = stateIds;
    }

    public List<Long> getManagerIds() {
        return managerIds;
    }

    public void setManagerIds(List<Long> managerIds) {
        this.managerIds = managerIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardQuery cardQuery = (CardQuery) o;
        return Objects.equals(typeIds, cardQuery.typeIds)
                && Objects.equals(stateIds, cardQuery.stateIds)
                && Objects.equals(managerIds, cardQuery.managerIds)
                && Objects.equals(searchString, cardQuery.searchString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeIds, stateIds, managerIds, searchString);
    }
}
