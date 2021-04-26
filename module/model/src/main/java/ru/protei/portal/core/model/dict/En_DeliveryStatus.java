package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_DeliveryStatus implements HasId {
    PRELIMINARY(1),
    PRE_RESERVE(2),
    RESERVE(3),
    ASSEMBLY(4),
    TEST(5),
    READY(6),
    SENT(7),
    WORK(8),
    ;
    private final int id;

    En_DeliveryStatus(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
