package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_AdminState;

import java.util.Set;

public class UserLoginShortViewQuery extends BaseQuery {
    private En_AdminState adminState;
    private Set<String> loginSet;
    private Set<Long> personIds;
    private Set<Long> companyIds;

    public En_AdminState getAdminState() {
        return adminState;
    }

    public void setAdminState(En_AdminState adminState) {
        this.adminState = adminState;
    }

    public Set<String> getLoginSet() {
        return loginSet;
    }

    public void setLoginSet(Set<String> loginSet) {
        this.loginSet = loginSet;
    }

    public Set<Long> getPersonIds() {
        return personIds;
    }

    public void setPersonIds(Set<Long> personIds) {
        this.personIds = personIds;
    }

    public Set<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(Set<Long> companyIds) {
        this.companyIds = companyIds;
    }

    @Override
    public String toString() {
        return "UserLoginShortViewQuery{" +
                "adminState=" + adminState +
                ", loginSet=" + loginSet +
                ", personIds=" + personIds +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
