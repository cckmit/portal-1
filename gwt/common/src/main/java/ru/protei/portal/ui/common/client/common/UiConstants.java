package ru.protei.portal.ui.common.client.common;

/**
 * Системные константы приложения
 */
public class UiConstants {
    /**
     * Иконки вкладок приложения
     */
    public interface TabIcons {
        String COMPANY = "icon icon-organization";
        String CONTACT = "fa fa-group";
        String PRODUCT = "icon icon-product";
        String ISSUE = "fa fa-tasks";
        String DASHBOARD = "fa fa-dashboard";
        String REGION = "fa fa-globe";
        String PROJECT = "fa fa-archive";
        String EQUIPMENT = "fa fa-server";
        String ROLE = "fa fa-user";
    }

    public interface ActionBarIcons {
        String CREATE = "fa fa-plus";
        String IMPORT = "fa fa-upload";
    }

    public interface ActionBarIdentity {
        String CONTACT = "contact";
        String ISSUE = "issue";
        String COMPANY = "company";
        String PRODUCT = "product";
        String DASHBOARD = "dashboard";
        String EQUIPMENT = "equipment";
        String PROJECT = "project";
        String ROLE = "role";
    }

    /**
     * from {@link ru.protei.portal.ui.common.client.events.AuthEvents}
     */
    public static final String LOGIN_PAGE = "login";
}
