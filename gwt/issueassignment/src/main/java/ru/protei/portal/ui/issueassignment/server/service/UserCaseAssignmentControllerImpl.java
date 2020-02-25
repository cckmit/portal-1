package ru.protei.portal.ui.issueassignment.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserCaseAssignment;
import ru.protei.portal.core.model.struct.UserCaseAssignmentTable;
import ru.protei.portal.core.service.UserCaseAssignmentService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.UserCaseAssignmentController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

@Service("UserCaseAssignmentController")
public class UserCaseAssignmentControllerImpl implements UserCaseAssignmentController {

    @Override
    public UserCaseAssignmentTable saveTableEntity(UserCaseAssignment userCaseAssignment) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(userCaseAssignmentService.saveTableEntity(token, userCaseAssignment));
    }

    @Override
    public UserCaseAssignmentTable removeTableEntity(Long id) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(userCaseAssignmentService.removeTableEntity(token, id));
    }

    @Override
    public UserCaseAssignmentTable getCaseAssignmentTable() throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(userCaseAssignmentService.getCaseAssignmentTable(token));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    UserCaseAssignmentService userCaseAssignmentService;
}
