package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_AbsenceReason implements HasId {
    PERSONAL_AFFAIR(1, true),
    BUSINESS_TRIP(2, true),
    LOCAL_BUSINESS_TRIP(3, true),
    STUDY(4, true),
    DISEASE(5, true),
    SICK_LEAVE(6, true),
    GUEST_PASS(7, false),
    NIGHT_WORK(8, true),
    LEAVE_WITHOUT_PAY(9, true),
    DUTY(10, true),
    REMOTE_WORK(11, false),
    LEAVE(12, true);

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
