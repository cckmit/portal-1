package ru.protei.portal.redmine.enums;

import java.util.Arrays;
import java.util.Optional;

public enum RedmineChangeType {
    STATUS_CHANGE("status_id"),
    PRIORITY_CHANGE("priority_id"),
    DESCRIPTION_CHANGE("description"),
    SUBJECT_CHANGE("subject");

    private final String name;

    RedmineChangeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Optional<RedmineChangeType> findByName(String name) {
        return Arrays.stream(values()).filter(x -> x.name.equals(name)).findFirst();
    }
}
