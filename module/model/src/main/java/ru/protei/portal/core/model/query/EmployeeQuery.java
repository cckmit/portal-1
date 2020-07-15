package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Фильтр по сотрудникам
 */
public class EmployeeQuery extends BaseQuery {

    private List<Long> ids;

    private Boolean fired;

    private Boolean deleted;

    private Boolean onlyPeople;

    private Set<EntityOption> homeCompanies;

    private String workPhone;

    private String mobilePhone;

    private String ipAddress;

    private String email;

    private String departmentParent;

    private String firstName;

    private String lastName;

    private String secondName;

    private Date birthday;

    private Set<Long> departmentIds;

    private Boolean absent;

    public EmployeeQuery() {
        fired = false;
    }

    public EmployeeQuery(List<Long> ids) {
        this.ids = ids;
    }

    public EmployeeQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        this(null, null, null, null, searchString, null, null, null, null, null, sortField, sortDir, null, null);
    }

    public EmployeeQuery(Boolean fired, Boolean deleted, Boolean onlyPeople, En_SortField sortField, En_SortDir sortDir) {
        this(fired, deleted, onlyPeople, null, null, null, null, null, null, null, sortField, sortDir, null, null);
    }

    public EmployeeQuery(Boolean fired, Boolean deleted, Boolean onlyPeople, Set<EntityOption> homeCompanies, String searchString, String workPhone, String mobilePhone, String ipAddress, String email, String departmentParent, En_SortField sortField, En_SortDir sortDir, List<Long> ids, Boolean absent) {
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
        this.ids = ids;
        this.absent = absent;
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

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Set<Long> getDepartmentIds() {
        return departmentIds;
    }

    public void setDepartmentIds(Set<Long> departmentIds) {
        this.departmentIds = departmentIds;
    }

    public Boolean getAbsent() {
        return absent;
    }

    public void setAbsent(Boolean absent) {
        this.absent = absent;
    }

    @Override
    public String toString() {
        return "EmployeeQuery{" +
                "ids=" + ids +
                "fired=" + fired +
                ", deleted=" + deleted +
                ", onlyPeople=" + onlyPeople +
                ", homeCompanies=" + homeCompanies +
                ", workPhone='" + workPhone + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", email='" + email + '\'' +
                ", departmentParent='" + departmentParent + '\'' +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", secondName=" + secondName +
                ", birthday=" + birthday +
                ", departmentIds=" + departmentIds +
                ", absent=" + absent +
                '}';
    }
}
