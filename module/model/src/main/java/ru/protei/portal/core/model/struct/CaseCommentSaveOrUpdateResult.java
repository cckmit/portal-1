package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;

import java.io.Serializable;
import java.util.Collection;

public class CaseCommentSaveOrUpdateResult implements Serializable {

    private CaseComment caseComment;
    private CaseComment oldCaseComment;
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;

    public CaseCommentSaveOrUpdateResult() {}

    public CaseCommentSaveOrUpdateResult(CaseComment caseComment, Collection<Attachment> addedAttachments) {
        this.caseComment = caseComment;
        this.addedAttachments = addedAttachments;
    }

    public CaseCommentSaveOrUpdateResult(CaseComment caseComment, CaseComment oldCaseComment, Collection<Attachment> addedAttachments, Collection<Attachment> removedAttachments) {
        this.caseComment = caseComment;
        this.oldCaseComment = oldCaseComment;
        this.addedAttachments = addedAttachments;
        this.removedAttachments = removedAttachments;
    }

    public CaseComment getCaseComment() {
        return caseComment;
    }

    public CaseComment getOldCaseComment() {
        return oldCaseComment;
    }

    public Collection<Attachment> getAddedAttachments() {
        return addedAttachments;
    }

    public Collection<Attachment> getRemovedAttachments() {
        return removedAttachments;
    }

    @Override
    public String toString() {
        return "CaseCommentSaveOrUpdateResult{" +
                "caseComment=" + caseComment +
                ", oldCaseComment=" + oldCaseComment +
                ", addedAttachments=" + addedAttachments +
                ", removedAttachments=" + removedAttachments +
                '}';
    }
}
