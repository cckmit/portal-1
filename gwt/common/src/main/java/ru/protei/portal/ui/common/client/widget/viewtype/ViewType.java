package ru.protei.portal.ui.common.client.widget.viewtype;

public enum ViewType {
    LIST("fa fa-list-ul"),
    TABLE("fa fa-table"),
    CALENDAR("fa fa-calendar"),
    ;

    private String icon;

    ViewType(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
