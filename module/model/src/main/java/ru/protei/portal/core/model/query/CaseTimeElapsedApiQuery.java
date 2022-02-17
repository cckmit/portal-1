package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.struct.DateRange;

import java.util.List;
import java.util.Set;

public class CaseTimeElapsedApiQuery extends BaseQuery {

    private DateRange period;       //createdRange;

    private Set<Long> productIds;

    private List<Long> companyIds;

    private List<Long> employeeIds;     // commentAuthorIds;

    private List<Integer> timeElapsedTypeIds;

    private List<String> caseTagsNames;
    
    public CaseTimeElapsedApiQuery() {}

    public DateRange getPeriod() {
        return period;
    }

    public void setPeriod(DateRange period) {
        this.period = period;
    }

    public Set<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(Set<Long> productIds) {
        this.productIds = productIds;
    }

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds;
    }

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public List<Integer> getTimeElapsedTypeIds() {
        return timeElapsedTypeIds;
    }

    public void setTimeElapsedTypeIds(List<Integer> timeElapsedTypeIds) {
        this.timeElapsedTypeIds = timeElapsedTypeIds;
    }

    public List<String> getCaseTagsNames() {
        return caseTagsNames;
    }

    public void setCaseTagsNames(List<String> caseTagsNames) {
        this.caseTagsNames = caseTagsNames;
    }

    @Override
    public String toString() {
        return "CaseTimeElapsedApiQuery{" +
                "searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                ", period=" + period +
                ", productIds=" + productIds +
                ", companyIds=" + companyIds +
                ", employeeIds=" + employeeIds +
                ", timeElapsedTypeIds=" + timeElapsedTypeIds +
                ", caseTagsNames=" + caseTagsNames +
                '}';
    }
}
