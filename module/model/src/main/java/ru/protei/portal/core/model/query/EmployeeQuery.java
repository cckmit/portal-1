package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

/**
 * Created by turik on 30.11.16.
 */
public class EmployeeQuery extends BaseQuery {

    private Boolean fired;

    private Boolean deleted;

    private boolean searchByContactInfo = true;

    public EmployeeQuery() {
        fired = false;
        deleted = false;
    }

    public EmployeeQuery(Boolean fired, Boolean deleted, String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        this.fired = fired;
        this.deleted = deleted;
        this.limit = 1000;
    }

    public Boolean getFired() {
        return fired;
    }

    public void setFired(Boolean fired) {
        this.fired = fired;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public boolean getSearchByContactInfo() {
        return searchByContactInfo;
    }

    public void setSearchByContactInfo(boolean searchByContactInfo) {
        this.searchByContactInfo = searchByContactInfo;
    }

    @Override
    public String toString() {
        return "EmployeeQuery{" +
                "fired=" + fired +
                ", deleted=" + deleted +
                '}';
    }
}
