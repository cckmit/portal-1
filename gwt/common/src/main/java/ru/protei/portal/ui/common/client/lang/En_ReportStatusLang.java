package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportStatus;

public class En_ReportStatusLang {

    @Inject
    Lang lang;

    public String getStateName(En_ReportStatus state){

        if (state == null) {
            return lang.errUnknownResult();
        }

        switch (state){
            case CREATED: return lang.issueReportsCreated();
            case PROCESS: return lang.issueReportsProcess();
            case READY: return lang.issueReportsReady();
            case ERROR: return lang.issueReportsError();
            case CANCELLED: return lang.issueReportsCancelled();
            default: return lang.errUnknownResult();
        }
    }
}
