package ru.protei.portal.core.model.util;

public class CrmConstants {

    public interface Session {
        String AUTH_TOKEN = "session-auth-token";
        String FILE_ITEM = "file-item";
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
        String EMAIL = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,4})+$";
    }

    public interface Time {
        long SEC = 1000L;
        long MINUTE = 60 * SEC;
        long HOUR = 60 * MINUTE;
        long DAY = 24 * HOUR;
    }
}
