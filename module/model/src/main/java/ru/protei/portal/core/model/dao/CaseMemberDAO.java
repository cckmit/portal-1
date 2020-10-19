package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseMember;

import java.util.List;

/**
 * DAO для членов команды проекта
 */
public interface CaseMemberDAO extends PortalBaseDAO<CaseMember> {

    List<CaseMember> listByCaseId(Long caseId);

    List<CaseMember> getLeaders(Long caseId);
}
