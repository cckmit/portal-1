package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseNotifierDAO;
import ru.protei.portal.core.model.ent.CaseNotifier;

import java.util.Collection;
import java.util.Collections;

/**
 * Реализация DAO для подписчиков кейса
 */
public class CaseNotifierDAO_Impl extends PortalBaseJdbcDAO<CaseNotifier> implements CaseNotifierDAO{

    @Override
    public Collection<CaseNotifier> getByCaseId(Long caseId) {
        Collection<CaseNotifier> result = getListByCondition("case_id = ?", caseId);
        return result == null? Collections.emptyList(): result;
    }

}
