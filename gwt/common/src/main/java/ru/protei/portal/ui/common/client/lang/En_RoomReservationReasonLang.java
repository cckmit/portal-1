package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_RoomReservationReason;

public class En_RoomReservationReasonLang {

    @Inject
    public En_RoomReservationReasonLang(Lang lang) {
        this.lang = lang;
    }

    public String getName(En_RoomReservationReason reason) {
        if (reason == null) {
            return "?";
        }
        switch (reason) {
            case NEGOTIATION: return lang.roomReservationReasonValue0();
            case MEETING: return lang.roomReservationReasonValue1();
            case PRESENTATION: return lang.roomReservationReasonValue2();
            case EDUCATION: return lang.roomReservationReasonValue3();
            case OTHER: return lang.roomReservationReasonValue4();
            case INTERVIEW: return lang.roomReservationReasonValue5();
            default: return "?";
        }
    }

    private Lang lang;
}
