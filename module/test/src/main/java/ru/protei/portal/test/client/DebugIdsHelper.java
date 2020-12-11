package ru.protei.portal.test.client;

public class DebugIdsHelper {
    public static final class IMPORTANCE_BUTTON {
        public static String byCode(String importanceCode) {
            return DebugIds.IMPORTANCE_BUTTON.DEFAULT + importanceCode;
        }
    }

    public static final class ISSUE_STATE {
        public static String byId(long id) {
            if (id == 1) {
                return DebugIds.ISSUE_STATE.CREATED;
            }
            if (id == 2) {
                return DebugIds.ISSUE_STATE.OPENED;
            }
            if (id == 3) {
                return DebugIds.ISSUE_STATE.CLOSED;
            }
            if (id == 4) {
                return DebugIds.ISSUE_STATE.PAUSED;
            }
            if (id == 5) {
                return DebugIds.ISSUE_STATE.VERIFIED;
            }
            if (id == 6) {
                return DebugIds.ISSUE_STATE.REOPENED;
            }
            if (id == 7) {
                return DebugIds.ISSUE_STATE.SOLVED_NOAP;
            }
            if (id == 8) {
                return DebugIds.ISSUE_STATE.SOLVED_FIX;
            }
            if (id == 9) {
                return DebugIds.ISSUE_STATE.SOLVED_DUP;
            }
            if (id == 10) {
                return DebugIds.ISSUE_STATE.IGNORED;
            }
            if (id == 11) {
                return DebugIds.ISSUE_STATE.ASSIGNED;
            }
            if (id == 12) {
                return DebugIds.ISSUE_STATE.ESTIMATED;
            }
            if (id == 14) {
                return DebugIds.ISSUE_STATE.DISCUSS;
            }
            if (id == 15) {
                return DebugIds.ISSUE_STATE.PLANNED;
            }
            if (id == 16) {
                return DebugIds.ISSUE_STATE.ACTIVE;
            }
            if (id == 17) {
                return DebugIds.ISSUE_STATE.DONE;
            }
            if (id == 18) {
                return DebugIds.ISSUE_STATE.TEST;
            }
            if (id == 19) {
                return DebugIds.ISSUE_STATE.TEST_LOCAL;
            }
            if (id == 20) {
                return DebugIds.ISSUE_STATE.TEST_CUST;
            }
            if (id == 21) {
                return DebugIds.ISSUE_STATE.DESIGN;
            }
            if (id == 30) {
                return DebugIds.ISSUE_STATE.WORKAROUND;
            }
            if (id == 31) {
                return DebugIds.ISSUE_STATE.INFO_REQUEST;
            }
            if (id == 33) {
                return DebugIds.ISSUE_STATE.CANCELED;
            }
            if (id == 34) {
                return DebugIds.ISSUE_STATE.CUST_PENDING;
            }
            if (id == 35) {
                return DebugIds.ISSUE_STATE.NX_REQUEST;
            }
            if (id == 36) {
                return DebugIds.ISSUE_STATE.CUST_REQUEST;
            }
            if (id == 37) {
                return DebugIds.ISSUE_STATE.REQUEST_TO_PARTNER;
            }
            return DebugIds.ISSUE_STATE.DEFAULT + id;
        }
    }
    public static final class LOCALE_BUTTON {
        public static String byLocale(String locale) {
            if (locale.equals("en")) {
                return DebugIds.LOCALE_BUTTON.EN;
            }
            if (locale.equals("ru")) {
                return DebugIds.LOCALE_BUTTON.RU;
            }
            return DebugIds.LOCALE_BUTTON.DEFAULT + locale;
        }
    }
    public static final class PRODUCT_TYPE {
        public static String byId(int id) {
            if (id == 1) {
                return DebugIds.PRODUCT_TYPES_BUTTON.COMPONENT;
            }
            if (id == 2) {
                return DebugIds.PRODUCT_TYPES_BUTTON.PRODUCT;
            }
            if (id == 3) {
                return DebugIds.PRODUCT_TYPES_BUTTON.DIRECTION;
            }
            if (id == 4) {
                return DebugIds.PRODUCT_TYPES_BUTTON.COMPLEX;
            }
            return DebugIds.PRODUCT_TYPES_BUTTON.DEFAULT + id;
        }
    }
}
