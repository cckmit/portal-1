package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.CaseComment;

import java.util.function.Consumer;

public class CaseCommentEvents {

    /**
     * Показать комментарии
     */
    public static class Show {

        public static class Builder {
            private Show event;
            public Builder(HasWidgets parent) {
                event = new Show(parent);
            }
            public Builder withCaseType(En_CaseType caseType) {
                event.caseType = caseType;
                return this;
            }
            public Builder withCaseId(Long caseId) {
                event.caseId = caseId;
                return this;
            }
            public Builder withModifyEnabled(boolean isModifyEnabled) {
                event.isModifyEnabled = isModifyEnabled;
                return this;
            }
            public Builder withElapsedTimeEnabled(boolean isElapsedTimeEnabled) {
                event.isElapsedTimeEnabled = isElapsedTimeEnabled;
                return this;
            }
            public Builder withPrivateVisible(boolean b) {
                event.isPrivateVisible = b;
                return this;
            }
            public Builder withPrivateCase(boolean b) {
                event.isPrivateCase = b;
                return this;
            }
            public Builder withTextMarkup(En_TextMarkup textMarkup) {
                event.textMarkup = textMarkup;
                return this;
            }
            public Show build() {
                return event;
            }
        }

        public Show() {}

        public Show(HasWidgets parent) {
            this.parent = parent;
        }

        public HasWidgets parent;
        public En_CaseType caseType;
        public Long caseId;
        public boolean isElapsedTimeEnabled = false;
        public boolean isModifyEnabled = false;
        public boolean isPrivateVisible = false;
        public boolean isPrivateCase = false;
        public En_TextMarkup textMarkup = En_TextMarkup.MARKDOWN;
    }

    /**
     * Провалидировать комментарий
     */
    public static class ValidateComment {

        public ValidateComment(Consumer<Boolean> onValidate) {
            this.onValidate = onValidate;
        }

        public void validate(boolean isValid) {
            onValidate.accept(isValid);
        }

        private Consumer<Boolean> onValidate;
    }

    public static class GetCurrentComment {

        public GetCurrentComment(Consumer<CaseComment> consumer) {
            this.consumer = consumer;
        }

        public void provide(CaseComment caseComment) {
            consumer.accept(caseComment);
        }

        private Consumer<CaseComment> consumer;
    }

    public static class OnSavingEvent {
        public OnSavingEvent() {}
    }

    public static class OnDoneEvent {

        public OnDoneEvent() {
            this.caseComment = null;
        }

        public OnDoneEvent(CaseComment caseComment) {
            this.caseComment = caseComment;
        }

        public CaseComment caseComment;
    }
}
