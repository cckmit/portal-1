package ru.protei.portal.ui.common.client.common;

/**
 * Статусы для проверки наличия объекта
 */
public enum NameStatus {

    SUCCESS ("fas fa-check-circle"),
    ERROR ("fas fa-exclamation-circle"),
    UNDEFINED ("fal fa-circle-notch"),
    NONE ("");

    private final String style;
    NameStatus (String type) { this.style = type; }
    public String getStyle() { return style; }

}
