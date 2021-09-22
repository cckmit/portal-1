package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseMemberDAO;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.ent.CaseMember;

import java.util.List;

/**
 * DAO для членов команды проекта
 */
public class CaseMemberDAO_Impl extends PortalBaseJdbcDAO<CaseMember> implements CaseMemberDAO {

    @Override
    public List<CaseMember> listByCaseId(Long caseId) {
        return getListByCondition("case_member.CASE_ID = ?", caseId);
    }

    @Override
    public List<CaseMember> getLeaders(Long caseId) {
        return getListByCondition(
                "case_member.CASE_ID = ? AND case_member.MEMBER_ROLE_ID = ?",
                caseId, En_PersonRoleType.HEAD_MANAGER.getId()
        );
    }
}
