package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.UserCaseAssignment;
import ru.protei.portal.core.model.struct.UserCaseAssignmentTable;

public interface UserCaseAssignmentControllerAsync {

    void saveTableEntity(UserCaseAssignment userCaseAssignment, AsyncCallback<UserCaseAssignmentTable> async);

    void removeTableEntity(Long id, AsyncCallback<UserCaseAssignmentTable> async);

    void getCaseAssignmentTable(AsyncCallback<UserCaseAssignmentTable> async);
}
