package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_RoomReservationReason implements HasId {
    NEGOTIATION(0),
    MEETING(1),
    PRESENTATION(2),
    EDUCATION(3),
    OTHER(4),
    INTERVIEW(5)
    ;

    En_RoomReservationReason(int id) {
        this.id = id;
    }
    private final int id;
    public int getId() { return id; }
}
