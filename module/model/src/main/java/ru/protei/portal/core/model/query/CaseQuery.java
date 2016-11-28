package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Date;
import java.util.List;

/**
 * Created by Mike on 02.11.2016.
 */
public class CaseQuery extends BaseQuery {

    private Long companyId;
    private Long productId;
    private Long managerId;
    private En_CaseType type;
    private List<Integer> stateIds;
    private List<Integer> importanceIds;

    private Date from;
    private Date to;

    public CaseQuery() {};

    public CaseQuery( En_CaseType type, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.type = type;
        this.limit = 1000;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) { this.productId = productId; }

    public En_CaseType getType() {
        return type;
    }

    public void setType( En_CaseType type ) {
        this.type = type;
    }

    public List<Integer> getStateIds() {
        return stateIds;
    }

    public void setStateIds(List<Integer> stateIds) { this.stateIds = stateIds; }

    public List<Integer> getImportanceIds() { return importanceIds; }

    public void setImportanceIds(List<Integer> importanceIds) { this.importanceIds = importanceIds; }

    public Date getFrom() { return from; }

    public void setFrom( Date from ) { this.from = from; }

    public Date getTo() { return to; }

    public void setTo( Date to ) { this.to = to; }

    public Long getManagerId () { return managerId; }

    public void setManagerId ( Long managerId ) { this.managerId = managerId; }

    @Override
    public String toString () {
        return "CaseQuery{" +
                "companyId=" + companyId +
                ", productId=" + productId +
                ", managerId=" + managerId +
                ", type=" + type +
                ", stateIds=" + stateIds +
                ", importanceIds=" + importanceIds +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
