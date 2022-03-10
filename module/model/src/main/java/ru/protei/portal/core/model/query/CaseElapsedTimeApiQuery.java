package ru.protei.portal.core.model.query;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class CaseElapsedTimeApiQuery extends BaseQuery {

    private Date from;
    
    private Date to;

    private Set<Long> productIds;

    private List<Long> companyIds;

    private List<Long> authorIds;
    
    public CaseElapsedTimeApiQuery() {}

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
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
        return "CaseElapsedTimeApiQuery{" +
                "searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                ", from=" + from +
                ", to=" + to +
                ", productIds=" + productIds +
                ", companyIds=" + companyIds +
                ", authorIds=" + authorIds +
                '}';
    }
}
