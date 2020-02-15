package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;

/**
 * Фильтр по сотрудникам
 */
public class EmployeeQuery extends BaseQuery {

    private Long id;

    private Boolean fired;

    private Boolean deleted;

    private Boolean onlyPeople;

    private Set<EntityOption> homeCompanies;

    private String workPhone;

    private String mobilePhone;

    private String ipAddress;

    private String email;

    private String departmentParent;

    public EmployeeQuery() {
        fired = false;
    }

    public EmployeeQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        this(null, null, null, null, searchString, null, null, null, null, null, sortField, sortDir);
    }

    public EmployeeQuery(Boolean fired, Boolean deleted, Boolean onlyPeople, En_SortField sortField, En_SortDir sortDir) {
        this(fired, deleted, onlyPeople, null, null, null, null, null, null, null, sortField, sortDir);
    }

    public EmployeeQuery(Boolean fired, Boolean deleted, Boolean onlyPeople, Set<EntityOption> homeCompanies, String searchString, String workPhone, String mobilePhone, String ipAddress, String email, String departmentParent, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        this.fired = fired;
        this.deleted = deleted;
        this.onlyPeople = onlyPeople;
        this.homeCompanies = homeCompanies;
        this.workPhone = workPhone;
        this.mobilePhone = mobilePhone;
        this.ipAddress = ipAddress;
        this.email = email;
        this.departmentParent = departmentParent;
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

    public String getDepartment() {
        return departmentParent;
    }

    public void setDepartmentParent(String departmentParent) {
        this.departmentParent = departmentParent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "EmployeeQuery{" +
                "id=" + id +
                ", fired=" + fired +
                ", deleted=" + deleted +
                ", onlyPeople=" + onlyPeople +
                ", homeCompanies=" + homeCompanies +
                ", workPhone='" + workPhone + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", email='" + email + '\'' +
                ", departmentParent='" + departmentParent + '\'' +
                '}';
    }
}
