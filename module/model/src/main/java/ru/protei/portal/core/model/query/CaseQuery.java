package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

/**
 * Created by Mike on 02.11.2016.
 */
public class CaseQuery extends BaseQuery {

    private Long companyId;
    private Long productId;
    private En_CaseType type;
    private Integer stateId;
    private Integer importanceId;

    /**
     * @TODO
     * переделать state и importance с id на список ids
     *
     *     private List<Integer> stateIds;
     *     private List<Integer> importanceIds;
     */

    public CaseQuery() {};

    public CaseQuery(En_CaseType type, EntityOption company, EntityOption product, String searchString, En_CaseState state, En_ImportanceLevel importance, En_SortField sortField, En_SortDir sortDir ) {
        this (type, company == null ? null : company.getId(), product == null ? null : product.getId(), searchString, state == null ? null : state.getId(), importance == null ?  null: importance.getId(), sortField, sortDir);
    }

    public CaseQuery( En_CaseType type, Long companyId, Long productId, String searchString, Integer stateId, Integer importanceId, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.type = type;
        this.companyId = companyId;
        this.productId = productId;
        this.stateId = stateId;
        this.importanceId = importanceId;

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

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getImportanceId() {
        return importanceId;
    }

    public void setImportanceId(Integer importanceId) {
        this.importanceId = importanceId;
    }
}
