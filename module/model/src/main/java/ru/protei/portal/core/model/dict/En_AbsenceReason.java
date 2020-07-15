package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_AbsenceReason implements HasId {
    PERSONAL_AFFAIR(1, true),
    GUEST_PASS(2, false),
    BUSINESS_TRIP(3, true),
    LOCAL_BUSINESS_TRIP(4, true),
    DISEASE(5, true),
    SICK_LEAVE(6, true),
    NIGHT_WORK(7, true),
    DUTY(8, true),
    STUDY(9, true),
    REMOTE_WORK(10, true),
    LEAVE(11, true),
    LEAVE_WITHOUT_PAY(12, true);

    private final int id;
    private final boolean actual;

    En_AbsenceReason(int id, boolean actual) {
        this.id = id;
        this.actual = actual;
    }

    @Override
    public int getId() {
        return id;
    }

    public boolean isActual() {
        return actual;
    }
}
