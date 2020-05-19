package ru.protei.portal.core.model.util;

import java.util.Arrays;
import java.util.List;

public class CrmConstants {

    public static final int DEFAULT_SELECTOR_PAGE_SIZE = 20;
    public static final int DEFAULT_SELECTOR_CHUNK_SIZE = 100;
    public static final int DEFAULT_SELECTOR_SAVED_CHUNKS = 100;
    public static final int EMAIL_MAX_SIZE = 254;

    public static final String SOME_LINKS_NOT_SAVED = "some links not saved";

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

    public interface State {
        long CREATED = 1L;
        long OPENED = 2L;
        long WORKAROUND = 30L;
        long TEST_CUST = 20L;
        long DONE = 17L;
        long VERIFIED = 5L;
        long PAUSED = 4L;
        long CANCELED = 33L;
        long ACTIVE = 16L;
        long TEST_LOCAL = 19L;
        long INFO_REQUEST = 31L;
        long NX_REQUEST = 35L;
        long CUST_REQUEST = 36L;
        long CUST_PENDING = 34L;
        long CLOSED = 3L;
        long IGNORED = 10L;
    }
}
