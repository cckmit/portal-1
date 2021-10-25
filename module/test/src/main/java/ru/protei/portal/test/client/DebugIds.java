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

        interface SUBSCRIPTION {

            String EMPLOYEE_SELECTOR_ADD_BUTTON = "profile-employee-selector-add-button";
            String EMPLOYEE_SELECTOR_CLEAR_BUTTON = "profile-employee-selector-clear-button";
            String EMPLOYEE_SELECTOR_ITEM_CONTAINER = "profile-employee-selector-item-container";

            interface LABEL {
                String EMPLOYEE_SELECTOR = "profile-employee-selector-label";
            }
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
        String ISSUE_ASSIGNMENT = "sidebar-menu-issue-assignment";
        String REPORTS = "sidebar-menu-issue-reports";
        @Deprecated String ISSUE_REPORTS = REPORTS;
        String OFFICIAL = "sidebar-menu-official";
        String PRODUCT = "sidebar-menu-product";
        String PROJECT = "sidebar-menu-project";
        String REGION = "sidebar-menu-region";
        String ROLE = "sidebar-menu-role";
        String SITE_FOLDER = "sidebar-menu-site-folder";
        String EMPLOYEE_REGISTRATION = "sidebar-menu-employee-registration";
        String EMPLOYEE = "sidebar-menu-employee";
        String CONTRACT = "sidebar-menu-contract";
        String EDUCATION = "sidebar-menu-education";
        String IP_RESERVATION = "sidebar-menu-ip-reservation";
        String ROOM_RESERVATION = "sidebar-menu-room-reservation";
        String PLAN = "sidebar-menu-plan";
        String ICON_SUFFIX = "-icon";
        String TOP_BRASS = "sidebar-menu-employee-top-brass";
        String EMPLOYEE_LIST = "sidebar-menu-employee-list";
        String EMPLOYEE_TABLE = "sidebar-menu-employee-table";
        String EMPLOYEE_BIRTHDAY = "sidebar-menu-employee-birthday";
        String ABSENCE = "sidebar-menu-employee-absence";
        String ARCHIVE = "sidebar-menu-archive";
        String BUG_TRACKING = "sidebar-menu-archive-bug-tracking";
        String TODO_LIST = "sidebar-menu-archive-todo-list";
        String FEATURE_REQUEST = "sidebar-menu-archive-feature-request";
        String CRM = "sidebar-menu-archive-crm";
        String ADMIN_CRM = "sidebar-menu-archive-admin-crm";
        String TEST_ZONE = "sidebar-menu-archive-test-zone";
        String CARD_SEARCH = "sidebar-menu-store-delivery-card-search";
        String STORE_AND_DELIVERY = "sidebar-menu-new-store-delivery";
        String DELIVERY = "sidebar-menu-store-delivery-new-delivery";
        String CARD = "sidebar-menu-new-store-delivery-delivery";
        String CARD_BATCH = "sidebar-menu-new-store-delivery-card-batch";
        String STORE = "sidebar-menu-store-delivery-store";
        String FLOOR_PLAN = "sidebar-menu-floor-plan";
        String NOTIFICATION_SYSTEM = "sidebar-menu-notification-system";
        String YOUTRACK = "sidebar-menu-youtrack";
        String YOUTRACK_ADMIN = "sidebar-menu-youtrack-admin";
        String RESERVED_IP = "sidebar-menu-ip-reservation-reserved-ip";
        String SUBNET = "sidebar-menu-ip-reservation-subnet";
        String ROOM_RESERVATION_CALENDAR = "sidebar-menu-room-reservation-calendar";
        String ROOM_RESERVATION_TABLE = "sidebar-menu-room-reservation-table";
        String DUTY_LOG = "sidebar-menu-duty-log";
        String DOCUMENTATION = "sidebar-menu-documentation";
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
        String TABLE = "dashboard-table-";
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
        String DATE_CREATED_RANGE_CONTAINER = "filter-date-created-range-container";
        String DATE_DEPARURE_RANGE_CONTAINER = "filter-date-departure-range-container";
        String DATE_MODIFIED_RANGE_CONTAINER = "filter-date-modified-range-container";
        String COMPANY_SELECTOR_ADD_BUTTON = "filter-company-selector-add-button";
        String COMPANY_SELECTOR_CLEAR_BUTTON = "filter-company-selector-clear-button";
        String COMPANY_SELECTOR_ITEM_CONTAINER = "filter-company-selector-item-container";
        String COMPANY_SELECTOR_LABEL = "filter-company-selector-label";
        String MANAGER_COMPANY_SELECTOR_ADD_BUTTON = "filter-manager-company-selector-add-button";
        String MANAGER_COMPANY_SELECTOR_CLEAR_BUTTON = "filter-manager-company-selector-clear-button";
        String MANAGER_COMPANY_SELECTOR_ITEM_CONTAINER = "filter-manager-company-selector-item-container";
        String MANAGER_COMPANY_SELECTOR_LABEL = "filter-manager-company-selector-label";
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
        String PLATFORMS_SELECTOR_ADD_BUTTON = "filter-platforms-selector-add-button";
        String PLATFORMS_SELECTOR_CLEAR_BUTTON = "filter-platforms-selector-clear-button";
        String PLATFORMS_SELECTOR_ITEM_CONTAINER = "filter-platform-selector-item-container";
        String PLATFORMS_SELECTOR_LABEL = "filter-platforms-selector-label";
        String TAG_SELECTOR_ADD_BUTTON = "filter-tag-selector-add-button";
        String TAG_SELECTOR_CLEAR_BUTTON = "filter-tag-selector-clear-button";
        String TAG_SELECTOR_ITEM_CONTAINER = "filter-tag-selector-item-container";
        String TAG_SELECTOR_LABEL = "filter-tag-selector-label";
        String PRIVACY_YES_BUTTON = "filter-privacy-yes-button";
        String PRIVACY_NO_BUTTON = "filter-privacy-no-button";
        String PRIVACY_NOT_DEFINED_BUTTON = "filter-privacy-not-defined-button";
        String FAVORITE_YES_BUTTON = "filter-favorite-yes-button";
        String FAVORITE_NO_BUTTON = "filter-favorite-no-button";
        String FAVORITE_NOT_DEFINED_BUTTON = "filter-favorite-not-defined-button";
        String PRIVACY_LABEL = "filter-privacy-label";
        String ISSUE_IMPORTANCE_LABEL = "filter-issue-importance-label";
        String ISSUE_STATE_LABEL = "filter-issue-state-label";
        String CREATOR_SELECTOR = "filter-creator-selector";
        String CREATOR_ADD_BUTTON = "filter-creator-add-button";
        String CREATOR_CLEAR_BUTTON = "filter-creator-clear-button";
        String CREATOR_ITEM_CONTAINER = "filter-creator-item-container";
        String COMPANY_DEPRECATED = "filter-company-deprecated";
        String PLATFORM_NAME_SEARCH_INPUT = "filter-platform-name-search-input";
        String PLATFORM_NAME_SEARCH_CLEAR_BUTTON = "filter-platform-name-clear_button";
        String PLATFORM_PARAMETERS_SEARCH_INPUT = "filter-platform-parameters-search-input";
        String PLATFORM_PARAMETERS_SEARCH_CLEAR_BUTTON = "filter-platform-parameters-clear-button";
        String PLATFORM_SERVER_IP_SEARCH_INPUT = "filter-platform-server-ip-search-input";
        String PLATFORM_SERVER_IP_SEARCH_CLEAR_BUTTON = "filter-platform-server-ip-clear-button";

        String CREATE_BUTTON = "filter-create-button";
        String SAVE_BUTTON = "filter-save-button";
        String RESET_BUTTON = "filter-reset-button";
        String REMOVE_BUTTON = "filter-remove-button";

        String COMPANY_SELECTOR = "filter-company-selector";
        String SHOW_FIRED = "filter-show-fired";

        String PLAN_SELECTOR = "filter-plan-selector";

        String OVERDUE_DEADLINES_YES_BUTTON = "filter-overdue-deadlines-yes-button";
        String OVERDUE_DEADLINES_NO_BUTTON = "filter-overdue-deadlines-no-button";
        String OVERDUE_DEADLINES_NOT_DEFINED_BUTTON = "filter-overdue-deadlines-not-defined-button";

        String COMMENT_INPUT = "filter-comment-input";

        String CARD_BATCH_SEARCH_BY_NUMBER_AND_ARTICLE_INPUT = "filter-card-batch-search-by-number-and-article-input";
        String CARD_BATCH_CONTRACTORS_SELECTOR = "filter-card-batch-contractors-selector";
        String CARD_BATCH_CONTRACTORS_ADD_BUTTON = "filter-card-batch-contractors-add-button";
        String CARD_BATCH_CONTRACTORS_CLEAR_BUTTON = "filter-card-batch-contractors-clear-button";
        String CARD_BATCH_CONTRACTORS_ITEM_CONTAINER = "filter-card-batch-contractors-item-container";

        String CARD_BATCH_CARD_TYPE_SELECTOR = "filter-card-batch-card-type-selector";
        String CARD_BATCH_CARD_TYPE_ADD_BUTTON = "filter-card-batch-card-type-add-button";
        String CARD_BATCH_CARD_TYPE_CLEAR_BUTTON = "filter-card-batch-card-type-clear-button";
        String CARD_BATCH_CARD_TYPE_ITEM_CONTAINER = "filter-card-batch-card-type-item-container";

        String CARD_BATCH_STATE_SELECTOR = "filter-card-batch-state-selector";
        String CARD_BATCH_SORT_FIELD = "filter-card-batch-sort-field-selector";
        String CARD_BATCH_SORT_DIRECTION = "filter-card-batch-sort-direction-selector";
        String CARD_BATCH_DEADLINE_SELECTOR = "filter-card-batch-deadline-selector";
        String CARD_BATCH_IMPORTANCE_SELECTOR = "filter-card-batch-importance-selector";

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

    public interface PERSON_CASE_FILTER {
        String ITEM_CONTAINER = "person-case-filter-item-container";
        String ITEM = "person-case-filter-item";
        String FILTER_SELECTOR = "person-case-filter-filter-selector";
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
        String MEDIUM = "importance-button-medium";
        String EMERGENCY = "importance-button-emergency";
        String HIGH = "importance-button-high";
        String LOW = "importance-button-low";
    }

    public interface PRIVACY_TYPE {
        String DEFAULT = "privacy-type-";
        String PUBLIC = "privacy-type-public";
        String PRIVATE_CUSTOMER = "privacy-type-private-customer";
        String PRIVATE = "privacy-type-private";
    }

    public interface PRODUCT_TYPES_BUTTON {
        String DEFAULT = "product-types-button-";
        String COMPONENT = "product-types-button-component";
        String PRODUCT = "product-types-button-product";
        String DIRECTION = "product-types-button-direction";
        String COMPLEX = "product-types-button-complex";
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
        String REQUEST_TO_PARTNER = "issue-state-request-to-partner";
        String BLOCKED = "issue-state-blocked";
        String DELIVERY_PRELIMINARY = "issue-state-delivery-preliminary";
        String DELIVERY_PRE_RESERVE = "issue-state-delivery-pre-reserve";
        String DELIVERY_RESERVE = "issue-state-delivery-reserve";
        String DELIVERY_ASSEMBLY = "issue-state-delivery-assembly";
        String DELIVERY_TEST = "issue-state-delivery-test";
        String DELIVERY_READY = "issue-state-delivery-ready";
        String DELIVERY_SENT = "issue-state-delivery-sent";
        String DELIVERY_WORK = "issue-state-delivery-work";
    }

    public interface PROJECT_STATE {
        String DEFAULT = "project-state-";
        String UNKNOWN = "project-state-unknown";
        String PAUSED = "project-state-paused";
        String MARKETING = "project-state-marketing";
        String PRESALE = "project-state-presale";
        String PROJECTING = "project-state-projecting";
        String DEVELOPMENT = "project-state-development";
        String DEPLOYMENT = "project-state-deployment";
        String TESTING = "project-state-testing";
        String SUPPORT = "project-state-support";
        String FINISHED = "project-state-finished";
        String CANCELED = "project-state-canceled";
    }

    public interface CARD_BATCH_STATE {
        String DEFAULT = "card-batch-state-";
        String IN_QUEUE_BUILD_EQUIPMENT = "card-batch-state-in-queue-build-equipment";
        String BUILD_EQUIPMENT = "card-batch-state-build-equipment";
        String IN_QUEUE_AUTOMATIC_MOUNTING = "card-batch-state-in-queue-automatic-mounting";
        String AUTOMATIC_MOUNTING = "card-batch-state-automatic-mounting";
        String IN_QUEUE_MANUAL_MOUNTING = "card-batch-state-in-queue-manual-mounting";
        String MANUAL_MOUNTING = "card-batch-state-manual-mounting";
        String IN_QUEUE_STICKER_LABELING = "card-batch-state-in-queue-sticker-labeling";
        String STICKER_LABELING = "card-batch-state-sticker-labeling";
        String TRANSFERRED_FOR_TESTING = "card-batch-state-transferred-for-testing";
    }

    public interface ISSUE {
        String BACK_BUTTON = "issue-back-button";
        String SHOW_EDIT_BUTTON = "issue-show-edit-button";
        String LINKS_BUTTON = "issue-links-button";
        String TAGS_BUTTON = "issue-tags-button";
        String FAVORITES_BUTTON = "issue-favorites-button";
        String EDIT_NAME_AND_DESC_BUTTON = "issue-edit-name-and-desc-button";
        String PRIVACY_BUTTON = "issue-privacy-button";
        String PRIVACY_ICON = "issue-privacy-icon";
        String PRIVACY_ICON_PRIVATE = "issue-privacy-icon-private";
        String PRIVACY_ICON_PUBLIC = "issue-privacy-icon-public";
        String NAME_INPUT = "issue-name-input";
        String NAME_FIELD = "issue-name-field";
        String LINKS_CONTAINER = "issue-links-container";
        String LINKS_TYPE_SELECTOR = "issue-links-type-selector";
        String LINKS_INPUT = "issue-links-input";
        String LINKS_APPLY_BUTTON = "issue-links-apply-button";
        String LINKS_COLLAPSE_BUTTON = "issue-links-collapse-button";
        String LINK_REMOVE_BUTTON = "issue-link-remove-button";
        String ADD_TAG_BUTTON = "issue-add-tag-button";
        String TAGS_CONTAINER = "issue-tags-container";
        String STATE_SELECTOR = "issue-state-selector";
        String PAUSE_DATE_CONTAINER = "issue-pause-date-container";
        String IMPORTANCE_SELECTOR = "issue-importance-selector";
        String PLATFORM_SELECTOR = "issue-platform-selector";
        String COMPANY_SELECTOR = "issue-company-selector";
        String MANAGER_COMPANY_SELECTOR = "issue-manager-company-selector";
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
        String ATTACHMENT_COLLAPSE_BUTTON = "issue-attachment-collapse-button";
        String SAVE_BUTTON = "issue-save-button";
        String CANCEL_BUTTON = "issue-cancel-button";
        String COPY_NUMBER_BUTTON = "issue-copy-number-button";
        String COPY_NUMBER_AND_NAME_BUTTON = "issue-copy-number-and-name-button";
        String NOTIFIERS_SELECTOR = "issue-subscriptions-selector";
        String EDIT_NAME_AND_DESC_ACCEPT = "issue-edit-name-and-desc-accept";
        String EDIT_NAME_AND_DESC_REJECT = "issue-edit-name-and-desc-reject";
        String PLANS_SELECTOR_ADD_BUTTON = "issue-plans-selector-add-button";
        String PLANS_SELECTOR_CLEAR_BUTTON = "issue-plans-selector-clear-button";
        String PLANS_SELECTOR_ITEM_CONTAINER = "issue-plans-selector-item-container";
        String PLANS_SELECTOR_LABEL = "issue-plans-selector-label";
        String PLANS_SELECTOR = "issue-plans-selector";
        String DEADLINE_CONTAINER = "issue-deadline-container";
        String WORK_TRIGGER_SELECTOR = "issue-work-trigger-selector";
        String SUBTASK_BUTTON = "issue-subtask-button";
        String TABS_CONTAINER = "issue-tabs-container";
        String TAB_COMMENT = "issue-tab-comments";
        String TAB_HISTORY = "issue-tab-history";

        interface LABEL {
            String NAME = "issue-label-name";
            String LINKS = "issue-label-links";
            String STATE = "issue-label-state";
            String IMPORTANCE = "issue-label-importance";
            String PLATFORM = "issue-label-platform";
            String COMPANY = "issue-label-company";
            String MANAGER_COMPANY = "issue-label-manager-company";
            String CONTACT = "issue-label-contact";
            String PRODUCT = "issue-label-product";
            String MANAGER = "issue-label-manager";
            String TIME_ELAPSED = "issue-label-time-elapsed";
            String NEW_ISSUE_TIME_ELAPSED = "issue-label-new-issue-time-elapsed";
            String INFO = "issue-label-info";
            String SUBSCRIPTIONS = "issue-label-subscriptions";
            String NOTIFIERS = "issue-label-notifiers";
            String ATTACHMENTS = "issue-label-attachments";
            String TIME_ELAPSED_TYPE = "issue-label-time-elapsed-type";
        }
    }

    public interface ISSUE_REPORT {
        String NAME_INPUT = "issue-report-name-input";
        String REPORT_TYPE = "issue-report-type";
        String REPORT_SCHEDULED_TYPE = "issue-report-scheduled-type";
        String ADDITIONAL_PARAMS = "issue-report-additional-params";
        String ADDITIONAL_PARAMS_ADD_BUTTON = "issue-report-additional-params-add-button";
        String ADDITIONAL_PARAMS_CLEAR_BUTTON = "issue-report-additional-params-clear-button";
        String ADDITIONAL_PARAMS_ITEM_CONTAINER = "issue-report-additional-params-item-container";
        String ADDITIONAL_PARAMS_LABEL = "issue-report-additional-params-label";
        String TIME_ELAPSED_TYPES = "issue-report-time-elapsed-types";
        String TIME_ELAPSED_TYPES_ADD_BUTTON = "issue-report-time-elapsed-types-add-button";
        String TIME_ELAPSED_TYPES_CLEAR_BUTTON = "issue-report-time-elapsed-types-clear-button";
        String TIME_ELAPSED_TYPES_ITEM_CONTAINER = "issue-report-time-elapsed-types-item-container";
        String TIME_ELAPSED_TYPES_LABEL = "issue-report-time-elapsed-types-label";
        String CREATE_BUTTON = "issue-report-create-button";
        String CANCEL_BUTTON = "issue-report-cancel-button";
        String WORK_TRIGGER_TYPES = "issue-report-work-trigger-types";
        String WORK_TRIGGER_TYPES_ADD_BUTTON = "issue-report-work-trigger-types-add-button";
        String WORK_TRIGGER_TYPES_CLEAR_BUTTON = "issue-report-work-trigger-types-clear-button";
        String WORK_TRIGGER_TYPES_ITEM_CONTAINER = "issue-report-work-trigger-types-item-container";
        String WORK_TRIGGER_TYPES_LABEL = "issue-report-work-trigger-types-label";
    }

    public interface CASE_COMMENT {

        interface COMMENT_LIST {
            String ITEMS_LIST = "issue-preview-comment-list-items-list";
            String NEW_MESSAGE = "issue-preview-comment-list-new-message";
            String USER_ICON = "issue-preview-comment-list-user-icon";
            String TEXT_INPUT = "issue-preview-comment-list-text-input";
            String PRIVACY_BUTTON = "issue-preview-comment-list-privacy-button";
            String SEND_BUTTON = "issue-preview-comment-list-send-button";
            String CANCEL_BUTTON = "issue-preview-comment-list-cancel-button";
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
            String TIME_ELAPSED = "issue-preview-comment-item-elapsed-time";
            String CREATE_DATE = "issue-preview-comment-item-create-date";
            String EDIT_TIME_ELAPSED_TYPE_POPUP = "issue-preview-comment-item-edit-time-elapsed-type-popup";
        }
    }

    public interface CASE_HISTORY {
        interface ITEM {
            String CREATE_DATE = "issue-case-history-item-create-date";
            String ADDED_VALUE = "issue-case-history-item-added-value";
            String REMOVED_VALUE = "issue-case-history-item-removed-value";
            String OLD_VALUE = "issue-case-history-item-old-value";
            String NEW_VALUE = "issue-case-history-item-new-value";
            String HISTORY_TYPE = "issue-case-history-item-history-type";
            String INITIATOR = "issue-case-history-item-initiator";
        }
    }

    @Deprecated public interface ISSUE_PREVIEW {
        @Deprecated String FULL_SCREEN_BUTTON = DebugIds.ISSUE.SHOW_EDIT_BUTTON;
        @Deprecated String PLATFORM = DebugIds.SITE_FOLDER.LINK.PLATFORM;

        @Deprecated interface COMMENT_LIST {
            @Deprecated String COMMENTS_LIST = DebugIds.CASE_COMMENT.COMMENT_LIST.ITEMS_LIST;
            @Deprecated String USER_ICON = DebugIds.CASE_COMMENT.COMMENT_LIST.USER_ICON;
            @Deprecated String TEXT_INPUT = DebugIds.CASE_COMMENT.COMMENT_LIST.TEXT_INPUT;
            @Deprecated String PRIVACY_BUTTON = DebugIds.CASE_COMMENT.COMMENT_LIST.PRIVACY_BUTTON;
            @Deprecated String SEND_BUTTON = DebugIds.CASE_COMMENT.COMMENT_LIST.SEND_BUTTON;
            @Deprecated String FILES_UPLOAD = DebugIds.CASE_COMMENT.COMMENT_LIST.FILES_UPLOAD;
            @Deprecated String TIME_ELAPSED = DebugIds.CASE_COMMENT.COMMENT_LIST.TIME_ELAPSED;
            @Deprecated String TIME_ELAPSED_TYPE = DebugIds.CASE_COMMENT.COMMENT_LIST.TIME_ELAPSED_TYPE;
        }

        @Deprecated interface COMMENT_ITEM {
            @Deprecated String PRIVACY_ICON = DebugIds.CASE_COMMENT.COMMENT_ITEM.PRIVACY_ICON;
            @Deprecated String REPLY_BUTTON = DebugIds.CASE_COMMENT.COMMENT_ITEM.REPLY_BUTTON;
            @Deprecated String EDIT_BUTTON = DebugIds.CASE_COMMENT.COMMENT_ITEM.EDIT_BUTTON;
            @Deprecated String REMOVE_BUTTON = DebugIds.CASE_COMMENT.COMMENT_ITEM.REMOVE_BUTTON;
            @Deprecated String OWNER = DebugIds.CASE_COMMENT.COMMENT_ITEM.OWNER;
            @Deprecated String TIME_ELAPSED = DebugIds.CASE_COMMENT.COMMENT_ITEM.TIME_ELAPSED;
            @Deprecated String CREATE_DATE = DebugIds.CASE_COMMENT.COMMENT_ITEM.CREATE_DATE;
            @Deprecated String EDIT_TIME_ELAPSED_TYPE_POPUP = DebugIds.CASE_COMMENT.COMMENT_ITEM.EDIT_TIME_ELAPSED_TYPE_POPUP;
        }
    }

    public interface PROJECT{
        String NUMBER_INPUT = "project-number-input";
        String NAME_INPUT = "project-name-input";
        String DESCRIPTION_INPUT = "project-description-input";
        String STATE_SELECTOR = "project-state-selector";
        String REGION_SELECTOR = "project-region-selector";
        String CUSTOMER_TYPE_SELECTOR = "project-customer-type-selector";
        String COMPANY_SELECTOR = "project-company-selector";
        String LINKS_BUTTON = "project-links-button";
        String TECHNICAL_SUPPORT_VALIDITY_CONTAINER = "project-technical-support-validity-container";
        String WORK_COMPLETION_DATE = "project-work-completion-date";
        String PURCHASE_DATE = "project-purchase-date";
        String SLA_INPUT = "project-sla-input";

        String DIRECTION_SELECTOR = "project-direction-selector";
        String DIRECTION_SELECTOR_ADD_BUTTON = "project-direction-selector-add-button";
        String DIRECTION_SELECTOR_CLEAR_BUTTON = "project-direction-selector-clear-button";
        String DIRECTION_SELECTOR_ITEM_CONTAINER = "project-direction-selector-item-container";
        String DIRECTION_SELECTOR_LABEL = "project-direction-selector-label";
        String PRODUCT_SELECTOR = "project-product-selector";
        String PRODUCT_SELECTOR_ADD_BUTTON = "project-product-selector-add-button";
        String PRODUCT_SELECTOR_CLEAR_BUTTON = "project-product-selector-clear-button";
        String PRODUCT_SELECTOR_ITEM_CONTAINER = "project-product-selector-item-container";
        String PRODUCT_SELECTOR_LABEL = "project-product-selector-label";
        String SUBCONTRACTOR_SELECTOR = "project-subcontractor-selector";
        String SUBCONTRACTOR_SELECTOR_ADD_BUTTON = "project-subcontractor-selector-add-button";
        String SUBCONTRACTOR_SELECTOR_CLEAR_BUTTON = "project-subcontractor-selector-clear-button";
        String SUBCONTRACTOR_SELECTOR_ITEM_CONTAINER = "project-subcontractor-selector-item-container";
        String SUBCONTRACTOR_SELECTOR_LABEL = "project-subcontractor-selector-label";
        String PLAN_SELECTOR = "project-plan-selector";
        String PLAN_SELECTOR_ADD_BUTTON = "project-plan-selector-add-button";
        String PLAN_SELECTOR_CLEAR_BUTTON = "project-plan-selector-clear-button";
        String PLAN_SELECTOR_ITEM_CONTAINER = "project-plan-selector-item-container";
        String PLAN_SELECTOR_LABEL = "project-plan-selector-label";

        String TEAM_MEMBER_ROLE = "project-team-member-role-";
        String TEAM_MEMBER_ROLE_SELECTOR = "project-team-member-role-selector";
        String TEAM_MEMBER_SELECTOR = "project-team-member-selector";
        String TEAM_MEMBER_ADD_BUTTON = "project-team-member-add-button";
        String TEAM_MEMBER_CLEAR_BUTTON = "project-team-member-clear-button";
        String TEAM_MEMBER_ITEM_CONTAINER = "project-team-member-item-container";

        String SAVE_BUTTON = "project-save-button";
        String CANCEL_BUTTON = "project-cancel-button";
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
        String CONTRACTS_CONTAINER = "project-preview-contracts-container";
        String PLATFORMS_LABEL = "project-preview-platforms-label";
        String TECHNICAL_SUPPORT_VALIDITY_CONTAINER = "project-preview-technical-support-validity-container";
        String WORK_COMPLETION_DATE = "project-preview-work-completion-date";
        String PURCHASE_DATE = "project-preview-purchase-date";
        String SLA_INPUT = "project-preview-sla-input";
    }

    public interface PROJECT_FILTER {
        String DIRECTION_SELECTOR = "project-filter-direction-selector";
        String DIRECTION_ADD_BUTTON = "project-filter-direction-add-button";
        String DIRECTION_CLEAR_BUTTON = "project-filter-direction-clear-button";
        String DIRECTION_ITEM_CONTAINER = "project-filter-direction-item-container";

        String REGION_SELECTOR = "project-filter-region-selector";
        String REGION_ADD_BUTTON = "project-filter-region-add-button";
        String REGION_CLEAR_BUTTON = "project-filter-region-clear-button";
        String REGION_ITEM_CONTAINER = "project-filter-region-item-container";

        String HEAD_MANAGER_SELECTOR = "project-filter-head-manager-selector";
        String HEAD_MANAGER_ADD_BUTTON = "project-filter-head-manager-add-button";
        String HEAD_MANAGER_CLEAR_BUTTON = "project-filter-head-manager-clear-button";
        String HEAD_MANAGER_ITEM_CONTAINER = "project-filter-head-manager-item-container";

        String TEAM_MEMBER_SELECTOR = "project-filter-team-member-selector";
        String TEAM_MEMBER_ADD_BUTTON = "project-filter-team-member-add-button";
        String TEAM_MEMBER_CLEAR_BUTTON = "project-filter-team-member-clear-button";
        String TEAM_MEMBER_ITEM_CONTAINER = "project-filter-team-member-item-container";

        String INITIATOR_COMPANY_SELECTOR = "project-filter-initiator-company-selector";
        String INITIATOR_COMPANY_ADD_BUTTON = "project-filter-initiator-company-add-button";
        String INITIATOR_COMPANY_CLEAR_BUTTON = "project-filter-initiator-company-clear-button";
        String INITIATOR_COMPANY_ITEM_CONTAINER = "project-filter-initiator-company-item-container";

        String PRODUCT_SELECTOR = "project-filter-product-selector";
        String PRODUCT_ADD_BUTTON = "project-filter-product-add-button";
        String PRODUCT_CLEAR_BUTTON = "project-filter-product-clear-button";
        String PRODUCT_ITEM_CONTAINER = "project-filter-product-item-container";

        String COMMENT_DATE_RANGE = "project-filter-comment-date-range";
        String SORT_FIELD_SELECTOR = "project-filter-sort-field-selector";
        String SORT_DIRECTION_BUTTON = "project-filter-sort-direction-button";
        String ONLY_MINE_PROJECTS = "project-filter-only-mine-projects-checkbox";
    }

    public interface COMPANY_TABLE {
        String LOCK_ICON = "company-table-lock-icon";
    }

    public interface PRODUCT_TABLE {
        String LOCK_ICON = "product-table-lock-icon";
        interface FILTER {
            String SHOW_DEPRECATED = "product-filter-show-deprecated-button";
            String TYPES = "product-filter-types-selector";
            String DIRECTION = "product-filter-direction";
        }
    }

    public interface COMPANY {
        String NAME = "company-name";
        String VERIFIABLE_ICON = "company-verifiable-icon";
        String CATEGORY = "company-category";
        String CATEGORY_IMAGE = "company-category-image";
        String PARENT = "company-parent";
        String LINK_MESSAGE = "company-link-message";
        String COMMENT = "company-comment";
        String AUTO_OPEN_ISSUES = "company-auto-open-issues";
        String SUBSCRIPTIONS = "company-subscriptions";
        String PROBATION_GENERAL_EMAILS = "company-probation-general-emails";
        String PROBATION_EMAILS = "company-probation-emails";
        interface GROUP {
            String ADD_BUTTON = "company-subscriptions-add-button";

            String ROOT = "company-subscriptions-group-root";
            String PLATFORM = "company-subscriptions-group-platform";
            String PRODUCT = "company-subscriptions-group-product";
            String COLLAPSE_BUTTON = "company-subscriptions-group-collapse-button";
            String REMOVE_BUTTON = "company-subscriptions-group-remove-button";
            String QUANTITY = "company-subscriptions-group-quantity";
            String ITEMS = "company-subscriptions-group-items";
            interface ITEM {
                String ROOT = "company-subscriptions-group-item-root";
                String LOCALE = "company-subscriptions-group-item-locale";
                String EMAIL = "company-subscriptions-group-item-email";
            }
        }
        String WEB_SITE = "company-web-site";
        String PHONES = "company-phones";
        String EMAILS = "company-emails";
        String ACTUAL_ADDRESS = "company-actual-address";
        String LEGAL_ADDRESS = "company-legal-address";
        String TABS = "company-tabs";
        String TAB_CONTACTS = "company-tab-contacts";
        String TAB_SITE_FOLDERS = "company-tab-site-folders";
        String CONTACTS = "company-contacts";
        String SITE_FOLDERS = "company-site-folders";
        String SAVE_BUTTON = "company-save-button";
        String CANCEL_BUTTON = "company-cancel-button";
    }

    public interface CONTACT_INFO {
        String ROOT = "contact-info-root";
        interface ITEM {
            String ROOT = "contact-info-item-root";
            String TYPE = "contact-info-item-type";
            String VALUE = "contact-info-item-value";
        }
    }

    public interface CONCISE_TABLE {
        String CONTACT = "contact-concise-table";
        String PLATFORM = "platform-concise-table";
    }

    public interface PRODUCT {
        String NAME = "product-name";
        String INTERNAL_DOC_LINK = "internal_doc_link";
        String EXTERNAL_DOC_LINK = "external_doc_link";
        String DESCRIPTION = "product-description";
        String SUBSCRIPTIONS = "product-subscriptions";
        String INCLUDES = "product-includes";
        String PRODUCTS = "product-products";
        String ALIASES = "product-aliases";
        String SAVE_BUTTON = "product-save-button";
        String CANCEL_BUTTON = "product-cancel-button";
        String DIRECTION = "product-direction";
        String DIRECTIONS = "product-directions";
        String DIRECTION_LABEL = "product-direction-label";
        String COMMON_MANAGER = "product-common-manager";
    }

    public interface PRODUCT_PREVIEW {
        String NAME = "product-preview-name";
        String INTERNAL_DOC_LINK = "product-preview-internal-doc-link";
        String EXTERNAL_DOC_LINK = "product-preview-external-doc-link";
        String DESCRIPTION = "product-preview-description";
        String BACK_BUTTON = "product-preview-back-button";
        String DIRECTION_LABEL = "product-preview-direction-label";
        String PARENTS_CONTAINER = "product-preview-parents-container";
    }

    public interface PRODUCT_ITEM {
        String LOCK_ICON = "product-item-lock-icon";
    }

    public interface DOCUMENT {
        interface TABLE {
            String LOCK_ICON = "document-table-lock-icon";
        }

        interface FILTER {
            String SEARCH_INPUT = "document-filter-search-input";
            String SORT_BY_LABEL = "document-filter-sort-by-label";
            String SORT_BY_SELECTOR = "document-filter-sort-by-selector";
            String SORT_BY_TOGGLE = "document-filter-sort-by-toggle";
            String CREATION_DATE_LABEL = "document-filter-creation-date-label";
            String CREATION_DATE_INPUT = "document-filter-creation-date-input";
            String CREATION_DATE_BUTTON = "document-filter-creation-date-button";
            String MANAGER_LABEL = "document-filter-manager-label";
            String MANAGER_SELECTOR = "document-filter-manager-selector";
            String PROJECT_LABEL = "document-filter-project-label";
            String PROJECT_SELECTOR_ADD_BUTTON = "document-filter-project-selector-add-button";
            String PROJECT_SELECTOR_CLEAR_BUTTON = "document-filter-project-selector-clear-button";
            String PROJECT_SELECTOR_ITEM_CONTAINER = "document-filter-project-selector-item-container";
            String ORGANIZATION_CODE_LABEL = "document-filter-organization-code-label";
            String ORGANIZATION_CODE_PROTEI = "document-filter-organization-code-protei";
            String ORGANIZATION_CODE_PROTEI_ST = "document-filter-organization-code-protei-st";
            String DOCUMENT_TYPE_LABEL = "document-filter-document-type-label";
            String DOCUMENT_TYPE_SELECTOR = "document-filter-document-type-selector";
            String DOCUMENT_CATEGORY_LABEL = "document-filter-document-category-label";
            String DOCUMENT_CATEGORY_SELECTOR = "document-filter-document-category-selector";
            String APPROVED_LABEL = "document-filter-approved-label";
            String APPROVED_YES = "document-filter-approved-yes";
            String APPROVED_ANY = "document-filter-approved-any";
            String APPROVED_NO = "document-filter-approved-no";
            String DOCUMENT_TEXT_LABEL = "document-filter-document-text-label";
            String DOCUMENT_TEXT_INPUT = "document-filter-document-text-input";
            String KEY_WORD_LABEL = "document-filter-key-word-label";
            String KEY_WORD_INPUT = "document-filter-key-word-input";
            String SHOW_DEPRECATED_CHECKBOX = "document-filter-show-deprecated-checkbox";
            String RESET_BUTTON = "document-filter-reset-button";
        }

        interface PREVIEW {
            String DOWNLOAD_DOC_BUTTON = "document-preview-download-doc-button";
            String DOWNLOAD_PDF_BUTTON = "document-preview-download-pdf-button";
            String DOWNLOAD_APPROVAL_BUTTON = "document-preview-download-approval-button";
            String DOC_COMMENT_INPUT = "document-preview-doc-comment-input";
            String DOC_UPLOADER = "document-preview-doc-uploader";
            String UPLOAD_DOC_FILE_BUTTON = "document-preview-upload-doc-file-button";
            String BACK_BUTTON = "document-preview-back-button";
            String HEADER_LABEL = "document-preview-header-label";
            String CREATED_BY = "document-preview-created-by";
            String ANNOTATION_LABEL = "document-preview-annotation-label";
            String KEY_WORDS_LABEL = "document-preview-key-words-label";
            String KEY_WORDS = "document-preview-key-words";
            String COMMON_HEADER_LABEL = "document-preview-common-header-label";
            String VERSION_LABEL = "document-preview-version-label";
            String VERSION = "document-preview-version";
            String TYPE_LABEL = "document-preview-type-label";
            String TYPE = "document-preview-type";
            String EXECUTION_TYPE_LABEL = "document-preview-execution-type-label";
            String EXECUTION_TYPE = "document-preview-execution-type";
            String PROJECT_LABEL = "document-preview-project-label";
            String PROJECT = "document-preview-project";
            String NUMBER_DECIMAL_LABEL = "document-preview-number-decimal-label";
            String NUMBER_DECIMAL = "document-preview-number-decimal";
            String NUMBER_INVENTORY_LABEL = "document-preview-number-inventory-label";
            String NUMBER_INVENTORY = "document-preview-number-inventory";
            String WORK_GROUP_HEADER_LABEL = "document-preview-work-group-header-label";
            String MANAGER_LABEL = "document-preview-manager-label";
            String MANAGER = "document-preview-manager";
            String REGISTRAR_LABEL = "document-preview-registrar-label";
            String REGISTRAR = "document-preview-registrar";
            String CONTRACTOR_LABEL = "document-preview-contractor-label";
            String CONTRACTOR = "document-preview-contractor";
            String MEMBERS_LABEL = "document-preview-member-label";
            String MEMBERS = "document-preview-member";
            String UPLOAD_WORK_DOCUMENTATION_LABEL = "document-preview-upload-work-documentation-label";
            String DOC_COMMENT_LABEL = "document-preview-doc-comment-label";
            String DOC_UPLOAD_CONTAINER_LOADING = "document-preview-doc-upload-container-loading";
        }

        interface PROJECT_SET {
            String BUTTON = "document-project-set-button";
        }

        interface CREATE {
            String BUTTON = "document-create-button";
            String NAME_LABEL = "document-create-name-label";
            String NAME_INPUT = "document-create-name-input";
            String VERSION_LABEL = "document-create-version-label";
            String VERSION_INPUT = "document-create-version-input";
            String INVENTORY_LABEL = "document-create-inventory-label";
            String INVENTORY_INPUT = "document-create-inventory-input";
            String DECIMAL_NUMBER_LABEL = "document-create-decimal-number-label";
            String DECIMAL_NUMBER_INPUT = "document-create-decimal-number-input";
            String ANNOTATION_LABEL = "document-create-annotation-label";
            String ANNOTATION_INPUT = "document-create-annotation-input";
            String KEY_WORD_LABEL = "document-create-key-word-label";
            String KEY_WORD_INPUT = "document-create-key-word-input";
            String APPROVED_CHECKBOX = "document-create-approved-checkbox";
            String APPROVED_LABEL = "document-create-approved-label";
            String APPROVED_SELECTOR = "document-create-approved-selector";
            String REGISTRAR_LABEL = "document-create-registrar-label";
            String REGISTRAR_SELECTOR = "document-create-registrar-selector";
            String CONTRACTOR_LABEL = "document-create-contractor-label";
            String CONTRACTOR_SELECTOR = "document-create-contractor-selector";
            String PROJECT_LABEL = "document-create-project-label";
            String PROJECT_SELECTOR = "document-create-project-selector";
            String EQUIPMENT_LABEL = "document-create-equipment-label";
            String EQUIPMENT_SELECTOR = "document-create-equipment-selector";
            String EXECUTION_TYPE_LABEL = "document-create-execution-type-label";
            String EXECUTION_TYPE_SELECTOR = "document-create-execution-type-selector";
            String DOCUMENT_CATEGORY_LABEL = "document-create-document-category-label";
            String DOCUMENT_CATEGORY_SELECTOR = "document-create-document-category-selector";
            String DOCUMENT_TYPE_LABEL = "document-create-document-type-label";
            String DOCUMENT_TYPE_SELECTOR = "document-create-document-type-selector";
            String APPROVE_DATE_LABEL = "document-create-approve-date-label";
            String APPROVE_DATE_INPUT = "document-create-approve-date-input";
            String APPROVE_DATE_BUTTON = "document-create-approve-date-button";
            String DOC_LABEL = "document-create-doc-label";
            String DOC_BUTTON = "document-create-doc-button";
            String DOC_DROP_ZONE = "document-create-doc-drop-zone";
            String PDF_LABEL = "document-create-pdf-label";
            String PDF_BUTTON = "document-create-pdf-button";
            String PDF_DROP_ZONE = "document-create-pdf-drop-zone";
            String PDF_APPROVED_LABEL = "document-create-pdf-approved-label";
            String PDF_APPROVED_BUTTON = "document-create-pdf-approved-button";
            String PDF_APPROVED_DROP_ZONE = "document-create-pdf-approved-drop-zone";
            String EMPLOYEE_PERMISSION_LABEL = "document-create-employee-permission-label";
            String EMPLOYEE_ADD_BUTTON = "document-create-employee-add-button";
            String EMPLOYEE_CLEAR_BUTTON = "document-create-employee-clear-button";
            String CUSTOMER_TYPE_LABEL = "document-create-customer-type-label";
            String CUSTOMER_TYPE = "document-create-customer-type";
            String DIRECTION_LABEL = "document-create-direction-label";
            String DIRECTION = "document-create-direction";
            String REGION_LABEL = "document-create-region-label";
            String REGION = "document-create-region";
            String PROJECT_INFO_LABEL = "document-create-project-info-label";
            String EQUIPMENT_INFO_LABEL = "document-create-equipment-info-label";
            String DOCUMENT_INFO_LABEL = "document-create-document-info-label";
            String PREVIOUS_BUTTON = "document-create-previous-button";
            String NEXT_BUTTON = "document-create-next-button";
        }

        interface PROJECT_SEARCH {
            String BUTTON = "document-project-search-button";
            String NAME_LABEL = "document-project-search-name-label";
            String NAME_INPUT = "document-project-search-name-input";
            String CREATION_DATE_LABEL = "document-project-search-creation-date-label";
            String CREATION_DATE_INPUT = "document-project-search-creation-date-input";
            String CREATION_DATE_BUTTON = "document-project-search-creation-date-button";
            String CUSTOMER_TYPE_LABEL = "document-project-search-customer-type-label";
            String CUSTOMER_TYPE_SELECTOR = "document-project-search-customer-type-selector";
            String PRODUCT_LABEL = "document-project-search-product-label";
            String PRODUCT_SELECTOR = "document-project-search-product-selector";
            String PRODUCTS_CONTAINER = "document-project-search-product-container";
            String MANAGER_SELECTOR = "document-project-search-manager-selector";
            String MANAGERS_CONTAINER = "document-project-search-manager-container";
            String FIND_BUTTON = "document-project-search-find-button";
            String RESET_BUTTON = "document-project-search-reset-button";
            String SHOW_FIRST_RECORDS_LABEL = "document-project-search-show-first-records-label";
        }

        interface PROJECT_CREATE {
            String BUTTON = "document-project-create-button";
            String NAME_LABEL = "document-project-create-name-label";
            String NAME_INPUT = "document-project-create-name-input";
            String DESCRIPTION_LABEL = "document-project-create-description-label";
            String DESCRIPTION_INPUT = "document-project-create-description-input";
            String REGION_LABEL = "document-project-create-region-label";
            String REGION_SELECTOR = "document-project-create-region-selector";
            String DIRECTION_LABEL = "document-project-create-direction-label";
            String DIRECTION_SELECTOR = "document-project-create-direction-selector";
            String CUSTOMER_TYPE_LABEL = "document-project-create-customer-type-label";
            String CUSTOMER_TYPE_SELECTOR = "document-project-create-customer-type-selector";
            String COMPANY_LABEL = "document-project-create-company-label";
            String COMPANY_SELECTOR = "document-project-create-company-selector";
            String PRODUCT_LABEL = "document-project-create-product-label";
            String PRODUCT_SELECTOR = "document-project-create-product-selector";
            String SAVE_BUTTON = "document-project-create-save-button";
            String RESET_BUTTON = "document-project-create-reset-button";
            String MANAGER_SELECTOR = "document-project-manager-selector";
        }
    }

    public interface PROJECT_LIST {
        String ITEM = "project-list-item";
    }

    public interface STRING_SELECT_INPUT {
        String ITEM = "string-select-input";
        String REMOVE_BUTTON = "string-select-input-remove-button";
    }

    public interface STRING_TAG_INPUT_FORM {
        String ITEM = "string-tag-input-form-item";
        String REMOVE_BUTTON = "string-tag-input-form-remove-button";
    }

    public interface LOCALE_BUTTON {
        String DEFAULT = "locale-button-";
        String EN = "locale-button-en";
        String RU = "locale-button-ru";
    }

    public interface TABLE {

        interface BUTTON {
            String FAVORITES = "table-favorites-button";
            String EDIT = "table-edit-button";
            String DOWNLOAD = "table-download-button";
            String REMOVE = "table-remove-button";
            String REFRESH = "table-refresh-button";
            String ARCHIVE = "table-archive-button";
            String ATTACHMENT = "table-attachment-button";
            String COPY = "table-copy-button";
            String CANCEL = "table-cancel-button";
        }

        interface ISSUE {
            String PRIVACY = "table-issue-privacy";
            String PRODUCT = "table-issue-product";
            String CREATION_DATE = "table-issue-creation-date";
            String NAME = "table-issue-name";
            String DESCRIPTION = "table-issue-description";
        }

        interface DELIVERY {
            String PRODUCT = "table-delivery-product";
            String DELIVERY_DATE = "table-delivery-delivery-date";
            String NAME = "table-delivery-name";
            String DESCRIPTION = "table-delivery-description";
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
        String DELIVERY_NUMBER_INPUT = "contract-delivery-number-input";
        String DESCRIPTION_INPUT = "contract-description-input";
        String DATE_SIGNING_CONTAINER = "contract-date-signing-container";
        String DATE_VALID_CONTAINER = "contract-date-valid-container";
        String COST_WITH_CURRENCY_CONTAINER = "contract-cost-with-currency-container";
        String CURRENCY_SELECTOR = "contract-currency-selector";
        String VAT_SELECTOR = "contract-vat-selector";
        String PROJECT_SELECTOR = "contract-project-selector";
        String DIRECTION_INPUT = "contract-direction-input";
        String ORGANIZATION_SELECTOR = "contract-organization-selector";
        String CURATOR_SELECTOR = "contract-curator-selector";
        String MANAGER_SELECTOR = "contract-manager-selector";
        String MANAGER_FIELD = "contract-manager-field";
        String DATE_VALID_DAYS_INPUT = "contract-date-valid-days-input";
        String DATE_VALID_DATE_SELECTOR = "contract-date-valid-date-selector";
        String CONTRACTOR_SELECTOR = "contract-contractor-selector";
        String ADD_DATES_BUTTON = "contract-add-dates-button";
        String ADD_SPECIFICATIONS_BUTTON = "contract-add-specifications-button";
        String SAVE_BUTTON = "contract-save-button";
        String CANCEL_BUTTON = "contract-cancel-button";
        String CONTRACT_SIGN_MANAGER_SELECTOR = "contract-contract-sign-manager-selector";
        String BACK_BUTTON = "contract-back-button";
        String ADD_TAG_BUTTON = "contract-add-tag-button";
        String DELIVERY_AND_PAYMENTS_PERIOD_TAB = "contract-delivery-and-payments-period-tab";
        String SPECIFICATION_TAB = "contract-specification-tab";
        String EXPENDITURE_CONTRACTS_TAB = "expenditure-contracts-tab";

        interface LABEL {
            String COMMON_HEADER = "contract-common-header";
            String WORKGROUP_HEADER = "contract-workgroup-header";
            String DELIVERY_AND_PAYMENTS_PERIOD_HEADER = "contract-delivery-and-payments-period-header";
            String SPECIFICATION_HEADER = "contract-specification-header";
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
            String CONTRACTOR = "contract-contractor-label";
            String CONTRACT_SIGN_MANAGER = "contract-contract-sign-manager-label";
        }

        interface DATE_ITEM {
            String ITEM = "contract-date-item";
            String TYPE_BUTTON = "contract-date-item-type-button";
            String DATE_CONTAINER = "contract-date-item-date-container";
            String CALENDAR_DAY = "contract-date-item-calendar-day";
            String COMMENT_INPUT = "contract-date-item-comment-input";
            String NOTIFY_LABEL = "contract-date-item-notify-label";
            String NOTIFY_SWITCHER = "contract-date-item-notify-switcher";
            String REMOVE_BUTTON = "contract-date-item-remove-button";
        }

        interface PAYMENT {
            String COST_TYPE_SELECTOR = "contract-payment-type-selector";
            String MONEY_PERCENT_INPUT = "contract-payment-money-percent-input";

        }

        interface COST_WITH_CURRENCY {
            String MONEY_AMOUNT_INPUT = "contract-payment-money-amount-input";
            String CURRENCY_SELECTOR = "contract-payment-currency-selector";
        }

        interface SPECIFICATION_ITEM {
            String ITEM = "contract-specification-item";
            String CLAUSE_INPUT = "contract-specification-item-clause-input";
            String TEXT_INPUT = "contract-specification-item-text-input";
            String QUANTITY_INPUT = "contract-specification-quantity-input";
            String REMOVE_BUTTON = "contract-specification-item-remove-button";
        }

        interface CONTRACTOR {
            String INN_INPUT = "contract-contractor-inn-input";
            String KPP_INPUT = "contract-contractor-kpp-input";
            String NAME_INPUT = "contract-contractor-name-input";
            String FULL_NAME_INPUT = "contract-contractor-full-name-input";
            String COUNTRY_SELECTOR = "contract-contractor-country-selector";
            String SEARCH_BUTTON = "contract-contractor-search-button";
            String CONTRACTOR_SEARCH_SELECTOR = "contract-contractor-search-selector";
        }

        interface PROJECT {
            String NAME = "contract-project-name";
            String SEARCH_BUTTON = "contract-project-search-button";
            String RESET_BUTTON = "contract-project-reset-button";
        }
    }

    public interface CONTRACT_PREVIEW {
        String CONTRACT_TITLE_LABEL = "contract-preview-contract-title-label";
        String CONTRACT_NAME_LABEL = "contract-preview-contract-name-label";
        String CONTRACTOR_LABEL = "contract-preview-contractor-label";
        String DATE_SIGNING_LABEL = "contract-preview-date-signing-label";
        String DATE_VALID_LABEL = "contract-preview-date-valid-label";
        String DIRECTIONS_LABEL = "contract-preview-product-directions-label";
        String DELIVERY_NUMBER_LABEL = "contract-preview-delivery-number-label";
        String RECEIPT_AGREEMENT_LABEL = "contract-preview-receipt-agreement-label";
        String EXPENDITURE_AGREEMENT_LABEL = "contract-preview-expenditure-agreement-label";
        String TAGS_LABEL = "contract-preview-tags-label";
        String ORGANIZATION_LABEL = "contract-preview-organization-label";
        String PROJECT_LABEL = "contract-preview-project-label";
        String CURATOR_LABEL = "contract-preview-curator-label";
        String PROJECT_MANAGER_LABEL = "contract-preview-project-manager-label";
        String SIGN_MANAGER_LABEL = "contract-preview-sign-manager-label";
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
        String ADDITIONAL_BUTTON = "dialog-details-additional-button";

        interface TAG {
            String NAME_LABEL = "dialog-details-tag-name-label";
            String VERIFIABLE_ICON = "dialog-details-tag-verifiable-icon";;
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

    public interface SITE_FOLDER {
        interface PLATFORM {
            String PROJECT = "site-folder-platform-project";
            String COMPANY = "site-folder-platform-company";
            String COPY_PREVIEW_LINK_BUTTON = "site-folder-platform-copy-preview-link-button";
            String NAME = "site-folder-platform-name";
            String MANAGER = "site-folder-platform-manager";
            String PARAMETERS = "site-folder-platform-parameters";
            String COMMENT = "site-folder-platform-comment";
            String UPLOADER = "site-folder-platform-uploader";
            String ATTACHMENTS = "site-folder-platform-attachment";
            String TABS = "site-folder-platform-tabs";
            String TAB_SERVERS = "site-folder-platform-tab-servers";
            String TAB_COMPANY_CONTACTS = "site-folder-platform-tab-company-contacts";
            String SERVERS = "site-folder-servers";
            String CONTACTS = "site-folder-contacts";
            String TECHNICAL_SUPPORT_VALIDITY = "site-folder-technical-support-validity";

            String SAVE_BUTTON = "site-folder-save-button";
            String CANCEL_BUTTON = "site-folder-cancel-button";
        }
        interface SERVER {
            String OPEN_BUTTON = "site-folder-server-open-button";
            String EXPORT_BUTTON = "site-folder-server-export-button";
            String CREATE_BUTTON = "site-folder-server-create-button";
            String ITEM = "site-folder-server-list-item";
            String NAME = "site-folder-server-name";
            String IP = "site-folder-server-ip";
            String APPS = "site-folder-server-apps";
            String COMMENT = "site-folder-server-comment";
            String PARAMS = "site-folder-server-params";
            String EDIT_BUTTON = "site-folder-server-edit-button";
            String COPY_BUTTON = "site-folder-server-copy-button";
            String REMOVE_BUTTON = "site-folder-server-remove-button";
        }
        interface SERVER_GROUP {
            String NAME = "site-folder-server-group-name";
        }
        interface LINK {
            String PLATFORM = "sitefolder-link-platform";
        }
    }

    public interface SUBNET {
        String ADDRESS_INPUT = "subnet-address-input";
        String MASK_INPUT = "subnet-mask-input";
        String COMMENT_INPUT = "subnet-comment-input";
        String ALLOW_RESERVE_CHECKBOX = "subnet-allow-reserve-checkbox";
        String SAVE_BUTTON = "subnet-save-button";
        String CANCEL_BUTTON = "subnet-cancel-button";
    }

    public interface RESERVED_IP {
        String MODE_TOGGLE = "reservedip-mode-toggle";
        String IP_ADDRESS_INPUT = "reservedip-address-input";
        String MAC_ADDRESS_INPUT = "reservedip-mac-address-input";
        String NUMBER_INPUT = "reservedip-number-input";
        String FREE_IP_COUNT_LABEL = "reservedip-free-ip-count-label";
        String SUBNET_SELECTOR = "reservedip-subnet-selector";
        String OWNER_SELECTOR = "reservedip-owner-selector";
        String USE_RANGE_TYPED_TOGGLE = "reservedip-use-range-typed-toggle";
        String USE_RANGE_TYPED_INPUT = "reservedip-use-range-typed-input";
        String USE_RANGE_TYPED_BUTTON = "reservedip-use-range-typed-button";
        String USE_RANGE_INPUT = "reservedip-use-range-input";
        String USE_RANGE_BUTTON = "reservedip-use-range-button";
        String COMMENT_INPUT = "reservedip-comment-input";
        String LAST_ACTIVE_DATE = "reservedip-last-active-date";
        String LAST_CHECK_INFO = "reservedip-last-сheck-info";
        String SAVE_BUTTON = "reservedip-save-button";
        String CANCEL_BUTTON = "reservedip-cancel-button";
    }

    public interface PLAN_PREVIEW {
        String FULL_SCREEN_BUTTON = "plan-preview-full-screen-button";
        String TITLE_LABEL = "plan-preview-title-label";
        String DATE_CREATED_LABEL = "plan-preview-date-created-label";
        String NAME_LABEL = "plan-preview-name-label";
        String PERIOD_LABEL = "plan-preview-period-label";
    }

    public interface ABSENCE {
        String EMPLOYEE_SELECTOR_LABEL = "absence-employee-selector-label";
        String EMPLOYEE_SELECTOR = "absence-employee-selector";
        String DATE_RANGE_LABEL = "absence-date-range-label";
        String DATE_RANGE_INPUT = "absence-date-range-input";
        String DATE_RANGE_BUTTON = "absence-date-range-button";
        String REASON_SELECTOR_LABEL = "absence-reason-selector-label";
        String REASON_SELECTOR = "absence-reason-selector";
        String COMMENT_LABEL = "absence-comment-label";
        String COMMENT_INPUT = "absence-comment-input";
        String DATE_RANGE_CONTAINER = "absence-date-range-container";
        String DATE_RANGE_CONTAINER_ADD_DAY_BUTTON = "absence-date-range-container-add-day-button";
        String DATE_RANGE_CONTAINER_ADD_WEEK_BUTTON = "absence-date-range-container-add-week-button";
    }

    public interface ABSENCE_REPORT {
        String NAME_LABEL = "absence-report-name-label";
        String NAME_INPUT = "absence-report-name-input";
        String DATE_RANGE_INPUT = "absence-report-date-range-input";
        String DATE_RANGE_BUTTON = "absence-report-date-range-button";
        String DATE_RANGE_LABEL = "absence-report-date-range-label";
        String EMPLOYEE_SELECTOR_ADD_BUTTON = "absence-report-employee-selector-add-button";
        String EMPLOYEE_SELECTOR_CLEAR_BUTTON = "absence-report-employee-selector-clear-button";
        String EMPLOYEE_SELECTOR_ITEM_CONTAINER = "absence-report-employee-selector-item-container";
        String EMPLOYEE_SELECTOR_LABEL = "absence-report-employee-selector-label";
        String REASON_SELECTOR_ADD_BUTTON = "absence-report-reason-selector-add-button";
        String REASON_SELECTOR_CLEAR_BUTTON = "absence-report-reason-selector-clear-button";
        String REASON_SELECTOR_ITEM_CONTAINER = "absence-report-reason-selector-item-container";
        String REASON_SELECTOR_LABEL = "absence-report-reason-selector-label";
        String SORT_FIELD_LABEL = "absence-report-sort-field-selector-label";
        String SORT_FIELD_SELECTOR = "absence-report-sort-field-selector";
        String SORT_DIR_BUTTON = "absence-report-sort-dir-button";
    }

    public interface ROOM_RESERVATION {
        interface FILTER {
            String DATE_RANGE_INPUT = "room-reservation-filter-date-range-input";
            String ROOM_SELECTOR_ADD_BUTTON = "room-reservation-filter-room-selector-add-button";
            String ROOM_SELECTOR_CLEAR_BUTTON = "room-reservation-filter-room-selector-clear-button";
            String ROOM_SELECTOR_ITEM_CONTAINER = "room-reservation-filter-room-selector-item-container";
            String ROOM_SELECTOR_LABEL = "room-reservation-filter-room-selector-label";
        }
    }

    public interface DUTY_LOG {
        interface EDIT {
            String EMPLOYEE_SELECTOR = "duty-log-employee-selector";
            String DATE_RANGE_INPUT = "duty-log-date-range-input";
            String TYPE_SELECTOR = "duty-log-type-selector";
        }

        interface REPORT {
            String NAME_LABEL = "duty-log-report-name-label";
            String NAME_INPUT = "duty-log-report-name-input";
        }

        interface FILTER {
            String DATE_RANGE = "duty-log-filter-date-range-input";
            String EMPLOYEE = "duty-log-filter-employee-selector";
            String TYPE = "duty-log-filter-type-selector";
        }
    }

    public interface GROUP {
        String ROOT = "group-root";
        String CONTAINER = "group-container";
        String ITEM = "group-item";
    }

    public interface DELIVERY {
        String NAME = "delivery-name";
        String DESCRIPTION = "delivery-description";
        String KITS = "delivery-kits";
        String STATE_SELECTOR = "delivery-state-selector";
        String TYPE_SELECTOR = "delivery-type-selector";
        String PROJECT_WIDGET = "delivery-project-widget";
        String CUSTOMER_TYPE = "delivery-customer-type";
        String CUSTOMER_COMPANY = "delivery-customer-company";
        String CUSTOMER_INITIATOR = "delivery-customer-initiator-selector";
        String CONTRACT_COMPANY = "delivery-contract-company";
        String TEAM = "delivery-team";
        String ATTRIBUTE = "delivery-attribute-selector";
        String CONTRACT = "delivery-contract-selector";
        String PRODUCTS = "delivery-products-selector";
        String DEPARTURE_DATE = "delivery-departure-date-container";
        String SUBSCRIBERS = "delivery-subscribers-container";
        String BACK_BUTTON = "delivery-back-button";
        String SHOW_EDIT_BUTTON = "delivery-show-edit-button";
        String SAVE_BUTTON = "delivery-save-button";
        String CANCEL_BUTTON = "delivery-cancel-button";
        String EDIT_NAME_AND_DESCRIPTION_BUTTON = "delivery-edit-name-and-desc-button";
        String ADD_KITS_BUTTON = "delivery-add-kits-button";
        String TAB_COMMENT = "delivery-tab-comments";
        String TAB_HISTORY = "delivery-tab-history";

        interface KIT {
            String ITEM = "kit-item";
            String SERIAL_NUMBER = "kit-item-serial-number-input";
            String STATE = "kit-item-state-selector";
            String NAME = "kit-item-name-input";
            String ADD_BUTTON = "kit-item-add-button";
            String REFRESH_BUTTON = "kit-item-refresh-button";
            String REMOVE_BUTTON = "kit-item-remove-button";
            String SAVE_BUTTON = "kit-item-save-button";
            String CANCEL_BUTTON = "kit-item-cancel-button";
            String TAB_HISTORY = "kit-item-tab-history";
            String BACK_BUTTON = "kit-back-button";
            String ACTION_MENU_BUTTON = "kit-action-menu-button";

            interface MODULE {
                String NAME = "module-name-input";
                String DESCRIPTION = "module-description-input";
                String STATE = "module-state-selector";
                String MANAGER = "module-manager";
                String HW_MANAGER = "module-hw-manager-selector";
                String QC_MANAGER = "module-qc-manager-selector";
                String CUSTOMER_COMPANY = "module-customer-company";
                String BUILD_DATE = "module-build-date-container";
                String DEPARTURE_DATE = "module-departure-date-container";
                String ADD_BUTTON = "module-add-button";
                String SAVE_BUTTON = "module-save-button";
                String CANCEL_BUTTON = "module-cancel-button";
                String EDIT_NAME_AND_DESCRIPTION_BUTTON = "module-edit-name-and-desc-button";
                String EDIT_NAME_AND_DESC_ACCEPT = "module-edit-name-and-desc-accept";
                String EDIT_NAME_AND_DESC_REJECT = "module-edit-name-and-desc-reject";
                String TAB_COMMENT = "module-tab-comments";
                String TAB_HISTORY = "module-tab-history";
            }
        }

        interface FILTER {
            String MILITARY_YES = "delivery-filter-military-yes";
            String MILITARY_ANY = "delivery-filter-military-any";
            String MILITARY_NO = "delivery-filter-military-no";
            String STATE_LABEL = "delivery-filter-state-label";
        }
    }

    public interface CONFIRM_DIALOG {
        String OK_BUTTON = "confirm-dialog-ok-button";
        String CANCEL_BUTTON = "confirm-dialog-cancel-button";
    }

    public interface YOUTRACK_WORK {
        String TABLE = "youtrack-work-report-table-";

        interface DIALOG {
            String NAME = "youtrack-work-report-dialog-name";
            String TYPE = "youtrack-work-report-dialog-type";
            String PROJECTS = "youtrack-work-report-dialog-projects";
            String PROJECTS_ADD = "youtrack-work-report-dialog-projects-add";
            String PROJECTS_CLEAR = "youtrack-work-report-dialog-projects-clear";
            String PROJECTS_ITEM_CONTAINER = "youtrack-work-report-dialog-projects-item-container";
        }
    }

    public interface CARD {
        String SAVE_BUTTON = "card-save-button";
        String CANCEL_BUTTON = "card-cancel-button";

        String SERIAL_NUMBER = "card-serial-number";

        String EDIT_NOTE_COMMENT_BUTTON = "card-edit-note-comment-button";
        String NOTE_COMMENT_SAVE_BUTTON = "card-note-comment-save-button";
        String NOTE_COMMENT_CANCEL_BUTTON = "card-note-comment-cancel-button";
        String NOTE = "card-note";
        String COMMENT = "card-comment";

        String TAB_HISTORY = "card-tab-history";

        String STATE = "card-state-selector";
        String TYPE = "card-type-selector";
        String CARD_BATCH = "card-card-batch-selector";
        String ARTICLE = "card-article-input";
        String MANAGER = "card-manager-selector";
        String TEST_DATE = "card-test-date-container";
    }

    public interface CARD_BATCH {
        String BACK_BUTTON = "card-batch-back-button";
        String SAVE_BUTTON = "card-batch-save-button";
        String CANCEL_BUTTON = "card-batch-cancel-button";
        String TYPE = "card-batch-type-selector";
        String NUMBER_INPUT = "card-batch-number-input";
        String ARTICLE = "card-batch-article-input";
        String AMOUNT = "card-batch-amount-input";
        String PARAMS = "card-batch-params-input";
        String STATE_SELECTOR = "card-batch-state-selector";
        String PRIORITY_SELECTOR = "card-batch-priority-selector";
        String DEADLINE_DATE = "card-batch-deadline-date-container";
        String CONTRACTOR_SELECTOR = "card-batch-contractor-selector";
        String NUMBER = "card-batch-number";
        String EDIT_COMMON_INFO_BUTTON = "card-batch-edit-common-info-button";
        String TAB_COMMENT = "card-batch-tab-comments";
        String TAB_HISTORY = "card-batch-tab-history";
    }
}
