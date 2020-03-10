package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.UserCaseAssignmentDAO;
import ru.protei.portal.core.model.ent.UserCaseAssignment;

import java.util.List;

public class UserCaseAssignmentDAO_Impl extends PortalBaseJdbcDAO<UserCaseAssignment> implements UserCaseAssignmentDAO {

    @Override
    public List<UserCaseAssignment> findByLoginId(Long loginId) {
        return getListByCondition("user_case_assignment.login_id = ?", loginId);
    }
}
