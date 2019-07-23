package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseObject;

import java.io.Serializable;

public class CaseObjectUpdateResult implements Serializable {

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
