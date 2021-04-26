package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_DeliveryType implements HasId {
    UPGRADE(1),
    UPGRADE_HW(2),
    UPGRADE_SW(3),
    REPLACEMENT_HW(4),
    BUG_FIX(5),
    NEW_VERSION(6),
    NEW_VERSION_SW(7),
    NEW_DELIVERY(8),
    TRIAL_OPERATION(9),
    DELIVERY(10),
    SUPPORT(11),
    ;
    private final int id;

    En_DeliveryType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
