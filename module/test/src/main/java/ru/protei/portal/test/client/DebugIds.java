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
    }

    public interface ISSUE {
        String BACK_BUTTON = "issue-back-button";
        String SHOW_EDIT_BUTTON = "issue-show-edit-button";
        String LINKS_BUTTON = "issue-links-button";
        String TAGS_BUTTON = "issue-tags-button";
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
        String EDIT_NAME_AND_DESC_ACCEPT = "issue-edit-name-and-desc-accept";
        String EDIT_NAME_AND_DESC_REJECT = "issue-edit-name-and-desc-reject";

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
            String TIME_ELAPSED_TYPE = "issue-label-time-elapsed-type";
        }
    }

    public interface CASE_COMMENT {

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
    }

    @Deprecated public interface ISSUE_PREVIEW {
        @Deprecated String FULL_SCREEN_BUTTON = DebugIds.ISSUE.SHOW_EDIT_BUTTON;
        @Deprecated String PLATFORM = DebugIds.SITE_FOLDER.LINK.PLATFORM;

        @Deprecated interface COMMENT_LIST {
            @Deprecated String COMMENTS_LIST = DebugIds.CASE_COMMENT.COMMENT_LIST.COMMENTS_LIST;
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
            @Deprecated String STATUS = DebugIds.CASE_COMMENT.COMMENT_ITEM.STATUS;
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
        String DIRECTION_SELECTOR = "project-direction-selector";
        String CUSTOMER_TYPE_SELECTOR = "project-customer-type-selector";
        String COMPANY_SELECTOR = "project-company-selector";
        String LINKS_BUTTON = "project-links-button";

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
        String CONTRACT_LABEL = "project-preview-contract-label";
        String PLATFORM_LABEL = "project-preview-platform-label";
    }

    public interface COMPANY_TABLE {
        String LOCK_ICON = "company-table-lock-icon";
    }

    public interface COMPANY_PREVIEW {
        String TAGS_CONTAINER = "company-preview-tags-container";
        String SUBSCRIPTION = "company-preview-subscription-item";

        interface LABEL {
            String SUBSCRIPTION = "company-preview-label-subscription-label";
            String CONTACT_INFO = "company-preview-contact-info-label";
        }
    }

    public interface PRODUCT_TABLE {
        String LOCK_ICON = "product-table-lock-icon";
        interface FILTER {
            String SHOW_DEPRECATED = "product-filter-show-deprecated-button";
            String TYPES = "product-filter-types-selector";
        }
    }

    public interface PRODUCT {
        String NAME = "product-name";
        String WIKI_LINK = "product-wiki-link";
        String DESCRIPTION = "product-description";
        String SUBSCRIPTIONS = "product-subscriptions";
        String SUBSCRIPTIONS_ITEM = "product-subscriptions-item";
        String SUBSCRIPTIONS_ITEM_LANG_CODE = "product-subscriptions-item-lang-code";
        String SUBSCRIPTIONS_ITEM_EMAIL = "product-subscriptions-item-email";
        String INCLUDES = "product-includes";
        String PRODUCTS = "product-products";
        String ALIASES = "product-aliases";
        String HISTORY_VERSION = "product-history-version";
        String CONFIGURATION = "product-configuration";
        String CDR_DESCRIPTION = "product-cdr-description";
        String SAVE_BUTTON = "product-save-button";
        String CANCEL_BUTTON = "product-cancel-button";

        interface TAB {
            String HISTORY_VERSION = "product-tab-history-version";
            String CONFIGURATION = "product-tab-configuration";
            String CDR_DESCRIPTION = "product-tab-cdr-description";
        }
    }

    public interface PRODUCT_PREVIEW {
        String NAME = "product-preview-name";
        String WIKI_LINK = "product-preview-wiki-link";
        String DESCRIPTION = "product-preview-description";
        String HISTORY_VERSION = "product-preview-history-version";
        String CONFIGURATION = "product-preview-configuration";
        String CDR_DESCRIPTION = "product-preview-cdr-description";
        String BACK_BUTTON = "product-preview-back-button";

        interface TAB {
            String HISTORY_VERSION = "product-preview-tab-history-version";
            String CONFIGURATION = "product-preview-tab-configuration";
            String CDR_DESCRIPTION = "product-preview-tab-cdr-description";
        }
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
            String PROJECT_SELECTOR = "document-filter-project-selector";
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
        }
    }

    public interface PROJECT_LIST {
        String ITEM = "project-list-item";
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

    public interface SITE_FOLDER {

        interface LINK {
            String PLATFORM = "sitefolder-link-platform";
        }
    }
}
