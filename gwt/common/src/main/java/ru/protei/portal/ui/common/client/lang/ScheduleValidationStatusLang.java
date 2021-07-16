package ru.protei.portal.ui.common.client.lang;


import ru.protei.portal.core.model.dict.ScheduleValidationStatus;

public class ScheduleValidationStatusLang {

    public static String getValidationMessage(ScheduleValidationStatus status, Lang lang) {
        switch (status) {
            case HAS_OVERLAP:
                return lang.errorAbsenceHasTimeRangeOverlap();
            case INCORRECT_TIME_RANGE:
                return lang.errorAbsenceIncorrectTimeRange();
            case NEED_FEEL_DAYS:
                return lang.errorAbsenceNeedFeelDays();
            case NEED_FEEL_TIME_RANGES:
                return lang.errorAbsenceNeedFeelTimeRanges();
            case NEED_FEEL_SCHEDULE:
                return lang.errorAbsenceNeedFeelSchedule();
            default:
                return lang.error();
        }
    }
}
