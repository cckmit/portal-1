package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;

/**
 * Фильтр по сотрудникам
 */
public class EmployeeQuery extends BaseQuery {

    private Boolean fired;

    private Boolean deleted;

    private Boolean onlyPeople;

    private Set<EntityOption> homeCompanies;

    private String workPhone;

    private String mobilePhone;

    private String ipAddress;

    private String email;

    public EmployeeQuery() {
        fired = false;
    }

    public EmployeeQuery(Boolean fired, Boolean onlyPeople, String searchString, En_SortField sortField, En_SortDir sortDir) {
        this(fired, null, onlyPeople, null, searchString, null, null, null, null, sortField, sortDir);
    }

    public EmployeeQuery(Boolean fired, Boolean deleted, Boolean onlyPeople, Set<EntityOption> homeCompanies, String searchString, String workPhone, String mobilePhone, String ipAddress, String email, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        this.fired = fired;
        this.deleted = deleted;
        this.onlyPeople = onlyPeople;
        this.homeCompanies = homeCompanies;
        this.workPhone = workPhone;
        this.mobilePhone = mobilePhone;
        this.ipAddress = ipAddress;
        this.email = email;
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

    public Boolean getOnlyPeople() {
        return onlyPeople;
    }

    public void setOnlyPeople(Boolean onlyPeople) {
        this.onlyPeople = onlyPeople;
    }

    public Set<EntityOption> getHomeCompanies() {
        return homeCompanies;
    }

    public void setHomeCompanies( Set<EntityOption> homeCompanies) {
        this.homeCompanies = homeCompanies;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone( String workPhone ) {
        this.workPhone = workPhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone( String mobilePhone ) {
        this.mobilePhone = mobilePhone;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress( String ipAddress ) {
        this.ipAddress = ipAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "EmployeeQuery{" +
                "fired=" + fired +
                ", deleted=" + deleted +
                ", onlyPeople=" + onlyPeople +
                ", homeCompanies=" + homeCompanies +
                ", workPhone='" + workPhone + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", email='" + email + '\'' +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
