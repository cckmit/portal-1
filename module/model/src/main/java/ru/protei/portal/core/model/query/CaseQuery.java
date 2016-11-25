package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.List;

/**
 * Created by Mike on 02.11.2016.
 */
public class CaseQuery extends BaseQuery {

    private Long companyId;
    private Long productId;
    private En_CaseType type;
    private List<Integer> stateIds;
    private List<Integer> importanceIds;

    public CaseQuery() {};

    public CaseQuery( En_CaseType type, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.type = type;

        this.limit = 1000;
    };

/*    public CaseQuery( En_CaseType type, EntityOption company, EntityOption product, String searchString, List<En_CaseState> state, List<En_ImportanceLevel> importance, En_SortField sortField, En_SortDir sortDir ) {
        this (type, company == null ? null : company.getId(), product == null ? null : product.getId(), searchString, state == null ? null : state.getId(), importance == null ?  null: importance.getId(), sortField, sortDir);
    }*/

    public CaseQuery( En_CaseType type, Long companyId, Long productId, String searchString, List<Integer> stateIds, List<Integer> importanceIds, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.type = type;
        this.companyId = companyId;
        this.productId = productId;
        this.stateIds = stateIds;
        this.importanceIds = importanceIds;

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

    public void setStateIds(List<Integer> stateIds) {
        this.stateIds = stateIds;
    }

    public List<Integer> getImportanceIds() { return importanceIds; }

    public void setImportanceIds(List<Integer> importanceIds) {
        this.importanceIds = importanceIds;
    }
}
