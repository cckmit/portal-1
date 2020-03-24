package ru.protei.portal.core.model.dict;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 17.05.16.
 */
public enum En_ImportanceLevel  {
    CRITICAL(1, "critical", true),
    IMPORTANT (2, "important", true),
    BASIC (3, "basic",true),
    COSMETIC (4, "cosmetic",true),
    MEDIUM (5, "medium",false);


    En_ImportanceLevel (int id, String code, boolean isCommon) {
        this.id = id;
        this.code = code;
        this.isCommon = isCommon;
    }

    private final int id;
    private final String code;
    private final boolean isCommon;


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
            for (En_ImportanceLevel value : En_ImportanceLevel.values()) {
                if (value.isCommon){
                    commonValues.add(value);
                }
            }
            En_ImportanceLevel[] result = new En_ImportanceLevel[commonValues.size()];
            return commonValues.toArray(result);
        }
    }
}
