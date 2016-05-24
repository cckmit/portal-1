package ru.protei.portal.tools.migrate.tools;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 20.05.16.
 */
public class CaseIdMapper implements PostBatchProcess<CaseObject> {

    private Map<String,Long> keysMap;

    public CaseIdMapper () {
        keysMap = new HashMap<>();
    }

    @Override
    public void postBatch(CaseObject caseObject) {
        keysMap.put(caseObject.defGUID(), caseObject.getId());
    }

    public Long getRealId (En_CaseType t, long old_id) {
        return keysMap.get(t.makeGUID(old_id));
    }
}
