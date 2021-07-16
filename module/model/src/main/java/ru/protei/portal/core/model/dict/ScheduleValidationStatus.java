package ru.protei.portal.core.model.dict;

public enum ScheduleValidationStatus {
    OK,
    HAS_OVERLAP,
    INCORRECT_TIME_RANGE,
    NEED_FEEL_DAYS,
    NEED_FEEL_TIME_RANGES,
    NEED_FEEL_SCHEDULE
}
