package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_CaseType;

public class CaseLinkEvents {

    public static class Show {
        public Show(HasWidgets parent, Long caseId, En_CaseType caseType) {
            this.parent = parent;
            this.caseId = caseId;
            this.caseType = caseType;
        }

        public HasWidgets parent;
        public Long caseId;
        public En_CaseType caseType;
    }
}
