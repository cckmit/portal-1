package ru.protei.portal.test.client;

public class DebugIdsHelper {
    public static final class IMPORTANCE_BUTTON {
        public static String byId(long id) {
            if (id == 1) {
                return DebugIds.IMPORTANCE_BUTTON.CRITICAL;
            }
            if (id == 2) {
                return DebugIds.IMPORTANCE_BUTTON.IMPORTANT;
            }
            if (id == 3) {
                return DebugIds.IMPORTANCE_BUTTON.BASIC;
            }
            if (id == 4) {
                return DebugIds.IMPORTANCE_BUTTON.COSMETIC;
            }
            if (id == 5) {
                return DebugIds.IMPORTANCE_BUTTON.MEDIUM;
            }
            if (id == 6) {
                return DebugIds.IMPORTANCE_BUTTON.EMERGENCY;
            }
            if (id == 7) {
                return DebugIds.IMPORTANCE_BUTTON.HIGH;
            }
            if (id == 8) {
                return DebugIds.IMPORTANCE_BUTTON.LOW;
            }
            return DebugIds.IMPORTANCE_BUTTON.DEFAULT + id;
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
            if (id == 38) {
                return DebugIds.ISSUE_STATE.BLOCKED;
            }
            if (id == 39) {
                return DebugIds.ISSUE_STATE.DELIVERY_PRELIMINARY;
            }
            if (id == 40) {
                return DebugIds.ISSUE_STATE.DELIVERY_PRE_RESERVE;
            }
            if (id == 41) {
                return DebugIds.ISSUE_STATE.DELIVERY_RESERVE;
            }
            if (id == 42) {
                return DebugIds.ISSUE_STATE.DELIVERY_ASSEMBLY;
            }
            if (id == 43) {
                return DebugIds.ISSUE_STATE.DELIVERY_TEST;
            }
            if (id == 44) {
                return DebugIds.ISSUE_STATE.DELIVERY_READY;
            }
            if (id == 45) {
                return DebugIds.ISSUE_STATE.DELIVERY_SENT;
            }
            if (id == 46) {
                return DebugIds.ISSUE_STATE.DELIVERY_WORK;
            }
            return DebugIds.ISSUE_STATE.DEFAULT + id;
        }
    }
    public static final class LOCALE_BUTTON {
        public static String byLocale(String locale) {
            if ("en".equals(locale)) {
                return DebugIds.LOCALE_BUTTON.EN;
            }
            if ("ru".equals(locale)) {
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

    public static final class PRIVACY_TYPE {
        public static String byType(String type) {
            if ("public".equals(type)) {
                return DebugIds.PRIVACY_TYPE.PUBLIC;
            }
            if ("private_customer".equals(type)) {
                return DebugIds.PRIVACY_TYPE.PRIVATE_CUSTOMER;
            }
            if ("private".equals(type)) {
                return DebugIds.PRIVACY_TYPE.PRIVATE;
            }
            return DebugIds.PRIVACY_TYPE.DEFAULT + type;
        }
    }

    public static final class PROJECT_STATE {
        public static String byId(long id) {
            if (id == 4) {
                return DebugIds.PROJECT_STATE.PAUSED;
            }
            if (id == 22) {
                return DebugIds.PROJECT_STATE.UNKNOWN;
            }
            if (id == 23) {
                return DebugIds.PROJECT_STATE.MARKETING;
            }
            if (id == 24) {
                return DebugIds.PROJECT_STATE.PRESALE;
            }
            if (id == 25) {
                return DebugIds.PROJECT_STATE.PROJECTING;
            }
            if (id == 26) {
                return DebugIds.PROJECT_STATE.DEVELOPMENT;
            }
            if (id == 27) {
                return DebugIds.PROJECT_STATE.DEPLOYMENT;
            }
            if (id == 28) {
                return DebugIds.PROJECT_STATE.TESTING;
            }
            if (id == 29) {
                return DebugIds.PROJECT_STATE.SUPPORT;
            }
            if (id == 32) {
                return DebugIds.PROJECT_STATE.FINISHED;
            }
            if (id == 33) {
                return DebugIds.PROJECT_STATE.CANCELED;
            }
            return DebugIds.PROJECT_STATE.DEFAULT + id;
        }
    }

    public static final class CARD_BATCH_STATE {
        public static String byId(long id) {
            if (id == 52) {
                return DebugIds.CARD_BATCH_STATE.IN_QUEUE_BUILD_EQUIPMENT;
            }
            if (id == 53) {
                return DebugIds.CARD_BATCH_STATE.BUILD_EQUIPMENT;
            }
            if (id == 54) {
                return DebugIds.CARD_BATCH_STATE.IN_QUEUE_AUTOMATIC_MOUNTING;
            }
            if (id == 55) {
                return DebugIds.CARD_BATCH_STATE.AUTOMATIC_MOUNTING;
            }
            if (id == 58) {
                return DebugIds.CARD_BATCH_STATE.IN_QUEUE_MANUAL_MOUNTING;
            }
            if (id == 59) {
                return DebugIds.CARD_BATCH_STATE.MANUAL_MOUNTING;
            }
            if (id == 60) {
                return DebugIds.CARD_BATCH_STATE.IN_QUEUE_STICKER_LABELING;
            }
            if (id == 61) {
                return DebugIds.CARD_BATCH_STATE.STICKER_LABELING;
            }
            if (id == 62) {
                return DebugIds.CARD_BATCH_STATE.TRANSFERRED_FOR_TESTING;
            }

            return DebugIds.CARD_BATCH_STATE.DEFAULT + id;
        }
    }
}
