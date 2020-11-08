package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public enum En_BundleType implements HasId {

    LINKED_WITH(1),
    PARENT_FOR(2),
    SUBTASK(3);

    private final int id;
    private static Map<En_CaseLink, List<En_BundleType>> mapTypes;

    static {
        mapTypes = new HashMap<>();
        mapTypes.put(En_CaseLink.CRM, listOf(LINKED_WITH, PARENT_FOR, SUBTASK));
        mapTypes.put(En_CaseLink.YT, listOf(LINKED_WITH));
    }

    En_BundleType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public static Map<En_CaseLink, List<En_BundleType>> getMapTypes() {
        return mapTypes;
    }
}
