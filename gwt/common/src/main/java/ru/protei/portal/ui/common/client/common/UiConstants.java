package ru.protei.portal.ui.common.client.common;

/**
 * Системные константы приложения
 */
public class UiConstants {
    /**
     * Иконки вкладок приложения
     */
    public interface TabIcons {
        String SUB_ITEM = "fa fa-chevron-right";
        String COMPANY = "icon icon-organization";
        String CONTACT = "fa fa-address-book";
        String PRODUCT = "icon icon-product";
        String ISSUE = "fa fa-tasks";
        String DASHBOARD = "fa fa-dashboard";
        String REGION = "fa fa-globe";
        String PROJECT = "fa fa-archive";
        String EQUIPMENT = "fa fa-server";
        String ACCOUNT = "fa fa-group";
        String ROLE = "fa fa-user";
        String OFFICIAL = "fa fa-book";
        String DOCUMENT = "fa fa-server";
        String DOCUMENT_TYPE = "fa fa-server";
        String ISSUE_REPORTS = "fa fa-tasks";
        String CASE_STATE = "fa fa-bookmark ";
        String SITE_FOLDER = "fa fa-folder";
        String EMPLOYEE = "fa fa-user-circle";
    }

    public interface ActionBarIcons {
        String CREATE = "fa fa-plus";
        String LIST = "fa fa-list-ul";
        String TABLE = "fa fa-table";
        String IMPORT = "fa fa-upload";
    }

    public interface ActionBarIdentity {
        String CONTACT = "contact";
        String ISSUE = "issue";
        String COMPANY = "company";
        String COMPANY_TYPE_VIEW = "companyTypeView";
        String PRODUCT = "product";
        String PRODUCT_TYPE_VIEW = "productTypeView";
        String DASHBOARD = "dashboard";
        String EQUIPMENT = "equipment";
        String PROJECT = "project";
        String ACCOUNT = "account";
        String ROLE = "role";
        String OFFICIAL = "official";
        String DOCUMENT = "document";
        String DOCUMENT_TYPE = "document_type";
        String ISSUE_REPORTS = "issue_reports";
        String SITE_FOLDER_PLATFORM = "site_folder_platform";
        String SITE_FOLDER_SERVER = "site_folder_server";
        String SITE_FOLDER_APP = "site_folder_app";
    }

    public interface UserIcon {
        String MALE = "./images/user-icon-m.svg";
        String FEMALE = "./images/user-icon-f.svg";
    }

    public interface Styles {
        String FOOTER = "footer-fixed";
        String HIDE = "hide";
    }

    /**
     * from {@link ru.protei.portal.ui.common.client.events.AuthEvents}
     */
    public static final String LOGIN_PAGE = "login";
    public static final String UNDEFINED_ENTRY = "undefined";
}
