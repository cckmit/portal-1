package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_AbsenceReason;

public class En_AbsenceReasonLang {

    public String getName(En_AbsenceReason reason) {
        if (reason == null) {
            return null;
        }
        switch (reason) {
            case PERSONAL_AFFAIR:
                return lang.absenceReasonPersonAffair();
            case BUSINESS_TRIP:
                return lang.absenceReasonBusinessTrip();
            case LOCAL_BUSINESS_TRIP:
                return lang.absenceReasonLocalBusinessTrip();
            case STUDY:
                return lang.absenceReasonStudy();
            case DISEASE:
                return lang.absenceReasonDisease();
            case SICK_LEAVE:
                return lang.absenceReasonSickLeave();
            case GUEST_PASS:
                return lang.absenceReasonGuestPass();
            case NIGHT_WORK:
                return lang.absenceReasonNightWork();
            case LEAVE_WITHOUT_PAY:
                return lang.absenceReasonLeaveWithoutPay();
            case DUTY:
                return lang.absenceReasonDuty();
            case REMOTE_WORK:
                return lang.absenceReasonRemoteWork();
            case LEAVE:
                return lang.absenceReasonLeave();
            default:
                return lang.unknownField();
        }
    }

    public String getIcon(En_AbsenceReason reason) {
        if(reason == null)
            return "";

        switch (reason){
            case PERSONAL_AFFAIR: return "text-danger fa-light fa-person-walking";

            case BUSINESS_TRIP:
            case LOCAL_BUSINESS_TRIP: return "text-complete fa-light fa-briefcase";

            case STUDY: return "hint-text fa-light fa-graduation-cap";

            case DISEASE:
            case SICK_LEAVE: return "text-danger fa-light fa-house-medical";

            case NIGHT_WORK: return "hint-text fa-light fa-moon";
            case DUTY: return "hint-text fa-light fa-phone-office";

            case LEAVE_WITHOUT_PAY: return "text-warning fa-light fa-brightness-low";

            case LEAVE: return "text-warning fa-light fa-sun-bright";

            case REMOTE_WORK: return "text-success fa-light fa-house-user";

            case GUEST_PASS: return "text-success fa-light fa-address-card";

            default:
                return "";
        }
    }

    @Inject
    private Lang lang;
}
