package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CommentsAndHistories implements Serializable {
    private List<CommentOrHistory> sortedCommentOrHistoryList;

    private Map<Long, CaseComment> idToComment;
    private Map<Long, History> idToHistory;

    public static class CommentOrHistory implements Serializable {
        private Long id;
        private En_CommentOrHistoryType type;

        public CommentOrHistory() {}

        public CommentOrHistory(Long id, En_CommentOrHistoryType type) {
            this.id = id;
            this.type = type;
        }

        public CommentOrHistory(CaseComment caseComment) {
            this.id = caseComment.getId();
            this.type = En_CommentOrHistoryType.COMMENT;
        }

        public CommentOrHistory(History history) {
            this.id = history.getId();
            this.type = En_CommentOrHistoryType.HISTORY;
        }

        public Long getId() {
            return id;
        }

        public En_CommentOrHistoryType getType() {
            return type;
        }
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
