package ru.protei.portal.test.client;

public class DebugIds {

    // better to not change this prefix, some libraries (brainworm table for example) hardcoded its value
    public static final String DEBUG_ID_PREFIX = "gwt-debug-";

    public interface AUTH {
        String INPUT_LOGIN = "auth-input-login";
        String INPUT_PASSWORD = "auth-input-password";
        String LOGIN_BUTTON = "auth-login-button";
    }

    public interface APP_VIEW {
        String GLOBAL_CONTAINER = "global-container";
        String LOGOUT_BUTTON = "app-logout-button";
        @Deprecated String LOCALE_SELECTOR = "app-locale-selector";
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
        String EMPLOYEE_REGISTRATION = "sidebar-menu-employee-registration";
        String CONTRACT = "sidebar-menu-contract";
        @Deprecated String SITE_FOLDER_PLATFORMS = "sidebar-menu-site-folder-platforms";
        @Deprecated String SITE_FOLDER_SERVERS = "sidebar-menu-site-folder-servers";
        @Deprecated String SITE_FOLDER_APPS = "sidebar-menu-site-folder-apps";
        String ICON_SUFFIX = "-icon";
    }

    public interface SELECTOR_POPUP {
        String ADD_NEW_ENTRY_BUTTON = "selector-popup-add-new-entry-button";
        String SEARCH_INPUT = "selector-popup-search-input";
        String SEARCH_ACTION = "selector-popup-search-action";
        String ENTRY_LIST_CONTAINER = "selector-popup-entry-list-container";
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
        @Deprecated String COMPANY_GROUP_SELECTOR = "filter-company-group-selector";
        String DATE_RANGE_SELECTOR = "filter-date-range-selector";
        String COMPANY_SELECTOR_ADD_BUTTON = "filter-company-selector-add-button";
        String COMPANY_SELECTOR_CLEAR_BUTTON = "filter-company-selector-clear-button";
        String PRODUCT_SELECTOR_ADD_BUTTON = "filter-product-selector-add-button";
        String PRODUCT_SELECTOR_CLEAR_BUTTON = "filter-product-selector-clear-button";
        String MANAGER_SELECTOR_ADD_BUTTON = "filter-manager-selector-add-button";
        String MANAGER_SELECTOR_CLEAR_BUTTON = "filter-manager-selector-clear-button";
        String INITIATORS_SELECTOR_ADD_BUTTON = "filter-initiators-selector-add-button";
        String INITIATORS_SELECTOR_CLEAR_BUTTON = "filter-initiators-selector-clear-button";
        String PRIVACY_YES_BUTTON = "filter-privacy-yes-button";
        String PRIVACY_NO_BUTTON = "filter-privacy-no-button";
        String PRIVACY_NOT_DEFINED_BUTTON = "filter-privacy-not-defined-button";

        @Deprecated String REPORT_BUTTON = "filter-report-button";
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
        String CANCELED = "issue-state-canceled";
        String CUST_PENDING = "issue-state-cust-pending";
    }

    public interface ISSUE {
        String PRIVACY_BUTTON = "issue-privacy-button";
        String NUMBER_INPUT = "issue-number-input";
        String NAME_INPUT = "issue-name-input";
        String LINKS_BUTTON = "issue-links-button";
        String LINKS_CONTAINER = "issue-links-container";
        String LINKS_TYPE_SELECTOR = "issue-links-type-selector";
        String LINKS_INPUT = "issue-links-input";
        String LINKS_APPLY_BUTTON = "issue-links-apply-button";
        @Deprecated String LINKS_ERROR_LABEL = "issue-links-error-label";
        String STATE_SELECTOR = "issue-state-selector";
        String IMPORTANCE_SELECTOR = "issue-importance-selector";
        String COMPANY_SELECTOR = "issue-company-selector";
        String INITIATOR_SELECTOR = "issue-initiator-selector";
        String PRODUCT_SELECTOR = "issue-product-selector";
        String MANAGER_SELECTOR = "issue-manager-selector";
        String TIME_ELAPSED_LABEL = "issue-time-elapsed-label";
        String TIME_ELAPSED_INPUT = "issue-time-elapsed-input";
        String DESCRIPTION_INPUT = "issue-description-input";
        String NOTIFIERS_SELECTOR_ADD_BUTTON = "issue-notifiers-selector-add-button";
        String NOTIFIERS_SELECTOR_CLEAR_BUTTON = "issue-notifiers-selector-clear-button";
        String ATTACHMENT_UPLOAD_BUTTON = "issue-attachment-upload-button";
        String ATTACHMENT_LIST_CONTAINER = "issue-attachment-list-container";
        String SAVE_BUTTON = "issue-save-button";
        String CANCEL_BUTTON = "issue-cancel-button";
        String COPY_TO_CLIPBOARD_BUTTON = "issue-copy-to-clipboard-button";

        interface LABEL {
            String NAME = "issue-label-name";
            String LINKS = "issue-label-links";
            String STATE = "issue-label-state";
            String IMPORTANCE = "issue-label-importance";
            String COMPANY = "issue-label-company";
            String CONTACT = "issue-label-contact";
            String PRODUCT = "issue-label-product";
            String MANAGER = "issue-label-manager";
            String TIME_ELAPSED = "issue-label-time-elapsed";
            String INFO = "issue-label-info";
            String SUBSCRIPTIONS = "issue-label-subscriptions";
            String NOTIFIERS = "issue-label-notifiers";
            String ATTACHMENTS = "issue-label-attachments";
        }
    }

    public interface ISSUE_PREVIEW {
        String PRIVACY_ICON = "issue-preview-privacy-icon";
        String FULL_SCREEN_BUTTON = "issue-preview-full-screen-button";
        @Deprecated String TITLE_LABEL = "issue-preview-title-label";
        String LINKS_CONTAINER = "issue-preview-links-container";
        String DATE_CREATED_LABEL = "issue-preview-date-created-label";
        String IMPORTANCE_LABEL = "issue-preview-importance-label";
        String PRODUCT_LABEL = "issue-preview-product-label";
        String STATE_LABEL = "issue-preview-state-label";
        String TIME_ELAPSED_LABEL = "issue-preview-time-elapsed-label";
        @Deprecated String COMPANY_LABEL = "issue-preview-company-label";
        String CONTACT_LABEL = "issue-preview-contact-label";
        @Deprecated String OUR_COMPANY_LABEL = "issue-preview-our-company-label";
        String MANAGER_LABEL = "issue-preview-manager-label";
        String SUBSCRIPTION_LABEL = "issue-preview-subscription-label";
        String NAME_LABEL = "issue-preview-name-label";
        String INFO_LABEL = "issue-preview-info-label";
        String ATTACHMENT_UPLOAD_BUTTON = "issue-preview-attachment-upload-button";
        String ATTACHMENT_LIST_CONTAINER = "issue-preview-attachment-list-container";
        String COPY_TO_CLIPBOARD_BUTTON = "issue-preview-copy-to-clipboard-button";

        interface COMMENT_LIST {
            String COMMENTS_LIST = "issue-preview-comment-list-comments-list";
            String USER_ICON = "issue-preview-comment-list-user-icon";
            String TEXT_INPUT = "issue-preview-comment-list-text-input";
            String PRIVACY_BUTTON = "issue-preview-comment-list-privacy-button";
            String SEND_BUTTON = "issue-preview-comment-list-send-button";
            String FILES_UPLOAD = "issue-preview-comment-list-files-upload";
            String TIME_ELAPSED = "issue-preview-comment-list-time-elapsed";
            String TIME_ELAPSED_TYPE = "issue-preview-comment-list-time-elapsed-type";
        }

        interface COMMENT_ITEM {
            String PRIVACY_ICON = "issue-preview-comment-item-privacy-icon";
            String REPLY_BUTTON = "issue-preview-comment-item-reply-button";
            String EDIT_BUTTON = "issue-preview-comment-item-edit-button";
            String REMOVE_BUTTON = "issue-preview-comment-item-remove-button";
            String OWNER = "issue-preview-comment-item-owner";
            String STATUS = "issue-preview-comment-item-status";
            String TIME_ELAPSED = "issue-preview-comment-item-elapsed-time";
            String CREATE_DATE = "issue-preview-comment-item-create-date";
        }
    }

    public interface DOCUMENT_EDIT {
        String COMMON_TAB = "document-edit-common-tab";
        String SEARCH_PROJECT_TAB = "document-edit-search-project-tab";
        String CREATE_PROJECT_TAB = "document-edit-create-project-tab";
        String CREATE_PRODUCT_TAB = "document-edit-create-product-tab";
    }
}
