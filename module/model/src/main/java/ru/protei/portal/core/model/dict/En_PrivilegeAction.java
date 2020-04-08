package ru.protei.portal.core.model.dict;

/**
 * Привилегии в системе
 */
public enum En_PrivilegeAction {

    CREATE("C", 2), EDIT("M", 3), VIEW("V", 1), REPORT("R", 5), EXPORT("E", 6), REMOVE("D", 4);

    private final String shortName;
    private final Integer order;

    En_PrivilegeAction(String shortName, Integer order) {
        this.shortName = shortName;
        this.order = order;
    }

    public String getShortName() {
        return  shortName;
    }

    public Integer getOrder() {
        return order;
    }
}
