package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.YoutrackWorkDictionaryDAO;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary.Columns;

import java.util.List;

public class YoutrackWorkDictionaryDAO_Impl extends PortalBaseJdbcDAO<YoutrackWorkDictionary> implements YoutrackWorkDictionaryDAO {
    @Override
    public List<YoutrackWorkDictionary> getByType(En_YoutrackWorkType type) {
        return getListByCondition(Columns.DICTIONARY_TYPE + " = ?", type.getId());
    }

    @Override
    public boolean isNameExist(String name, Long id) {
        if (id != null) {
            return checkExistsByCondition(Columns.NAME + " = ? and "+ Columns.ID + " <> ?", name, id);
        } else {
            return checkExistsByCondition(Columns.NAME + " = ?", name);
        }
    }
}
