package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemView;
import ru.protei.portal.ui.common.client.view.casecomment.item.CaseCommentItemView;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CommentsAndHistoryEvents {
    public static class Init {
        public Init(En_CaseType caseType, Long caseId, En_TextMarkup textMarkup,
                    boolean isPrivateVisible, boolean isElapsedTimeEnabled, boolean isModifyEnabled,
                    Function<CaseComment, String> makeAllowEditValidationString,
                    Function<CaseComment, String> makeAllowRemoveValidationString) {

            this.caseType = caseType;
            this.caseId = caseId;
            this.textMarkup = textMarkup;
            this.isPrivateVisible = isPrivateVisible;
            this.isElapsedTimeEnabled = isElapsedTimeEnabled;
            this.isModifyEnabled = isModifyEnabled;
            this.makeAllowEditValidationString = makeAllowEditValidationString;
            this.makeAllowRemoveValidationString = makeAllowRemoveValidationString;
        }

        public En_CaseType caseType;
        public Long caseId;
        public En_TextMarkup textMarkup;
        public boolean isPrivateVisible;
        public boolean isElapsedTimeEnabled;
        public boolean isModifyEnabled;
        public Function<CaseComment, String> makeAllowEditValidationString;
        public Function<CaseComment, String> makeAllowRemoveValidationString;
    }

    public static class FillComments {
        public FillComments(FlowPanel commentsContainer, List<CaseComment> comments) {
            this.commentsContainer = commentsContainer;
            this.comments = comments;
        }

        public FlowPanel commentsContainer;
        public List<CaseComment> comments;
    }

    public static class CreateComment {
        public CreateComment(CaseComment caseComment, Consumer<AbstractCaseCommentItemView> itemViewConsumer) {
            this.caseComment = caseComment;
            this.itemViewConsumer = itemViewConsumer;
        }

        public CaseComment caseComment;
        public Consumer<AbstractCaseCommentItemView> itemViewConsumer;
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

    public static class SaveOrUpdateClientComment {
        public SaveOrUpdateClientComment(CaseComment caseComment) {
            this.caseComment = caseComment;
        }

        public CaseComment caseComment;
    }

    public static class RemoveComment {}

    public static class RemoveClientComment {
        public RemoveClientComment(Long commentId) {
            this.commentId = commentId;
        }

        public Long commentId;
    }

    public static class RemoveAttachment {
        public RemoveAttachment(CaseCommentItemView itemView, CaseComment comment, Attachment attachment) {
            this.itemView = itemView;
            this.comment = comment;
            this.attachment = attachment;
        }

        public CaseCommentItemView itemView;
        public CaseComment comment;
        public Attachment attachment;
    }

    public static class ReplyComment {
        public ReplyComment(Long authorId) {
            this.authorId = authorId;
        }

        public Long authorId;
    }

    public static class FillHistories {
        public FillHistories(List<History> histories, InsertPanel.ForIsWidget container) {
            this.histories = histories;
            this.container = container;
        }

        private final List<History> histories;
        private final InsertPanel.ForIsWidget container;
    }
}
