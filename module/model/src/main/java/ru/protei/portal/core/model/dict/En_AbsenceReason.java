package ru.protei.portal.core.model.dict;

public enum En_AbsenceReason {
    PERSONAL_AFFAIR(1),
    BUSINESS_TRIP(2),
    LOCAL_BUSINESS_TRIP(3),
    STUDY(4),
    DISEASE(5),
    SICK_LEAVE(6),
    GUEST_PASS(7),
    NIGHT_WORK(8),
    LEAVE_WITHOUT_PAY(9),
    DUTY(10),
    REMOTE_WORK(11);

    private final int id;

    En_AbsenceReason(int id) {
        this.id = id;
    }
}
