package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.UserCaseAssignment;
import ru.protei.portal.core.model.struct.UserCaseAssignmentTable;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

@RemoteServiceRelativePath("springGwtServices/UserCaseAssignmentController")
public interface UserCaseAssignmentController extends RemoteService {

    UserCaseAssignmentTable saveTableEntity(UserCaseAssignment userCaseAssignment) throws RequestFailedException;

    UserCaseAssignmentTable removeTableEntity(Long id) throws RequestFailedException;

    UserCaseAssignmentTable getCaseAssignmentTable() throws RequestFailedException;
}
