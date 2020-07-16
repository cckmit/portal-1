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
            case PERSONAL_AFFAIR: return "linearicons-user";
            case BUSINESS_TRIP:
            case LOCAL_BUSINESS_TRIP: return "linearicons-briefcase";
            case STUDY: return "linearicons-library2";
            case DISEASE:
            case SICK_LEAVE: return "linearicons-first-aid";
            case NIGHT_WORK:
            case DUTY: return "linearicons-moon";
            case LEAVE_WITHOUT_PAY:
            case LEAVE: return "linearicons-sun";
            case REMOTE_WORK: return "linearicons-home";
            case GUEST_PASS: return "linearicons-profile";
            default:
                return "";
        }
    }

    @Inject
    private Lang lang;
}
