package ru.protei.portal.core.model.dict;

/**
 * Created by Mike on 04.11.2016.
 */
public enum En_Gender {
    MALE("M", 1),
    FEMALE("F", 2),
    UNDEFINED("-", null);

    private String code;
    private Integer legacyId;

    En_Gender (String code, Integer legacyId) {
        this.code = code;
        this.legacyId = legacyId;
    }

    public String getCode() {
        return code;
    }

    public Integer getLegacyId() {
        return legacyId;
    }

    public static En_Gender parse (String value) {
        if (value == null || value.isEmpty())
            return UNDEFINED;

        for (En_Gender gender : En_Gender.values())
            if (gender.code.equalsIgnoreCase(value))
                return gender;

        return UNDEFINED;
    }


    public static En_Gender fromLegacyId (Integer id) {
        return id == null ? En_Gender.UNDEFINED :
                id.intValue() == 1 ? En_Gender.MALE : id.intValue() == 2 ? En_Gender.FEMALE : En_Gender.UNDEFINED;
    }
}
