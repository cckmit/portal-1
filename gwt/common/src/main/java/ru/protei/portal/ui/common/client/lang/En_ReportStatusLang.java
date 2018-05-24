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
            case CREATED: lang.issueReportsCreated();
            case PROCESS: lang.issueReportsProcess();
            case READY: lang.issueReportsReady();
            case ERROR: lang.issueReportsError();
            default: return lang.errUnknownResult();
        }
    }
}
