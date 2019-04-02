package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_CaseType;

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
    }

    /**
     * Сохранить комментарий
     */
    public static class SaveComment {

        public SaveComment(Long caseId, SaveComment.SaveCommentCompleteHandler handler) {
            this.caseId = caseId;
            this.handler = handler;
        }

        public Long caseId;
        public SaveComment.SaveCommentCompleteHandler handler;

        public interface SaveCommentCompleteHandler {
            void onSuccess();
            void onError(Throwable throwable, String message);
        }
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

    /**
     * Удалить черновик комментария из хранилища
     */
    public static class RemoveDraftFromStorage {

        public RemoveDraftFromStorage(Long caseId) {
            this.caseId = caseId;
        }

        public Long caseId;
    }
}
