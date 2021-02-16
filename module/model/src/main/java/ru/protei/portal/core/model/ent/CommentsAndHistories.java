package ru.protei.portal.core.model.ent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class CommentsAndHistories implements Serializable {
    private List<CommentOrHistory> commentOrHistoryList = new ArrayList<>();

    public List<CommentOrHistory> getCommentOrHistoryList() {
        return commentOrHistoryList;
    }

    public void setComments(List<CaseComment> comments) {
        commentOrHistoryList.addAll(stream(comments)
                .map(CommentOrHistory::new)
                .collect(Collectors.toList())
        );
    }

    public void setHistories(List<History> histories) {
        commentOrHistoryList.addAll(stream(histories)
                .map(CommentOrHistory::new)
                .collect(Collectors.toList())
        );
    }

    public static class CommentOrHistory implements Serializable {
        private Date date;
        private CaseComment caseComment;
        private History history;

        public CommentOrHistory() {}

        public CommentOrHistory(CaseComment caseComment) {
            this.date = caseComment.getCreated();
            this.caseComment = caseComment;
        }

        public CommentOrHistory(History history) {
            this.date = history.getDate();
            this.history = history;
        }

        public CaseComment getCaseComment() {
            return caseComment;
        }

        public History getHistory() {
            return history;
        }

        public Date getDate() {
            return date;
        }
    }
}
