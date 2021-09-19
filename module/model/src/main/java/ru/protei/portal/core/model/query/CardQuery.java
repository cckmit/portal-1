package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.ArrayList;
import java.util.List;

public class CardQuery extends BaseQuery {

    private List<Long> typeIds;

    private List<Long> stateIds;

    private List<Long> managerIds;

    public CardQuery() {
    }

    public CardQuery(String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.typeIds = new ArrayList<>();
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
}
