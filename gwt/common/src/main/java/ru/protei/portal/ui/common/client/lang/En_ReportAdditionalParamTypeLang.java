package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportAdditionalParamType;

public class En_ReportAdditionalParamTypeLang {
    @Inject
    public En_ReportAdditionalParamTypeLang(Lang lang) {
        this.lang = lang;
    }

    public String getName(En_ReportAdditionalParamType value) {
        if (value == null) {
            return lang.errUnknownResult();
        }

        switch (value) {
            case IMPORTANCE_HISTORY: return lang.issueImportanceCheckHistory();
            case DESCRIPTION: return lang.issueReportWithDescription();
            case TAGS: return lang.issueReportWithTags();
            case LINKED_ISSUES: return lang.issueReportWithLinkedIssues();
            case HUMAN_READABLE: return lang.issueReportHumanReadable();
            case DEADLINE_WORK_TRIGGER: return lang.issueReportDeadlineWorkTrigger();
            default: return lang.unknownField();
        }
    }

    private Lang lang;
}
