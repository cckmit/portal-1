package ru.protei.portal.ui.delivery.client.activity.pcborder.table;

import ru.protei.winter.core.utils.enums.HasId;

public enum PcbOrderGroupType implements HasId {

    ACTIVE(1),
    COMPLETED(2);

    private final int id;

    PcbOrderGroupType(int id) {
        this.id = id;
    }

    public boolean isBefore(PcbOrderGroupType other) {
        return this.id > other.id;
    }

    public int getId() {
        return id;
    }
}
