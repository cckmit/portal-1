package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;

import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public interface CaseObjectDAO extends PortalBaseDAO<CaseObject> {
    public Map<Long,Long> getNumberToIdMap (En_CaseType caseType);
}
