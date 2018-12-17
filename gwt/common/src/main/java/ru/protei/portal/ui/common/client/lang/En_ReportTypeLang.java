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
            case CRM_CASE_OBJECTS: return lang.reportTypeCrmCaseObjects();
            case CRM_MANAGER_TIME: return lang.reportTypeCrmManagerTime();
            default: return lang.unknownField();
        }
    }

    Lang lang;
}
