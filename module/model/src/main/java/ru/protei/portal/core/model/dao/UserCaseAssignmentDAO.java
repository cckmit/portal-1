package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.UserCaseAssignment;

import java.util.List;

public interface UserCaseAssignmentDAO extends PortalBaseDAO<UserCaseAssignment> {

    List<UserCaseAssignment> findByLoginId(Long loginId);
}
