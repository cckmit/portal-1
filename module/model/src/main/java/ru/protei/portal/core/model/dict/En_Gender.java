package ru.protei.portal.core.model.dict;

/**
 * Created by Mike on 04.11.2016.
 */
public enum En_Gender {
    MALE("M"),
    FEMALE("F"),
    UNDEFINED("-");

    private String code;

    En_Gender (String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static En_Gender parse (String value) {
        if (value == null || value.isEmpty())
            return UNDEFINED;

        for (En_Gender gender : En_Gender.values())
            if (gender.code.equalsIgnoreCase(value))
                return gender;

        return UNDEFINED;
    }
}
