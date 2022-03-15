package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class EmployeeApiQuery extends BaseQuery {
    private List<Long> ids;
    
    @JsonAlias({"name", "displayName"})
    private String displayName;

    private String email;

    private String workPhone;

    private String mobilePhone;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @Override
    public String toString() {
        return "EmployeeApiQuery{" +
                "ids=" + ids +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", workPhone='" + workPhone + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                '}';
    }
}
