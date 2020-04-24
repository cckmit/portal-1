package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.UserCaseAssignment;
import ru.protei.portal.core.model.view.CaseShortView;

import java.io.Serializable;
import java.util.List;

public class UserCaseAssignmentTable implements Serializable {

    private List<UserCaseAssignment> userCaseAssignments;
    private List<CaseShortView> caseShortViews;

    public UserCaseAssignmentTable() {}

    public UserCaseAssignmentTable(List<UserCaseAssignment> userCaseAssignments, List<CaseShortView> caseShortViews) {
        this.userCaseAssignments = userCaseAssignments;
        this.caseShortViews = caseShortViews;
    }

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

    @Override
    public String toString() {
        return "UserCaseAssignmentTable{" +
                "userCaseAssignments=" + userCaseAssignments +
                ", caseShortViews=" + caseShortViews +
                '}';
    }
}
