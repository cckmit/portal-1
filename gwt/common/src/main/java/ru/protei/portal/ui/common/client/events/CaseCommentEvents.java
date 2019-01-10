package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_CaseType;

public class CaseCommentEvents {

    /**
     * Показать комментарии
     */
    public static class Show {

        public Show(HasWidgets parent, En_CaseType caseType, Long caseId) {
            this(parent, caseType, caseId, false);
        }

        public Show(HasWidgets parent, En_CaseType caseType, Long caseId, boolean isElapsedTimeEnabled) {
            this.parent = parent;
            this.caseType = caseType;
            this.caseId = caseId;
            this.isElapsedTimeEnabled = isElapsedTimeEnabled;
        }

        public HasWidgets parent;
        public En_CaseType caseType;
        public Long caseId;
        public boolean isElapsedTimeEnabled;

    }
}
