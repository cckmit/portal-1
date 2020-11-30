package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CaseCommentQuery extends BaseQuery {

    private Date createdBefore;
    private Boolean caseStateNotNull;
    private List<Long> caseObjectIds;
    private Long caseNumber;
    private List<Long> authorIds;
    private Boolean viewPrivate = null;
    private String remoteId;
    private List<CommentType> commentTypes;

    public CaseCommentQuery() {
        this(null, null, En_SortField.creation_date, En_SortDir.ASC);
    }

    public CaseCommentQuery(Long id) {
        this(id, null, En_SortField.creation_date, En_SortDir.ASC);
    }

    public CaseCommentQuery(Long id, Date createdBefore) {
        this(id, null, En_SortField.creation_date, En_SortDir.ASC);
        setCreatedBefore(createdBefore);
    }

    public CaseCommentQuery(Long id, String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        addCaseObjectId(id);
    }

    public Date getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(Date createdBefore) {
        this.createdBefore = createdBefore;
    }

    public void setCaseStateNotNull(Boolean caseStateNotNull) {
        this.caseStateNotNull = caseStateNotNull;
    }

    public Boolean isCaseStateNotNull() {
        return caseStateNotNull;
    }

    public List<Long> getCaseObjectIds() {
        return caseObjectIds;
    }

    public void setCaseObjectIds(List<Long> caseObjectIds) {
        this.caseObjectIds = caseObjectIds;
    }

    public void addCaseObjectId(Long caseObjectId) {
        if (caseObjectId == null) {
            return;
        }
        if (caseObjectIds == null) {
            caseObjectIds = new ArrayList<>();
        }
        caseObjectIds.add(caseObjectId);
    }

    public List<Long> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<Long> authorIds) {
        this.authorIds = authorIds;
    }

    public void addAuthorId(Long authorId) {
        if (authorId == null) {
            return;
        }
        if (authorIds == null) {
            authorIds = new ArrayList<>();
        }
        authorIds.add(authorId);
    }

    public Boolean isViewPrivate() {
        return viewPrivate;
    }

    public void setViewPrivate(Boolean viewOnlyPrivate) {
        this.viewPrivate = viewOnlyPrivate;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(Long caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public void addCommentType(CommentType commentType) {
        if (commentTypes == null) {
            commentTypes = new ArrayList<>();
        }

        commentTypes.add(commentType);
    }

    public List<CommentType> getCommentTypes() {
        return commentTypes;
    }

    public enum CommentType {
        CASE_STATE, IMPORTANCE, TIME_ELAPSED, TEXT
    }
}
