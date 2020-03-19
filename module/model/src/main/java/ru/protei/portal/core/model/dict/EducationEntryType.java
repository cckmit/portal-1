package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum EducationEntryType implements HasId {
    COURSE(1),
    CONFERENCE(2),
    LITERATURE(3),
    ;

    EducationEntryType(int id) {
        this.id = id;
    }

    private final int id;
    public int getId() { return id; }

    public static EducationEntryType byId(int id) {
        for (EducationEntryType type : EducationEntryType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
}
