package ru.protei.portal.ui.common.client.common;

/**
 * Статусы для проверки наличия объекта
 */
public enum NameStatus {

    SUCCESS ("fas fa-check-circle text-success"),
    ERROR ("fas fa-exclamation-circle text-danger"),
    UNDEFINED ("fas fa-spinner fa-spin"),
    NONE ("");

    private final String style;
    NameStatus (String type) { this.style = type; }
    public String getStyle() { return style; }

}
