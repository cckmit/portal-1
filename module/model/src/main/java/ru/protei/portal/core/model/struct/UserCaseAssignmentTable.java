package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.UserCaseAssignment;
import ru.protei.portal.core.model.view.CaseShortView;

import java.io.Serializable;
import java.util.List;

public class UserCaseAssignmentTable implements Serializable {

    private List<UserCaseAssignment> userCaseAssignments;
    private List<CaseShortView> caseShortViews;
    private long caseShortViewsLimit;
    private boolean caseShortViewsLimitOverflow;

    public UserCaseAssignmentTable() {}

    public List<UserCaseAssignment> getUserCaseAssignments() {
        return userCaseAssignments;
    }

    public void setUserCaseAssignments(List<UserCaseAssignment> userCaseAssignments) {
        this.userCaseAssignments = userCaseAssignments;
    }

    public List<CaseShortView> getCaseShortViews() {
        return caseShortViews;
    }

    public void setCaseShortViews(List<CaseShortView> caseShortViews) {
        this.caseShortViews = caseShortViews;
    }

    public long getCaseShortViewsLimit() {
        return caseShortViewsLimit;
    }

    public void setCaseShortViewsLimit(long caseShortViewsLimit) {
        this.caseShortViewsLimit = caseShortViewsLimit;
    }

    public boolean isCaseShortViewsLimitOverflow() {
        return caseShortViewsLimitOverflow;
    }

    public void setCaseShortViewsLimitOverflow(boolean caseShortViewsLimitOverflow) {
        this.caseShortViewsLimitOverflow = caseShortViewsLimitOverflow;
    }

    @Override
    public String toString() {
        return "UserCaseAssignmentTable{" +
                "userCaseAssignments=" + userCaseAssignments +
                ", caseShortViews=" + caseShortViews +
                ", caseShortViewsLimit=" + caseShortViewsLimit +
                ", caseShortViewsLimitOverflow=" + caseShortViewsLimitOverflow +
                '}';
    }
}
