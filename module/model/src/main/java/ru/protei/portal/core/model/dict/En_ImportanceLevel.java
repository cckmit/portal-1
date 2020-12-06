package ru.protei.portal.core.model.dict;

import ru.protei.portal.core.model.util.CrmConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 17.05.16.
 */
public enum En_ImportanceLevel  {
    CRITICAL(1, "critical"),
    IMPORTANT (2, "important"),
    BASIC (3, "basic"),
    COSMETIC (4, "cosmetic"),
    MEDIUM (5, "medium"),
    EMERGENCY (6, "emergency");

    private final int id;
    private final String code;

    En_ImportanceLevel (int id, String code) {
        this.id = id;
        this.code = code;
    }

    public static En_ImportanceLevel getById(Integer id) {
        if(id == null)
            return null;

        for (En_ImportanceLevel imp : En_ImportanceLevel.values())
            if (imp.id == id)
                return imp;

        return null;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public static En_ImportanceLevel find (int id) {
        for (En_ImportanceLevel il : En_ImportanceLevel.values())
            if (il.id == id)
                return il;

        return null;
    }

    public static En_ImportanceLevel[] values(boolean onlyCommon){
        if (!onlyCommon){
            return En_ImportanceLevel.values();
        } else {
            List<En_ImportanceLevel> commonValues = new ArrayList<>();
            for (En_ImportanceLevel level : En_ImportanceLevel.values()) {
                if (CrmConstants.ImportanceLevel.commonImportanceLevelIds.contains(level.getId())){
                    commonValues.add(level);
                }
            }
            En_ImportanceLevel[] result = new En_ImportanceLevel[commonValues.size()];
            return commonValues.toArray(result);
        }
    }
}
