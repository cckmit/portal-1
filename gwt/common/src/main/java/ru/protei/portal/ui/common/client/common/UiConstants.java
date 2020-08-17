package ru.protei.portal.ui.common.client.common;

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
        String ISSUE_ASSIGNMENT = "fas fa-table";
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
        String EDUCATION = "fas fa-graduation-cap";
        String IP_RESERVATION = "fa fa-sitemap";
        String ROOM_RESERVATION = "fas fa-calendar-day";
        String PLAN = "fa fa-list-ol";
    }
    public interface Icons {
        String APPROVED = "fa-clipboard-check";
        String NOT_APPROVED = "fa-clipboard";

        String REMOVE = "fa-trash-alt";
        String DOWNLOAD = "fa-cloud-download-alt";
        String REFRESH = "fa-redo";
        String CANCEL = "fa-window-close";
        String FAVORITE_ACTIVE = "fas fa-star";
        String FAVORITE_NOT_ACTIVE = "far fa-star";

        String BIG_ICON = "fa-lg";
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
        String DASHBOARD_CREATE_TABLE = "dashboard_create_table";
        String DASHBOARD_CREATE_ISSUE = "dashboard_create_issue";
        String ISSUE_ASSIGNMENT_CREATE_ISSUE = "issue_assignment_create_issue";
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
        String TOP_BRASS = "top_brass";
        String EMPLOYEE_VIEW = "employee_view";
        String EMPLOYEE_CREATE = "employee_create";
        String SUBNET = "subnet";
        String SUBNET_CREATE = "subnet_create";
        String RESERVED_IP = "reserved_ip";
        String RESERVED_IP_CREATE = "reserved_ip_create";
        String PLAN_CREATE = "plan_create";
        String ABSENCE = "absence";
        String ABSENCE_REPORT = "absence_report";
        String EMPLOYEE_BIRTHDAYS = "employee_birthdays";
        String ABSENCE_SUMMARY_TABLE = "absence_summary_table";
        String ROOM_RESERVATION_CALENDAR = "room_reservation_calendar";
        String ROOM_RESERVATION_TABLE = "room_reservation_table";
        String ROOM_RESERVATION_CREATE = "room_reservation_create";
    }

    public interface Styles {
        String HIDE = "hide";
        String HAS_ERROR = "has-error";
        String INACTIVE = "inactive";
        String REQUIRED = "required";
        String TEXT_CENTER = "text-center";
        String SEARCH_NO_RESULT = "search-no-result";
        String MULTIPLE_ANY = "multiple-any";
        String SHORT_VIEW = "col-md-6";
        String FULL_VIEW = "col-md-12";
        String LINK_DISABLE = "link-disabled";
        String WIDE_MODAL = "modal-lg";
        String XL_MODAL = "modal-xl";
        String FAVORITES = "favorites";
        String FAVORITE_ICON = "favorite-icon";
    }

    public interface ColumnClassName {
        String REMOVE = "remove";
        String DOWNLOAD = "download";
        String REFRESH = "refresh";
        String CANCEL = "cancel";
    }

    /**
     * from {@link ru.protei.portal.ui.common.client.events.AuthEvents}
     */
    public static final String LOGIN_PAGE = "login";
    public static final String UNDEFINED_ENTRY = "undefined";
    public static final String REMEMBER_ME_PREFIX = "auth_remember_me_";
    public static final String LINKS_PANEL_VISIBILITY = "case-link-panel-body";
    public static final String ISSUE_CREATE_PREVIEW_DISPLAYED = "issue_create_is_preview_displayed";
}
