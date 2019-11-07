package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.util.DiffCollectionResult;

public class CaseObjectUpdateResult  {

    private CaseObject caseObject;
    private boolean isUpdated;

    public CaseObjectUpdateResult() {}

    public CaseObjectUpdateResult(CaseObject caseObject, boolean isUpdated) {
        this.caseObject = caseObject;
        this.isUpdated = isUpdated;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }


    public boolean isUpdated() {
        return isUpdated;
    }

    @Override
    public String toString() {
        return "CaseObjectUpdateResult{" +
                "caseObject=" + caseObject +
                ", isUpdated=" + isUpdated +
                '}';
    }
}
