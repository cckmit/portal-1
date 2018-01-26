package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseNotifier;

import java.util.Collection;

/**
 * интерфейс DAO для подписчиков кейса
 */
public interface CaseNotifierDAO extends PortalBaseDAO<CaseNotifier>{

    Collection<CaseNotifier> getByCaseId(Long caseId);

}
