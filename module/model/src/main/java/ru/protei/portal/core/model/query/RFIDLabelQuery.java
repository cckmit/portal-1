package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.view.filterwidget.FilterQuery;

import java.util.Objects;

public class RFIDLabelQuery extends BaseQuery implements FilterQuery {
    private String epc;

    private String name;

    public RFIDLabelQuery() {}

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RFIDLabelQuery)) return false;
        RFIDLabelQuery that = (RFIDLabelQuery) o;
        return Objects.equals(epc, that.epc)
                && Objects.equals(name, that.name)
                && Objects.equals(searchString, that.searchString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epc, name, searchString);
    }
}
