package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.winter.core.utils.collections.DiffCollectionResult;

public class CaseObjectUpdateResult  {

    private CaseObject caseObject;
    private boolean isUpdated;
    private DiffCollectionResult<CaseLink> mergeLinks;

    public CaseObjectUpdateResult( CaseObject caseObject, DiffCollectionResult<CaseLink> mergeLinks, boolean b ) {
        this.mergeLinks = mergeLinks;
    }

    public CaseObjectUpdateResult(CaseObject caseObject, boolean isUpdated) {
        this.caseObject = caseObject;
        this.isUpdated = isUpdated;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }

    public DiffCollectionResult<CaseLink> getMergeLinks() {
        return mergeLinks;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    @Override
    public String toString() {
        return "CaseObjectUpdateResult{" +
                "caseObject=" + caseObject +
                ", isUpdated=" + isUpdated +
                ", mergeLinks=" + mergeLinks +
                '}';
    }
}
