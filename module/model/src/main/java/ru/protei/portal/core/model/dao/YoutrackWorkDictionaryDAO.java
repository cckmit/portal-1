package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;

import java.util.List;

public interface YoutrackWorkDictionaryDAO extends PortalBaseDAO<YoutrackWorkDictionary> {
    List<YoutrackWorkDictionary> getByType(En_YoutrackWorkType type);

    boolean isNameExist(String name, Long id);
}