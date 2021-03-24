package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.BaseQuery;

import java.io.Serializable;

public class CaseFilterDto<T extends BaseQuery> implements Serializable {
    private CaseFilter caseFilter;
    private T query;

    public CaseFilterDto() {}

    public CaseFilterDto(CaseFilter caseFilter, T query) {
        this.caseFilter = caseFilter;
        this.query = query;
    }

    public CaseFilter getCaseFilter() {
        return caseFilter;
    }

    public void setCaseFilter(CaseFilter caseFilter) {
        this.caseFilter = caseFilter;
    }

    public T getQuery() {
        return query;
    }

    public void setQuery(T query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "CaseFilterDto{" +
                "caseFilter=" + caseFilter +
                ", query=" + query +
                '}';
    }
}
