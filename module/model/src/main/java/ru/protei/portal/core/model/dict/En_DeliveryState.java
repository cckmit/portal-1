package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_DeliveryState implements HasId {
    PRELIMINARY(39),
    PRE_RESERVE(40),
    RESERVE(41),
    ASSEMBLY(42),
    TEST(43),
    READY(44),
    SENT(45),
    WORK(46),
    ;
    private final int id;

    En_DeliveryState(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
