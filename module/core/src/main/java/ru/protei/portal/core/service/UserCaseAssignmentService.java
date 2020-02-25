package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserCaseAssignment;
import ru.protei.portal.core.model.struct.UserCaseAssignmentTable;

public interface UserCaseAssignmentService {

    @Privileged(En_Privilege.ISSUE_ASSIGNMENT_VIEW)
    Result<UserCaseAssignmentTable> saveTableEntity(AuthToken token, UserCaseAssignment userCaseAssignment);

    @Privileged(En_Privilege.ISSUE_ASSIGNMENT_VIEW)
    Result<UserCaseAssignmentTable> removeTableEntity(AuthToken token, Long id);

    @Privileged(En_Privilege.ISSUE_ASSIGNMENT_VIEW)
    Result<UserCaseAssignmentTable> getCaseAssignmentTable(AuthToken token);
}
