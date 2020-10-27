package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DateIntervalType;

/**
 * Названия типов временных интервалов
 */
public class En_DateIntervalLang {

    public String getName(En_DateIntervalType type) {
        switch (type) {
            case MONTH: return lang.monthInterval();
            case FIXED: return lang.fixedInterval();
            case UNLIMITED: return lang.unlimitedInterval();
            case TODAY: return lang.todayInterval();
            case YESTERDAY: return lang.yesterdayInterval();
            case THIS_WEEK: return lang.thisWeekInterval();
            case THIS_WEEK_AND_BEYOND: return lang.thisWeekAndBeyondInterval();
            case LAST_WEEK: return lang.lastWeekInterval();
            case NEXT_WEEK: return lang.nextWeekInterval();
            case THIS_MONTH: return lang.thisMonthInterval();
            case LAST_MONTH: return lang.lastMonthInterval();
            case PREVIOUS_AND_THIS_MONTH: return lang.lastPreviousAndThisMonthInterval();
            case NEXT_MONTH: return lang.nextMonthInterval();
            case THIS_YEAR: return lang.thisYearInterval();
            case LAST_YEAR: return lang.lastYearInterval();
            default: return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
