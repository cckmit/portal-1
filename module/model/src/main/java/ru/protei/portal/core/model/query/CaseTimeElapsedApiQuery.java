package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.struct.DateRange;

import java.util.List;
import java.util.Set;

public class CaseTimeElapsedApiQuery extends BaseQuery {

    private DateRange period;

    private Set<Long> productIds;

    private List<Long> companyIds;

    private List<Long> authorIds;
    
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

    public List<Long> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<Long> authorIds) {
        this.authorIds = authorIds;
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
                ", authorIds=" + authorIds +
                '}';
    }
}
