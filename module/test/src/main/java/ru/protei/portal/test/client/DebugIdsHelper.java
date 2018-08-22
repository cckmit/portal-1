package ru.protei.portal.test.client;

public class DebugIdsHelper {

    public static final class COMPANY_CATEGORY_BUTTON {
        public static String byId(Long id) {
            if (id == 1) {
                return DebugIds.COMPANY_CATEGORY_BUTTON.CUSTOMER;
            }
            if (id == 2) {
                return DebugIds.COMPANY_CATEGORY_BUTTON.PARTNER;
            }
            if (id == 3) {
                return DebugIds.COMPANY_CATEGORY_BUTTON.SUBCONTRACTOR;
            }
            if (id == 4) {
                return DebugIds.COMPANY_CATEGORY_BUTTON.OFFICIAL;
            }
            if (id == 5) {
                return DebugIds.COMPANY_CATEGORY_BUTTON.HOME_COMPANY;
            }
            return DebugIds.COMPANY_CATEGORY_BUTTON.DEFAULT + id;
        }
    }

    public static final class IMPORTANCE_BUTTON {
        public static String byId(int id) {
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
            return DebugIds.IMPORTANCE_BUTTON.DEFAULT + id;
        }
    }

    public static final class ISSUE_STATE {
        public static String byId(int id) {
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
            if (id == 32) {
                return DebugIds.ISSUE_STATE.CUST_PENDING;
            }
            return DebugIds.ISSUE_STATE.DEFAULT + id;
        }
    }
}
