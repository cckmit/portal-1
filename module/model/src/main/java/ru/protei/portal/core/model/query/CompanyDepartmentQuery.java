package ru.protei.portal.core.model.query;


import java.util.Set;

public class CompanyDepartmentQuery extends BaseQuery {

    private Long companyId;
    private Long personId;
    private Set<Long> departmentsIds;

    public CompanyDepartmentQuery() {
    }

    public CompanyDepartmentQuery(Long companyId, Long personId) {
        this.companyId = companyId;
        this.personId = personId;
    }

    public CompanyDepartmentQuery(Long personId) {
        this.personId = personId;
    }

    public CompanyDepartmentQuery(Set<Long> departmentsIds) {
        this.departmentsIds = departmentsIds;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Set<Long> getDepartmentsIds() {
        return departmentsIds;
    }

    public void setDepartmentsIds(Set<Long> departmentsIds) {
        this.departmentsIds = departmentsIds;
    }

    @Override
    public String toString() {
        return "CompanyDepartmentQuery{" +
                "companyId=" + companyId +
                ", personId=" + personId +
                '}';
    }
}
