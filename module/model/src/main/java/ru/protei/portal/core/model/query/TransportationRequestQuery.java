package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.struct.DateRange;

import java.util.Objects;

public class TransportationRequestQuery extends BaseQuery {
    private DateRange pickupDate;

    public TransportationRequestQuery() {}

    public TransportationRequestQuery(String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
    }

    public DateRange getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(DateRange pickupDate) {
        this.pickupDate = pickupDate;
    }

    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                pickupDate != null;
    }

    @Override
    public String toString() {
        return "TransportationRequestQuery{" +
                ", pickupDate=" + pickupDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportationRequestQuery)) return false;
        TransportationRequestQuery that = (TransportationRequestQuery) o;
        return Objects.equals(pickupDate, that.pickupDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pickupDate);
    }
}
