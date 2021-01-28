package ru.protei.portal.core.model.ent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CommentsAndHistories implements Serializable {
    private List<CommentOrHistory> sortedCommentOrHistoryList;

    private Map<Long, CaseComment> idToComment;
    private Map<Long, History> idToHistory;

    public static class CommentOrHistory implements Serializable {
        private Long id;
        private Type type;

        public CommentOrHistory() {}

        public CommentOrHistory(Long id, Type type) {
            this.id = id;
            this.type = type;
        }

        public CommentOrHistory(CaseComment caseComment) {
            this.id = caseComment.getId();
            this.type = Type.COMMENT;
        }

        public CommentOrHistory(History history) {
            this.id = history.getId();
            this.type = Type.HISTORY;
        }
    }

    public enum Type {
        COMMENT, HISTORY
    }

    public List<CommentOrHistory> getSortedCommentOrHistoryList() {
        return sortedCommentOrHistoryList;
    }

    public void setSortedCommentOrHistoryList(List<CommentOrHistory> sortedCommentOrHistoryList) {
        this.sortedCommentOrHistoryList = sortedCommentOrHistoryList;
    }

    public CaseComment getComment(Long id) {
        return idToComment.get(id);
    }

    public History getHistory(Long id) {
        return idToHistory.get(id);
    }

    public void setIdToComment(Map<Long, CaseComment> idToComment) {
        this.idToComment = idToComment;
    }

    public void setIdToHistory(Map<Long, History> idToHistory) {
        this.idToHistory = idToHistory;
    }
}
