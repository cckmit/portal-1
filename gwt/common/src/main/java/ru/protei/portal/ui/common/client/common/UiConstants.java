package ru.protei.portal.ui.common.client.common;

/**
 * Системные константы приложения
 */
public class UiConstants {
    /**
     * Иконки вкладок приложения
     */
    public static interface TabIcons {
        public static final String COMPANY = "icon icon-organization";
        public static final String CONTACT = "fa fa-group";
        public static final String PRODUCT = "icon icon-product";
        public static final String ISSUE = "fa fa-tasks";
        public static final String DASHBOARD = "fa fa-dashboard";
        public static final String EQUIPMENT = "fa fa-server";
    }

    public static interface ActionBarIcons {
        public static final String CREATE = "fa fa-plus";
    }

    public static interface ActionBarIdentity {
        public static final String CONTACT = "contact";
        public static final String ISSUE = "issue";
        public static final String COMPANY = "company";
        public static final String PRODUCT = "product";
        public static final String DASHBOARD = "dashboard";
        public static final String EQUIPMENT = "equipment";
    }

    /**
     * from {@link ru.protei.portal.ui.common.client.events.DashboardEvents}
     */
    public static final String INITIAL_PAGE = "dashboard";

    /**
     * from {@link ru.protei.portal.ui.common.client.events.AuthEvents}
     */
    public static final String LOGIN_PAGE = "login";
}
