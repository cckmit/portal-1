package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.HasFilterQueryIds;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.core.model.view.filterwidget.Filter;

import java.io.Serializable;

public class CaseFilterDto<T extends HasFilterQueryIds>
        implements Serializable, Filter<FilterShortView, T> {

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

    @Override
    public Long getId() {
        return caseFilter.getId();
    }

    @Override
    public void setId(Long id) {
        caseFilter.setId(id);
    }

    @Override
    public String getName() {
        return caseFilter.getName();
    }

    @Override
    public FilterShortView toShortView() {
        return new FilterShortView(getId(), getName());
    }

    @Override
    public T getQuery() {
        return query;
    }

    @Override
    public SelectorsParams getSelectorsParams() {
        return caseFilter.getSelectorsParams();
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
