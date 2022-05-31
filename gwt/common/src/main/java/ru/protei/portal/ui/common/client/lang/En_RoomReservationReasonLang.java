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
            case NEGOTIATION: return lang.roomReservationReasonNegotiation();
            case MEETING: return lang.roomReservationReasonMeeting();
            case PRESENTATION: return lang.roomReservationReasonPresentation();
            case EDUCATION: return lang.roomReservationReasonEducation();
            case INTERVIEW: return lang.roomReservationReasonInterview();
            case OTHER: return lang.roomReservationReasonOther();
            default: return "?";
        }
    }

    private Lang lang;
}
