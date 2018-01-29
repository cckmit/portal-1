package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

/**
 * Created by turik on 30.11.16.
 */
public class EmployeeQuery extends BaseQuery {

    private Boolean fired;

    private Boolean onlyPeople;

    public EmployeeQuery() {
        fired = false;
    }

    public EmployeeQuery(Boolean fired, Boolean onlyPeople, String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        this.fired = fired;
        this.onlyPeople = onlyPeople;
        this.limit = 1000;
    }

    public Boolean getFired() {
        return fired;
    }

    public void setFired(Boolean fired) {
        this.fired = fired;
    }

    public Boolean getOnlyPeople() {
        return onlyPeople;
    }

    public void setOnlyPeople( Boolean onlyPeople ) {
        this.onlyPeople = onlyPeople;
    }

    @Override
    public String toString() {
        return "EmployeeQuery{" +
                "fired=" + fired +
                "onlyPeople=" + onlyPeople +
                '}';
    }
}
