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

    public String getStyle(En_AbsenceReason state) {
        if(state == null)
            return "fa fa-unknown";

        switch (state){
            case PERSONAL_AFFAIR: return "bg-primary-lighter";
            case BUSINESS_TRIP:
            case LOCAL_BUSINESS_TRIP: return "bg-success-lighter";
            case STUDY: return "bg-danger-lighter";
            case DISEASE:
            case SICK_LEAVE: return "bg-info-lighter";
            case NIGHT_WORK:
            case DUTY: return "bg-complete-lighter";
            case LEAVE_WITHOUT_PAY:
            case LEAVE: return "bg-warning-lighter";

            default:
                return "";
        }
    }

    @Inject
    private Lang lang;
}
