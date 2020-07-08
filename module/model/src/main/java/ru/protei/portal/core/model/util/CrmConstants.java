package ru.protei.portal.core.model.util;

import java.util.Arrays;
import java.util.List;

public class CrmConstants {

    public static final int DEFAULT_SELECTOR_PAGE_SIZE = 20;
    public static final int DEFAULT_SELECTOR_CHUNK_SIZE = 100;
    public static final int DEFAULT_SELECTOR_SAVED_CHUNKS = 100;
    public static final int EMAIL_MAX_SIZE = 254;

    public static final String SOME_LINKS_NOT_SAVED = "some links not saved";
    public static final String SOME_PLANS_NOT_UPDATED = "some plans not updated";

    public static final String DEFAULT_LOCALE = "ru";

    public interface Session {
        String AUTH_TOKEN = "session-auth-token";
        String FILE_ITEM = "file-item";
        String FILE_ITEM_PDF = "file-item-pdf";
        String FILE_ITEM_DOC = "file-item-doc";
        String FILE_ITEM_APPROVAL_SHEET = "file-item-approval-sheet";
    }

    public interface Header {
        String USER_AGENT = "User-Agent";
    }

    public interface Product {
        Long UNDEFINED = -1L;
    }

    public interface Employee {
        Long UNDEFINED = -1L;
    }

    public interface EmployeeRegistration {
        int ADDITIONAL_SOFT_MAX_LENGTH = 512;
        int RESOURCE_COMMENT_MAX_LENGTH = 512;
        int OPERATING_SYSTEM_MAX_LENGTH = 64;
        int POSITION_MAX_LENGTH = 128;
        int WORKPLACE_MAX_LENGTH = 256;
    }

    public interface Person {
        Long SYSTEM_USER_ID = 1L;
    }

    public interface TopBrassPerson {
        long PINCHUK_PERSON_ID = 29;
        long APOSTOLOVA_PERSON_ID = 4;
        long KOLOBKOV_PERSON_ID= 20;
        long FREYKMAN_PERSON_ID = 45;
        long MASLOV_PERSON_ID = 25;
    }

    public interface Issue {
        String CREATE_CONTACT_IDENTITY = "issue-edit";
        int MIN_LENGTH_FOR_SEARCH_BY_COMMENTS = 3;
    }

    public interface CaseTag {
        Long NOT_SPECIFIED = -1L;
    }

    public interface Masks {
        String EMAIL = "^[-a-zA-Z0-9_\\.]+@[-a-zA-Z0-9_\\.]+\\.\\w{2,4}$";
        String ONLY_DIGITS = "^\\d*$";
        String IP = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String COMPANY_NAME_ILLEGAL_CHARS = ".*[<>/]+.*";
        String CONTRACT_SPECIFICATION_CLAUSE = "^\\d{1,3}(\\.\\d{1,3})*$";
    }

    public interface Time {
        long SEC = 1000L;
        long MINUTE = 60 * SEC;
        long HOUR = 60 * MINUTE;
        long DAY = 24 * HOUR;
    }

    public interface ContactConstants {
        int FIRST_NAME_SIZE = 80;
        int SECOND_NAME_SIZE = 80;
        int LAST_NAME_SIZE = 80;
        int SHORT_NAME_SIZE = 128;
        int LOGIN_SIZE = 64;
    }

    public interface Comment {
        String TIME_ELAPSED_DEFAULT_COMMENT = "затраченное время";
    }

    public interface ClassifierCode {
        int MAX_SIZE = 6;
    }

    public interface RegistrationNumber {
        int MAX_SIZE = 3;
    }

    public interface Jira {
        String INFO_LINK = "jiraInfo";
        String WORKFLOW_IMAGE = "./images/jira-workflow.jpg";
    }

    public interface ImportanceLevel {
        List<Integer> commonImportanceLevelIds = Arrays.asList(1,2,3,4);
    }

    public interface Company {
        long HOME_COMPANY_ID = 1L;
        long MAIN_HOME_COMPANY_ID = 3084L;
        String MAIN_HOME_COMPANY_NAME = "Протей";
    }

    public interface LocaleTags {
        String RU = "ru";
        String EN = "en";
    }

    public interface LinkStart {
        String HTTP = "http://";
        String HTTPS = "https://";
    }

    public interface Redmine {
        String NO_CONTENT_TYPE = "application/octet-stream";
    }

    public interface IpReservation {
        String SUBNET_ADDRESS = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
        String IP_ADDRESS = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
        String MAC_ADDRESS = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        String NUMBER = "^\\d{1,3}$";

        String SUBNET_MASK = "0/24";

        int MIN_IPS_COUNT = 1;
        int MAX_IPS_COUNT = 255;

        int RELEASE_DATE_EXPIRES_IN_DAYS =  3;
    }

    public interface Youtrack {
        String REQUEST_TYPE_VALUE = "Удаление пользователей";
    }

    public interface State {
        String CREATED_NAME = "created";
        long CREATED = 1;
        long OPENED = 2;
        long WORKAROUND = 30;
        long TEST_CUST = 20;
        long DONE = 17;
        long VERIFIED = 5;
        long PAUSED = 4;
        long CANCELED = 33;
        long ACTIVE = 16;
        long TEST_LOCAL = 19;
        long INFO_REQUEST = 31;
        long NX_REQUEST = 35;
        long CUST_REQUEST = 36;
        long CUST_PENDING = 34;
        long CLOSED = 3;
        long IGNORED = 10;
        long REQUEST_TO_PARTNER = 37;
        long SOLVED_NOT_A_PROBLEM = 7;
        long SOLVED_FIXED = 8;
        long SOLVED_DUPLICATED = 9;
    }
}
