package ru.protei.portal.test.client;

public class DebugIds {

    // better to not change this prefix, some libraries (winter.web table for example) hardcoded its value
    public static final String DEBUG_ID_PREFIX = "gwt-debug-";

    public interface AUTH {
        String INPUT_LOGIN = "auth-input-login";
        String INPUT_PASSWORD = "auth-input-password";
        String LOGIN_BUTTON = "auth-login-button";
    }

    public interface APP_VIEW {
        String LOGOUT_BUTTON = "app-logout-button";
        String LOCALE_SELECTOR = "app-locale-selector";
        String TOGGLE_SIDEBAR_BUTTON = "app-toggle-sidebar-button";
        String USER_PANEL = "app-user-panel";
    }

    public interface ACTION_BAR {
        String CREATE_BUTTON = "action-bar-create-button";
    }

    public interface SIDEBAR_MENU {
        String ACCOUNT = "sidebar-menu-account";
        String CASE_STATE = "sidebar-menu-case-state";
        String COMPANY = "sidebar-menu-company";
        String CONTACT = "sidebar-menu-contact";
        String DASHBOARD = "sidebar-menu-dashboard";
        String DOCUMENT = "sidebar-menu-document";
        String DOCUMENT_TYPE = "sidebar-menu-document-type";
        String EQUIPMENT = "sidebar-menu-equipment";
        String ISSUE = "sidebar-menu-issue";
        String ISSUE_REPORTS = "sidebar-menu-issue-reports";
        String OFFICIAL = "sidebar-menu-official";
        String PRODUCT = "sidebar-menu-product";
        String PROJECT = "sidebar-menu-project";
        String REGION = "sidebar-menu-region";
        String ROLE = "sidebar-menu-role";
        String SITE_FOLDER = "sidebar-menu-site-folder";
        String SITE_FOLDER_PLATFORMS = "sidebar-menu-site-folder-platforms";
        String SITE_FOLDER_SERVERS = "sidebar-menu-site-folder-servers";
        String SITE_FOLDER_APPS = "sidebar-menu-site-folder-apps";
    }

    public interface DASHBOARD {
        String TABLE_ACTIVE = "dashboard-table-active";
        String TABLE_NEW = "dashboard-table-new";
        String TABLE_INACTIVE = "dashboard-table-inactive";
    }

    public interface FILTER {
        String COLLAPSE_BUTTON = "filter-collapse-button";
        String RESTORE_BUTTON = "filter-restore-button";

        String SEARCH_INPUT = "filter-search-input";
        String SEARCH_CLEAR_BUTTON = "filter-search-clear-button";
        String SEARCH_BY_COMMENTS_TOGGLE = "filter-search-by-comments-toggle";
        String SORT_FIELD_SELECTOR = "filter-sort-field-selector";
        String SORT_DIR_BUTTON = "filter-sort-dir-button";
        String COMPANY_GROUP_SELECTOR = "filter-company-group-selector";
        String DATE_RANGE_SELECTOR = "filter-date-range-selector";
        String COMPANY_SELECTOR_ADD_BUTTON = "filter-company-selector-add-button";
        String COMPANY_SELECTOR_CLEAR_BUTTON = "filter-company-selector-clear-button";
        String PRODUCT_SELECTOR_ADD_BUTTON = "filter-product-selector-add-button";
        String PRODUCT_SELECTOR_CLEAR_BUTTON = "filter-product-selector-clear-button";
        String MANAGER_SELECTOR_ADD_BUTTON = "filter-manager-selector-add-button";
        String MANAGER_SELECTOR_CLEAR_BUTTON = "filter-manager-selector-clear-button";
        String PRIVACY_YES_BUTTON = "filter-privacy-yes-button";
        String PRIVACY_NO_BUTTON = "filter-privacy-no-button";
        String PRIVACY_NOT_DEFINED_BUTTON = "filter-privacy-not-defined-button";

        String REPORT_BUTTON = "filter-report-button";
        String SAVE_BUTTON = "filter-save-button";
        String RESET_BUTTON = "filter-reset-button";
        String REMOVE_BUTTON = "filter-remove-button";

        interface USER_FILTER {
            String FILTERS_BUTTON = "filter-user-filters-button";
            String FILTER_NAME_INPUT = "filter-user-filter-name-input";
            String FILTER_OK_BUTTON = "filter-user-filter-ok-button";
            String FILTER_CANCEL_BUTTON = "filter-user-filter-cancel-button";
        }
    }

    public interface COMPANY_CATEGORY_BUTTON {
        String DEFAULT = "company-category-button-";
        String CUSTOMER = "company-category-button-customer";
        String PARTNER = "company-category-button-partner";
        String SUBCONTRACTOR = "company-category-button-subcontractor";
        String OFFICIAL = "company-category-button-official";
        String HOME_COMPANY = "company-category-button-home-company";
    }

    public interface IMPORTANCE_BUTTON {
        String DEFAULT = "importance-button-";
        String CRITICAL = "importance-button-critical";
        String IMPORTANT = "importance-button-important";
        String BASIC = "importance-button-basic";
        String COSMETIC = "importance-button-cosmetic";
    }

    public interface ISSUE_STATE {
        String DEFAULT = "issue-state-";
        String CREATED = "issue-state-created";
        String OPENED = "issue-state-opened";
        String CLOSED = "issue-state-closed";
        String PAUSED = "issue-state-paused";
        String VERIFIED = "issue-state-verified";
        String REOPENED = "issue-state-reopened";
        String SOLVED_NOAP = "issue-state-solved-noap";
        String SOLVED_FIX = "issue-state-solved-fix";
        String SOLVED_DUP = "issue-state-solved-dup";
        String IGNORED = "issue-state-ignored";
        String ASSIGNED = "issue-state-assigned";
        String ESTIMATED = "issue-state-estimated";
        String DISCUSS = "issue-state-discuss";
        String PLANNED = "issue-state-planned";
        String ACTIVE = "issue-state-active";
        String DONE = "issue-state-done";
        String TEST = "issue-state-test";
        String TEST_LOCAL = "issue-state-test-local";
        String TEST_CUST = "issue-state-test-customer";
        String DESIGN = "issue-state-design";
        String WORKAROUND = "issue-state-workaround";
        String INFO_REQUEST = "issue-state-info-request";
        String CUST_PENDING = "issue-state-cust-pending";
    }

    public interface ISSUE {
        String PRIVACY_BUTTON = "issue-privacy-button";
        String NUMBER_INPUT = "issue-number-input";
        String NAME_INPUT = "issue-name-input";
        String LINKS_BUTTON = "issue-links-button";
        String STATE_SELECTOR = "issue-state-selector";
        String IMPORTANCE_SELECTOR = "issue-importance-selector";
        String COMPANY_SELECTOR = "issue-company-selector";
        String INITIATOR_SELECTOR = "issue-initiator-selector";
        String PRODUCT_SELECTOR = "issue-product-selector";
        String MANAGER_SELECTOR = "issue-manager-selector";
        String TIME_ELAPSED_LABEL = "issue-time-elapsed-label";
        String DESCRIPTION_INPUT = "issue-description-input";
        String NOTIFIERS_SELECTOR_ADD_BUTTON = "issue-notifiers-selector-add-button";
        String NOTIFIERS_SELECTOR_CLEAR_BUTTON = "issue-notifiers-selector-clear-button";
        String ATTACHMENT_UPLOAD_BUTTON = "issue-attachment-upload-button";
        String SAVE_BUTTON = "issue-save-button";
        String CANCEL_BUTTON = "issue-cancel-button";
    }
}