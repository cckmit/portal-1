package ru.protei.portal.test.client;

public class DebugIds {

    // better to not change this prefix, some libraries (brainworm table for example) hardcoded its value
    public static final String DEBUG_ID_PREFIX = "gwt-debug-";
    public static final String DEBUG_ID_ATTRIBUTE = "gwt-test-id";

    public interface AUTH {
        String INPUT_LOGIN = "auth-input-login";
        String INPUT_PASSWORD = "auth-input-password";
        String LOGIN_BUTTON = "auth-login-button";
        String ERROR_ALERT = "auth-error-alert";
    }

    public interface NOTIFY {
        String NOTIFY_ITEM = "notify-item";
        String NOTIFY_ICON_SUCCESS = "notify-icon-success";
        String NOTIFY_ICON_ERROR = "notify-icon-error";
        String NOTIFY_CONTENT_TITLE = "notify-content-title";
        String NOTIFY_CONTENT_MESSAGE = "notify-content-message";
    }

    public interface APP_VIEW {
        String GLOBAL_CONTAINER = "global-container";
        String LOGOUT_BUTTON = "app-logout-button";
        String LOCALE_SELECTOR = "app-locale-selector";
        String TOGGLE_SIDEBAR_BUTTON = "app-toggle-sidebar-button";
        String USER_PANEL = "app-user-panel";
        String NOTIFICATION_CONTAINER = "app-notification-container";
        String SETTING_BUTTON = "app-setting-button";
        String USER_NAME_LABEL = "app-user-name-label";
        String SIDEBAR = "app-sidebar";
        String DASHBOARD_BUTTON = "app-dashboard-button";
        String FIX_CLOSED_SIDEBAR_BUTTON = "app-fix-closed-sidebar-button";
        String FIX_OPENED_SIDEBAR_BUTTON = "app-fix-opened-sidebar-button";
    }

    public interface PROFILE {
        String NAME = "profile-name";
        String COMPANY = "profile-company";
        String CHANGE_PASSWORD_BUTTON = "profile-change-password-button";
        String CURRENT_PASSWORD_INPUT = "profile-current-password-input";
        String NEW_PASSWORD_INPUT = "profile-new-password-input";
        String CONFIRM_PASSWORD_INPUT = "profile-confirm-password-input";
        String SAVE_PASSWORD_BUTTON = "profile-save-password-button";

        interface LABEL {
            String CHANGE_PASSWORD = "profile-change-password-label";
            String CURRENT_PASSWORD = "profile-current-password-label";
            String NEW_PASSWORD = "profile-new-password-label";
            String CONFIRM_PASSWORD = "profile-confirm-password-label";
        }
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
        String EMPLOYEE = "sidebar-menu-employee";
        String CONTRACT = "sidebar-menu-contract";
        @Deprecated String SITE_FOLDER_PLATFORMS = "sidebar-menu-site-folder-platforms";
        @Deprecated String SITE_FOLDER_SERVERS = "sidebar-menu-site-folder-servers";
        @Deprecated String SITE_FOLDER_APPS = "sidebar-menu-site-folder-apps";
        String ICON_SUFFIX = "-icon";
    }

    public interface SELECTOR {

        interface POPUP {
            String ADD_NEW_ENTRY_BUTTON = "selector-popup-add-new-entry-button";
            String SEARCH_INPUT = "selector-popup-search-input";
            String SEARCH_ACTION = "selector-popup-search-action";
            String ENTRY_LIST_CONTAINER = "selector-popup-entry-list-container";
            String ITEM = "selector-popup-item";
        }

        interface SELECTED {
            String ITEM = "selector-selected-item";
            String REMOVE_BUTTON = "selector-selected-remove-button";
        }
    }

    public interface DASHBOARD {
        String TABLE_ACTIVE = "dashboard-table-active";
        String TABLE_NEW = "dashboard-table-new";
        String TABLE_INACTIVE = "dashboard-table-inactive";
    }

    public interface FILTER {
        String FILTERS_LABEL = "filter-filters-label";
        String COLLAPSE_BUTTON = "filter-collapse-button";
        String RESTORE_BUTTON = "filter-restore-button";

        String SEARCH_INPUT = "filter-search-input";
        String SEARCH_CLEAR_BUTTON = "filter-search-clear-button";
        String SEARCH_BY_COMMENTS_TOGGLE = "filter-search-by-comments-toggle";
        String SEARCH_BY_WARNING_COMMENTS_LABEL = "filter-search-by-comments-warning-label";
        String SORT_FIELD_SELECTOR = "filter-sort-field-selector";
        String SORT_FIELD_LABEL = "filter-sort-field-label";
        String SORT_DIR_BUTTON = "filter-sort-dir-button";
        @Deprecated String COMPANY_GROUP_SELECTOR = "filter-company-group-selector";
        String DATE_RANGE_SELECTOR = "filter-date-range-selector";
        String DATE_CREATED_RANGE_INPUT = "filter-date-created-range-input";
        String DATE_CREATED_RANGE_LABEL = "filter-date-created-range-label";
        String DATE_MODIFIED_RANGE_INPUT = "filter-date-modified-range-input";
        String DATE_MODIFIED_RANGE_LABEL = "filter-date-range-label";
        String DATE_CREATED_RANGE_BUTTON = "filter-date-created-range-button";
        String DATE_MODIFIED_RANGE_BUTTON = "filter-date-modified-range-button";
        String COMPANY_SELECTOR_ADD_BUTTON = "filter-company-selector-add-button";
        String COMPANY_SELECTOR_CLEAR_BUTTON = "filter-company-selector-clear-button";
        String COMPANY_SELECTOR_ITEM_CONTAINER = "filter-company-selector-item-container";
        String COMPANY_SELECTOR_LABEL = "filter-company-selector-label";
        String PRODUCT_SELECTOR_ADD_BUTTON = "filter-product-selector-add-button";
        String PRODUCT_SELECTOR_CLEAR_BUTTON = "filter-product-selector-clear-button";
        String PRODUCT_SELECTOR_ITEM_CONTAINER = "filter-product-selector-item-container";
        String PRODUCT_SELECTOR_LABEL = "filter-product-selector-label";
        String MANAGER_SELECTOR_ADD_BUTTON = "filter-manager-selector-add-button";
        String MANAGER_SELECTOR_CLEAR_BUTTON = "filter-manager-selector-clear-button";
        String MANAGER_SELECTOR_ITEM_CONTAINER = "filter-manager-selector-item-container";
        String MANAGER_SELECTOR_LABEL = "filter-manager-selector-label";
        String INITIATORS_SELECTOR_ADD_BUTTON = "filter-initiators-selector-add-button";
        String INITIATORS_SELECTOR_CLEAR_BUTTON = "filter-initiators-selector-clear-button";
        String INITIATORS_SELECTOR_ITEM_CONTAINER = "filter-manager-selector-item-container";
        String INITIATORS_SELECTOR_LABEL = "filter-initiators-selector-label";
        String TAG_SELECTOR_ADD_BUTTON = "filter-tag-selector-add-button";
        String TAG_SELECTOR_CLEAR_BUTTON = "filter-tag-selector-clear-button";
        String TAG_SELECTOR_ITEM_CONTAINER = "filter-tag-selector-item-container";
        String TAG_SELECTOR_LABEL = "filter-tag-selector-label";
        String PRIVACY_YES_BUTTON = "filter-privacy-yes-button";
        String PRIVACY_NO_BUTTON = "filter-privacy-no-button";
        String PRIVACY_NOT_DEFINED_BUTTON = "filter-privacy-not-defined-button";
        String PRIVACY_LABEL = "filter-privacy-label";
        String ISSUE_IMPORTANCE_LABEL = "filter-issue-importance-label";
        String ISSUE_STATE_LABEL = "filter-issue-state-label";

        @Deprecated String REPORT_BUTTON = "filter-report-button";
        String CREATE_BUTTON = "filter-create-button";
        String SAVE_BUTTON = "filter-save-button";
        String RESET_BUTTON = "filter-reset-button";
        String REMOVE_BUTTON = "filter-remove-button";

        String COMPANY_SELECTOR = "filter-company-selector";
        String SHOW_FIRED = "filter-show-fired";

        interface USER_FILTER {
            String FILTERS_BUTTON = "filter-user-filters-button";
            String FILTER_NAME_INPUT = "filter-user-filter-name-input";
            String FILTER_OK_BUTTON = "filter-user-filter-ok-button";
            String FILTER_CANCEL_BUTTON = "filter-user-filter-cancel-button";
        }

        interface LABEL {
            String SORT_FIELD = "filter-sort-field-label";
            String COMPANY = "filter-company-label";
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
        String NX_REQUEST = "issue-state-nx-request";
        String CUST_REQUEST = "issue-state-cust-request";
    }

    public interface ISSUE {
        String PRIVACY_BUTTON = "issue-privacy-button";
        String PRIVACY_ICON = "issue-privacy-icon";
        String PRIVACY_ICON_PRIVATE = "issue-privacy-icon-private";
        String PRIVACY_ICON_PUBLIC = "issue-privacy-icon-public";
        String NUMBER_INPUT = "issue-number-input";
        String NAME_INPUT = "issue-name-input";
        String NAME_FIELD = "issue-name-field";
        String LINKS_BUTTON = "issue-links-button";
        String LINKS_CONTAINER = "issue-links-container";
        String LINKS_TYPE_SELECTOR = "issue-links-type-selector";
        String LINKS_INPUT = "issue-links-input";
        String LINKS_APPLY_BUTTON = "issue-links-apply-button";
        String LINKS_COLLAPSE_BUTTON = "issue-links-collapse-button";
        @Deprecated String LINKS_ERROR_LABEL = "issue-links-error-label";
        @Deprecated String LINK_INFO_CONTAINER = "issue-link-info-container";
        String LINK_REMOVE_BUTTON = "issue-link-remove-button";
        String LINK_STATE = "issue-link-state";
        @Deprecated String LINK_IMPORTANCE_ICON = "issue-link-importance-icon";
        String LINK_NUMBER = "issue-link-number";
        String LINK_DESCRIPTION = "issue-link-description";
        String LINK_ELEMENT = "issue-link-element";
        String TAGS_BUTTON = "issue-tags-button";
        String ADD_TAG_BUTTON = "issue-add-tag-button";
        String TAGS_CONTAINER = "issue-tags-container";
        String STATE_SELECTOR = "issue-state-selector";
        String IMPORTANCE_SELECTOR = "issue-importance-selector";
        String PLATFORM_SELECTOR = "issue-platform-selector";
        String COMPANY_SELECTOR = "issue-company-selector";
        String INITIATOR_SELECTOR = "issue-initiator-selector";
        String PRODUCT_SELECTOR = "issue-product-selector";
        String MANAGER_SELECTOR = "issue-manager-selector";
        String TIME_ELAPSED = "issue-time-elapsed";
        String TIME_ELAPSED_INPUT = "issue-time-elapsed-input";
        String DESCRIPTION_INPUT = "issue-description-input";
        String DESCRIPTION_FIELD = "issue-description-field";
        String NOTIFIERS_SELECTOR_ADD_BUTTON = "issue-notifiers-selector-add-button";
        String NOTIFIERS_SELECTOR_CLEAR_BUTTON = "issue-notifiers-selector-clear-button";
        String NOTIFIERS_SELECTOR_ITEM_CONTAINER = "issue-notifiers-selector-item-container";
        String NOTIFIERS_SELECTOR_LABEL = "issue-notifiers-selector-label";
        String ATTACHMENT_UPLOAD_BUTTON = "issue-attachment-upload-button";
        String ATTACHMENT_LIST_CONTAINER = "issue-attachment-list-container";
        String SAVE_BUTTON = "issue-save-button";
        String CANCEL_BUTTON = "issue-cancel-button";
        String COPY_NUMBER_BUTTON = "issue-copy-number-button";
        String COPY_NUMBER_AND_NAME_BUTTON = "issue-copy-number-and-name-button";
        String NOTIFIERS_SELECTOR = "issue-subscriptions-selector";

        interface LABEL {
            String NAME = "issue-label-name";
            String LINKS = "issue-label-links";
            String STATE = "issue-label-state";
            String IMPORTANCE = "issue-label-importance";
            String PLATFORM = "issue-label-platform";
            String COMPANY = "issue-label-company";
            String CONTACT = "issue-label-contact";
            String PRODUCT = "issue-label-product";
            String MANAGER = "issue-label-manager";
            String TIME_ELAPSED = "issue-label-time-elapsed";
            String NEW_ISSUE_TIME_ELAPSED = "issue-label-new-issue-time-elapsed";
            String INFO = "issue-label-info";
            String SUBSCRIPTIONS = "issue-label-subscriptions";
            String NOTIFIERS = "issue-label-notifiers";
            String ATTACHMENTS = "issue-label-attachments";
            @Deprecated String TAGS = "issue-label-tags";
            String TIME_ELAPSED_TYPE = "issue-label-time-elapsed-type";
        }
    }

    public interface ISSUE_PREVIEW {
        String PRIVACY_ICON = "issue-preview-privacy-icon";
        String FULL_SCREEN_BUTTON = "issue-preview-full-screen-button";
        @Deprecated String TITLE_LABEL = "issue-preview-title-label";
        @Deprecated String LINKS_CONTAINER = "issue-preview-links-container";
        String DATE_CREATED = "issue-preview-date-created-item";
        String IMPORTANCE = "issue-preview-importance-item";
        String PRODUCT = "issue-preview-product-item";
        String STATE = "issue-preview-state-item";
        String TIME_ELAPSED = "issue-preview-time-elapsed-item";
        @Deprecated String COMPANY_LABEL = "issue-preview-company-label";
        String CONTACT = "issue-preview-contact-item";
        @Deprecated String OUR_COMPANY_LABEL = "issue-preview-our-company-label";
        String MANAGER = "issue-preview-manager-item";
        String SUBSCRIPTION = "issue-preview-subscription-item";
        String NAME = "issue-preview-name-item";
        String PLATFORM = "issue-preview-platform-item";
        String INFO = "issue-preview-info-item";
        String ATTACHMENT_UPLOAD_BUTTON = "issue-preview-attachment-upload-button";
        String ATTACHMENT_LIST_CONTAINER = "issue-preview-attachment-list-container";
        String COPY_NUMBER_BUTTON = "issue-preview-copy-number-button";
        String COPY_NUMBER_AND_NAME_BUTTON = "issue-preview-copy-number-and-name-button";

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
            String EDIT_TIME_ELAPSED_TYPE_POPUP = "issue-preview-comment-item-edit-time-elapsed-type-popup";
        }

        interface LABEL {
            String PRODUCT = "issue-preview-product-label";
            String STATE = "issue-preview-state-label";
            String CONTACT = "issue-preview-contact-label";
            String MANAGER = "issue-preview-manager-label";
            String SUBSCRIPTION = "issue-preview-subscription-label";
            String PLATFORM = "issue-preview-platform-label";
            String IMPORTANCE = "issue-preview-importance-label";
            String TIME_ELAPSED = "issue-preview-time-elapsed-label";
        }
    }

    public interface DOCUMENT_EDIT {
        String COMMON_TAB = "document-edit-common-tab";
        String SEARCH_PROJECT_TAB = "document-edit-search-project-tab";
        String CREATE_PROJECT_TAB = "document-edit-create-project-tab";
        String CREATE_PRODUCT_TAB = "document-edit-create-product-tab";

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

    public interface PROJECT{
        String NUMBER_INPUT = "project-number-input";
        String NAME_INPUT = "project-name-input";
        String DESCRIPTION_INPUT = "project-description-input";
        String STATE_SELECTOR = "project-state-selector";
        String REGION_SELECTOR = "project-region-selector";
        String DIRECTION_SELECTOR = "project-direction-selector";
        String CUSTOMER_TYPE_SELECTOR = "project-customer-type-selector";
        String COMPANY_SELECTOR = "project-company-selector";
/*        String TEAM_SELECTOR = "project-team-selector";
        String PRODUCTS_SELECTOR = "project-products-selector";
        String DOCUMENTS_CONTAINER = "project-documents-container";*/

        String SAVE_BUTTON = "project-save-button";
        String CANCEL_BUTTON = "project-cancel-button";

        interface LABEL {
        }
    }

    public interface PROJECT_PREVIEW {
        String FULL_SCREEN_BUTTON = "project-preview-full-screen-button";
        String TITLE_LABEL = "project-preview-title-label";
        String DATE_CREATED_LABEL = "project-preview-date-created-label";
        String STATE_LABEL = "project-preview-state-label";
        String REGION_LABEL = "project-preview-region-label";
        String DIRECTION_LABEL = "project-preview-direction-label";
        String CUSTOMER_TYPE_LABEL = "project-preview-customer-type-label";
        String COMPANY_LABEL = "project-preview-company-label";
        String NAME_LABEL = "project-preview-name-label";
        String INFO_LABEL = "project-preview-info-label";
        String TEAM_LABEL = "project-preview-team-label";
        String PRODUCTS_LABEL = "project-preview-products-label";
        String DOCUMENTS_CONTAINER = "project-preview-documents-container";
        String COMMENTS_CONTAINER = "project-preview-comments-container";
        String CONTRACT_LABEL = "project-preview-contract-label";
        String PLATFORM_LABEL = "project-preview-platform-label";
        String TO_CONTRACT_BUTTON = "project-preview-to-contract-button";
    }

    public interface REGION_STATE {
        String DEFAULT = "region-state-";
        String UNKNOWN = "region-state-unknown";
        String MARKETING = "region-state-marketing";
        String PRESALE = "region-state-presale";
        String PROJECTING = "region-state-projecting";
        String DEVELOPMENT = "region-state-development";
        String DEPLOYMENT = "region-state-deployment";
        String TESTING = "region-state-testing";
        String SUPPORT = "region-state-support";
        String FINISHED = "region-state-finished";
        String CANCELED = "region-state-canceled";
    }

    public interface COMPANY_TABLE {
        String LOCK_ICON = "company-table-lock-icon";
    }

    public interface COMPANY_ITEM {
        String LOCK_ICON = "company-item-lock-icon";
    }

    public interface COMPANY_PREVIEW {
        String TAGS_CONTAINER = "company-preview-tags-container";
        String SUBSCRIPTION = "company-preview-subscription-item";

        interface LABEL {
            @Deprecated String TAGS = "company-preview-tags-label";
            String SUBSCRIPTION = "company-preview-label-subscription-label";
            String CONTACT_INFO = "company-preview-contact-info-label";
        }
    }

    public interface PRODUCT_TABLE {
        String LOCK_ICON = "product-table-lock-icon";
    }

    public interface PRODUCT_ITEM {
        String LOCK_ICON = "product-item-lock-icon";
    }

    public interface DOCUMENT_TABLE {
        String LOCK_ICON = "document-table-lock-icon";
    }

    public interface LOCALE_BUTTON {
        String DEFAULT = "locale-button-";
        String EN = "locale-button-en";
        String RU = "locale-button-ru";
    }

    public interface TABLE {

        interface BUTTON {
            String EDIT = "table-edit-button";
            String DOWNLOAD = "table-download-button";
            String REMOVE = "table-remove-button";
            String REFRESH = "table-refresh-button";
            String ARCHIVE = "table-archive-button";
            String ATTACHMENT = "table-attachment-button";
            String COPY = "table-copy-button";
        }

        interface ISSUE {
            String PRIVACY = "table-issue-privacy";
            String PRODUCT = "table-issue-product";
            String CREATION_DATE = "table-issue-creation-date";
            String NAME = "table-issue-name";
            String DESCRIPTION = "table-issue-description";
        }
    }

    public interface ATTACHMENT {
        String SHOW = "attachment-show-button";
        String DELETE = "attachment-delete-button";
        String DOWNLOAD = "attachment-download-button";
        String NAME = "attachment-name";
        String SIZE = "attachment-size";
        String IMAGE = "attachment-fullscreen-image";
    }

    public interface TAG_SELECTOR_POPUP {
        String ITEM = "tag-selector-popup-item";
        String EDIT_BUTTON = "tag-selector-popup-edit-button";
        String NAME = "tag-selector-popup-name";
        String COMPANY_NAME = "tab-selector-popup-company-name";
    }

    public interface CONTRACT {
        String NUMBER_INPUT = "contract-number-input";
        String TYPE_SELECTOR = "contract-type-selector";
        String STATE_SELECTOR = "contract-state-selector";
        String PARENT_SELECTOR = "contract-parent-selector";
        String DESCRIPTION_INPUT = "contract-description-input";
        String DATE_SIGNING_CONTAINER = "contract-date-signing-container";
        String DATE_VALID_CONTAINER = "contract-date-valid-container";
        String COST_WITH_CURRENCY_CONTAINER = "contract-cost-with-currency-container";
        String PROJECT_SELECTOR = "contract-project-selector";
        String DIRECTION_SELECTOR = "contract-direction-selector";
        String ORGANIZATION_SELECTOR = "contract-organization-selector";
        String CURATOR_SELECTOR = "contract-curator-selector";
        String MANAGER_SELECTOR = "contract-manager-selector";
        String CONTRAGENT_SELECTOR = "contract-contragent-selector";
        String ADD_DATES_BUTTON = "contract-add-dates-button";
        String SAVE_BUTTON = "contract-save-button";
        String CANCEL_BUTTON = "contract-cancel-button";

        interface LABEL {
            String COMMON_HEADER = "contract-common-header";
            String WORKGROUP_HEADER = "contract-workgroup-header";
            String DELIVERY_AND_PAYMENTS_PERIOD_HEADER = "contract-delivery-and-payments-period-header";
            String NUMBER = "contract-number-label";
            String TYPE = "contract-type-label";
            String STATE = "contract-state-label";
            String PARENT = "contract-parent-label";
            String DESCRIPTION = "contract-description-label";
            String DATE_SIGNING = "contract-date-signing-label";
            String DATE_VALID = "contract-date-valid-label";
            String COST_WITH_CURRENCY = "contract-cost-with-currency-label";
            String PROJECT = "contract-project-label";
            String DIRECTION = "contract-direction-label";
            String ORGANIZATION = "contract-organization-label";
            String CURATOR = "contract-curator-label";
            String MANAGER = "contract-manager-label";
            String CONTRAGENT = "contract-contragent-label";
        }

        interface DATE_ITEM {
            String ITEM = "contract-date-item";
            String TYPE_BUTTON = "contract-date-item-type-button";
            String DATE_CONTAINER = "contract-date-item-date-container";
            String COMMENT_INPUT = "contract-date-item-comment-input";
            String NOTIFY_LABEL = "contract-date-item-notify-label";
            String NOTIFY_SWITCHER = "contract-date-item-notify-switcher";
            String REMOVE_BUTTON = "contract-date-item-remove-button";
        }
    }

    public interface CLEANABLE_SEARCH_BOX {
        String SEARCH_INPUT = "cleanable-search-box-search-input";
    }

    public interface DIALOG_DETAILS {
        String MODAL_DIALOG = "dialog-details-modal-dialog";
        String NAME = "dialog-details-name";
        String CLOSE_BUTTON = "dialog-details-close-button";
        String REMOVE_BUTTON = "dialog-details-remove-button";
        String SAVE_BUTTON = "dialog-details-save-button";
        String CANCEL_BUTTON = "dialog-details-cancel-button";

        interface TAG {
            String NAME_LABEL = "dialog-details-tag-name-label";
            String COMPANY_LABEL = "dialog-details-tag-company-label";
            String COLOR_LABEL = "dialog-details-tag-color-label";
            String AUTHOR_LABEL = "dialog-details-author-label";
            String NAME_INPUT = "dialog-details-tag-name-input";
            String COMPANY_SELECTOR = "dialog-details-tag-company-selector";
            String COMPANY = "dialog-details-tag-company";
        }
    }

    public interface COLOR_PICKER {
        String BUTTON = "color-picker-button";
        String INPUT = "color-picker-input";
    }

    public interface COLLAPSIBLE_PANEL {
        String COLLAPSE_BUTTON = "collapsible-panel-collapse-button";
    }
}
