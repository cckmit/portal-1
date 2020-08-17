package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_TextMarkup;

public class CaseCommentEvents {

    /**
     * Показать комментарии
     */
    public static class Show {

        public Show() {}

        public Show(HasWidgets parent, Long caseId, En_CaseType caseType, boolean isModifyEnabled) {
            this.parent = parent;
            this.caseId = caseId;
            this.caseType = caseType;
            this.isModifyEnabled = isModifyEnabled;
        }

        public HasWidgets parent;
        public En_CaseType caseType;
        public Long caseId;
        public boolean isElapsedTimeEnabled = false;
        public boolean isModifyEnabled = false;
        public boolean isPrivateVisible = false;
        public boolean isPrivateCase = false;
        public boolean isNewCommentEnabled = true;
        public En_TextMarkup textMarkup = En_TextMarkup.MARKDOWN;
    }

    /**
     * Перезагрузить список комментариев
     */
    public static class Reload {
        public Reload() {}
    }

    public static class DisableNewComment {
        public DisableNewComment() {}
    }
}
