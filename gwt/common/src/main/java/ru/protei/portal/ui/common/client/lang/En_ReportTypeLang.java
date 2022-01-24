package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportType;

public class En_ReportTypeLang {

    @Inject
    public En_ReportTypeLang(Lang lang) {
        this.lang = lang;
    }

    public String getType(En_ReportType value) {
        if (value == null) {
            return lang.errUnknownResult();
        }
        switch (value) {
            case CASE_OBJECTS: return lang.reportTypeCaseObjects();
            case CASE_TIME_ELAPSED: return lang.reportTypeCaseTimeElapsed();
            case CASE_RESOLUTION_TIME: return lang.reportTypeCaseResolutionTime();
            case PROJECT: return lang.reportTypeProject();
            case CONTRACT: return lang.reportTypeContract();
            case NIGHT_WORK: return lang.reportNightWork();
            case YT_WORK: return lang.reportYtWork();
            case YT_DELIVERY: return lang.reportYtDelivery();
            default: return lang.unknownField();
        }
    }

    Lang lang;
}
