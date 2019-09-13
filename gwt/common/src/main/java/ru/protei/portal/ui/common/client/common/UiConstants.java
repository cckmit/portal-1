package ru.protei.portal.ui.common.client.common;

import com.google.gwt.core.client.GWT;

/**
 * Системные константы приложения
 */
public class UiConstants {
    /**
     * Иконки вкладок приложения
     */
    public interface TabIcons {
        String COMPANY = "fa fa-building";
        String CONTACT = "fa fa-address-book";
        String PRODUCT = "fa fa-cubes";
        String ISSUE = "fa fa-tasks";
        String DASHBOARD = "fa fa-tachometer-alt";
        String REGION = "fa fa-globe";
        String PROJECT = "fa fa-archive";
        String EQUIPMENT = "fa fa-server";
        String ACCOUNT = "fa fa-users-cog";
        String ROLE = "fa fa-user";
        String OFFICIAL = "fa fa-user-plus";
        String DOCUMENT = "fa fa-book";
        String DOCUMENT_TYPE = "fa fa-list-alt";
        String ISSUE_REPORTS = "fa fa-file-download";
        String CASE_STATE = "fa fa-bookmark ";
        String SITE_FOLDER = "fa fa-briefcase"  ;
        String EMPLOYEE = "fa fa-user-circle";
        String EMPLOYEE_REGISTRATION = "fa fa-clipboard";
        String CONTRACT = "fa fa-file-signature";
    }
    public interface Icons {
        String APPROVED = "fa-clipboard-check";
        String NOT_APPROVED = "fa-clipboard";
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
        String ISSUE_REPORT = "issue_report";
        String SITE_FOLDER_PLATFORM = "site_folder_platform";
        String SITE_FOLDER_SERVER = "site_folder_server";
        String SITE_FOLDER_APP = "site_folder_app";
        String EMPLOYEE_REGISTRATION = "employee_registration";
        String CONTRACT = "contract";
        String EMPLOYEE_TYPE_VIEW = "employeeTypeView";
    }

    public interface Styles {
        String HIDE = "hide";
        String REQUIRED = "required";
        String TEXT_CENTER = "text-center";
        String SEARCH_NO_RESULT = "search-no-result";
        String MULTIPLE_ANY = "multiple-any";
    }

    /**
     * from {@link ru.protei.portal.ui.common.client.events.AuthEvents}
     */
    public static final String LOGIN_PAGE = "login";
    public static final String UNDEFINED_ENTRY = "undefined";
    public static final String REMEMBER_ME_PREFIX = "auth_remember_me_";
}
