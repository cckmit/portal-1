package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_DeliveryAttribute implements HasId {
    DELIVERY(1),
    TEST(2),
    PILOT_ZONE(3);

    private final int id;

    En_DeliveryAttribute(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
