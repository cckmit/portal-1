package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.dict.En_RegionState;

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

    public String getStateIcon(En_AbsenceReason state) {
        if(state == null)
            return "fa fa-unknown";

        switch (state){
            case PERSONAL_AFFAIR: return "fas fa-user-clock";
            case BUSINESS_TRIP: return "fas fa-plane-departure";
            case LOCAL_BUSINESS_TRIP: return "fas fa-business-time";
            case STUDY: return "fas fa-graduation-cap";
            case DISEASE: return "fas fa-viruses";
            case SICK_LEAVE: return "fas fa-hospital-user";
            case NIGHT_WORK: return "fas fa-cloud-moon";
            case LEAVE_WITHOUT_PAY: return "fas fa-umbrella-beach";
            case LEAVE: return "fas fa-umbrella-beach";
            case DUTY: return "fas fa-user-check";
            default:
                return "fa fa-unknown";
        }
    }

    @Inject
    private Lang lang;
}
