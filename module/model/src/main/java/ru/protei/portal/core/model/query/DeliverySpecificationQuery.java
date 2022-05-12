package ru.protei.portal.core.model.query;

public class DeliverySpecificationQuery extends BaseQuery {

    public DeliverySpecificationQuery() {}

    @Override
    public String toString() {
        return "DeliverySpecificationQuery{" +
                "searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
