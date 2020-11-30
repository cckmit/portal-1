package ru.protei.portal.core.model.dict;

import java.util.Objects;

public enum En_ExtAppType {
    REDMINE("redmine"),
    JIRA("jira"),
    ;

    private String code;

    En_ExtAppType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static En_ExtAppType forCode(String code) {
        for (En_ExtAppType item : values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}
