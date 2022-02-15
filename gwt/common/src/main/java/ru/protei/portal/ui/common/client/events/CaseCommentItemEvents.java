package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.FlowPanel;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemView;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CaseCommentItemEvents {
    public static class Init {
        public Init(En_CaseType caseType, Long caseId, En_TextMarkup textMarkup,
                    boolean isPrivateVisible, boolean isElapsedTimeEnabled,
                    boolean isModifyEnabled, boolean isEditEnabled, FlowPanel commentsContainer,
                    Function<CaseComment, String> makeAllowEditValidationString,
                    Function<CaseComment, String> makeAllowRemoveValidationString) {

            this.caseType = caseType;
            this.caseId = caseId;
            this.textMarkup = textMarkup;
            this.isPrivateVisible = isPrivateVisible;
            this.isElapsedTimeEnabled = isElapsedTimeEnabled;
            this.isModifyEnabled = isModifyEnabled;
            this.isEditEnabled = isEditEnabled;
            this.commentsContainer = commentsContainer;
            this.makeAllowEditValidationString = makeAllowEditValidationString;
            this.makeAllowRemoveValidationString = makeAllowRemoveValidationString;
        }

        public En_CaseType caseType;
        public Long caseId;
        public En_TextMarkup textMarkup;
        public boolean isPrivateVisible;
        public boolean isElapsedTimeEnabled;
        public boolean isModifyEnabled;
        public boolean isEditEnabled;
        public FlowPanel commentsContainer;
        public Function<CaseComment, String> makeAllowEditValidationString;
        public Function<CaseComment, String> makeAllowRemoveValidationString;
    }

    public static class Clear {}

    public static class FillComments {
        public FillComments(List<CaseComment> comments) {
            this.comments = comments;
        }

        public List<CaseComment> comments;
    }

    public static class CreateComment {
        public CreateComment(CaseComment caseComment, boolean isVisible) {
            this.caseComment = caseComment;
            this.isVisible = isVisible;
        }

        public CaseComment caseComment;
        public boolean isVisible;
    }

    public static class EditComment {
        public EditComment(CaseComment comment, AbstractCaseCommentItemView itemView, BiConsumer<CaseComment, Collection<Attachment>> resultConsumer) {
            this.comment = comment;
            this.itemView = itemView;
            this.resultConsumer = resultConsumer;
        }

        public CaseComment comment;
        public AbstractCaseCommentItemView itemView;
        public BiConsumer<CaseComment, Collection<Attachment>> resultConsumer;
    }

    /**
     * Сохранение комментария из пуш-уведомления
     */
    public static class SaveOrUpdateClientComment {
        public SaveOrUpdateClientComment(CaseComment caseComment, boolean isVisible) {
            this.caseComment = caseComment;
            this.isVisible = isVisible;
        }

        public CaseComment caseComment;
        public boolean isVisible;
    }

    /**
     * Удаление комментария из пуш-уведомления
     */
    public static class RemoveClientComment {
        public RemoveClientComment(Long commentId) {
            this.commentId = commentId;
        }

        public Long commentId;
    }

    public static class RemoveAttachment {
        public RemoveAttachment(CaseComment comment, Attachment attachment) {
            this.comment = comment;
            this.attachment = attachment;
        }

        public CaseComment comment;
        public Attachment attachment;
    }

    public static class ReplyComment {
        public ReplyComment(Long authorId) {
            this.authorId = authorId;
        }

        public Long authorId;
    }

    public static class Show {}

    public static class Hide {}
}
