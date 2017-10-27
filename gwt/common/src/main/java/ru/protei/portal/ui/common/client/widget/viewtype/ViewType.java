package ru.protei.portal.ui.common.client.widget.viewtype;

/**
 * Типы представлений списков
 */
public enum ViewType {
    LIST("fa fa-list-ul"),
    TABLE("fa fa-table");

    private String icon;

    ViewType(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
