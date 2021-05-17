package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.DeliveryFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.HasFilterQueryIds;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.core.model.view.filterwidget.Filter;

import java.io.Serializable;

public class DeliveryFilterDto<T extends HasFilterQueryIds>
        implements Serializable, Filter<FilterShortView, T> {

    private DeliveryFilter deliveryFilter;
    private T query;

    public DeliveryFilterDto() {}

    public DeliveryFilterDto(DeliveryFilter deliveryFilter, T query) {
        this.deliveryFilter = deliveryFilter;
        this.query = query;
    }

    public DeliveryFilter getDeliveryFilter() {
        return deliveryFilter;
    }

    public void setDeliveryFilter(DeliveryFilter deliveryFilter) {
        this.deliveryFilter = deliveryFilter;
    }

    @Override
    public Long getId() {
        return deliveryFilter.getId();
    }

    @Override
    public void setId(Long id) {
        deliveryFilter.setId(id);
    }

    @Override
    public String getName() {
        return deliveryFilter.getName();
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
        return deliveryFilter.getSelectorsParams();
    }

    public void setQuery(T query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "DeliveryFilterDto{" +
                "deliveryFilter=" + deliveryFilter +
                ", query=" + query +
                '}';
    }
}
