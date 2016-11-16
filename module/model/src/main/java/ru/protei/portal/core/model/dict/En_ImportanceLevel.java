package ru.protei.portal.core.model.dict;

/**
 * Created by michael on 17.05.16.
 */
public enum En_ImportanceLevel  {
    CRITICAL(1, "critical"),
    IMPORTANT (2, "important"),
    BASIC (3, "basic"),
    COSMETIC (4, "cosmetic");


    En_ImportanceLevel (int id, String code) {
        this.id = id;
        this.code = code;
    }

    private final int id;
    private final String code;


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
}
