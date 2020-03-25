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

        public Show(HasWidgets parent) {
            this.parent = parent;
        }

        public Show withCaseType(En_CaseType caseType) {
            this.caseType = caseType;
            return this;
        }
        public Show withCaseId(Long caseId) {
            this.caseId = caseId;
            return this;
        }
        public Show withModifyEnabled(boolean isModifyEnabled) {
            this.isModifyEnabled = isModifyEnabled;
            return this;
        }
        public Show withElapsedTimeEnabled(boolean isElapsedTimeEnabled) {
            this.isElapsedTimeEnabled = isElapsedTimeEnabled;
            return this;
        }
        public Show withPrivateVisible(boolean b) {
            this.isPrivateVisible = b;
            return this;
        }
        public Show withPrivateCase(boolean b) {
            this.isPrivateCase = b;
            return this;
        }
        public Show withTextMarkup(En_TextMarkup textMarkup) {
            this.textMarkup = textMarkup;
            return this;
        }
        public Show withExtendedPrivacyType(boolean b) {
            this.extendedPrivacyType = b;
            return this;
        }

        public HasWidgets parent;
        public En_CaseType caseType;
        public Long caseId;
        public boolean isElapsedTimeEnabled = false;
        public boolean isModifyEnabled = false;
        public boolean isPrivateVisible = false;
        public boolean isPrivateCase = false;
        public En_TextMarkup textMarkup = En_TextMarkup.MARKDOWN;
        public boolean extendedPrivacyType = false;
    }

    /**
     * Перезагрузить список комментариев
     */
    public static class Reload {
        public Reload() {}
    }
}
