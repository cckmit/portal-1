package ru.protei.portal.core.model.query;

import java.util.Objects;

public class RFIDLabelQuery extends BaseQuery {
    public RFIDLabelQuery() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RFIDLabelQuery query = (RFIDLabelQuery) o;
        return Objects.equals(searchString, query.searchString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchString);
    }
}
