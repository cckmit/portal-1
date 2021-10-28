package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.ui.common.client.activity.commenthistory.AbstractCommentAndHistoryListView;

import java.util.List;

public class CommentAndHistoryEvents {

    /**
     * Показать комментарии и историю
     */
    public static class Show {

        public Show() {}

        public Show(AbstractCommentAndHistoryListView view, Long caseId, En_CaseType caseType, boolean isModifyEnabled) {
            this(view, caseId, caseType, isModifyEnabled, null);
        }

        public Show(AbstractCommentAndHistoryListView view, Long caseId, En_CaseType caseType, boolean isModifyEnabled, Long caseCreatorId) {
            this.view = view;
            this.caseId = caseId;
            this.caseType = caseType;
            this.isModifyEnabled = isModifyEnabled;
            this.caseCreatorId = caseCreatorId;
        }

        public AbstractCommentAndHistoryListView view;
        public En_CaseType caseType;
        public Long caseId;
        public Long caseCreatorId;
        public Long initiatorCompanyId;
        public boolean isElapsedTimeEnabled = false;
        public boolean isModifyEnabled = false;
        public boolean isPrivateVisible = false;
        public boolean isPrivateCase = false;
        public boolean isNewCommentEnabled = true;
        public boolean isMentionEnabled = true;
        public En_TextMarkup textMarkup = En_TextMarkup.MARKDOWN;
        public boolean extendedPrivacyType = false;
        public boolean isJiraWorkflowWarningVisible = false;
    }

    /**
     * Перезагрузить список элементов
     */
    public static class Reload {
        public Reload() {}
    }

    public static class DisableNewComment {
        public DisableNewComment() {}
    }

    public static class ShowItems {
        public ShowItems(AbstractCommentAndHistoryListView view, List<En_CommentOrHistoryType> typesToShow) {
            this.view = view;
            this.typesToShow = typesToShow;
        }

        public AbstractCommentAndHistoryListView view;
        public List<En_CommentOrHistoryType> typesToShow;
    }

    public static class ShowJiraWorkflowWarning {
        public ShowJiraWorkflowWarning(boolean isJiraWorkflowWarningVisible) {
            this.isJiraWorkflowWarningVisible = isJiraWorkflowWarningVisible;
        }
        public boolean isJiraWorkflowWarningVisible;
    }
}
