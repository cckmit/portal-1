package ru.protei.portal.ui.common.client.service;

/**
 * Статусы для проверки наличия объекта
 */
public enum NameStatus {

    SUCCESS ("icon-success"),
    ERROR ("icon-error"),
    UNDEFINED ("icon-verifiable"),
    NONE ("");

    private final String style;
    NameStatus (String type) { this.style = type; }
    public String getStyle() { return style; }

}
