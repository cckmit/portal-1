package ru.protei.portal.core.model.util;

public class CrmConstants {

    public interface Auth {
        String SESSION_DESC = "auth-session-data";
    }

    public interface Header {
        String USER_AGENT = "User-Agent";
    }

    public interface Product {
        Long UNDEFINED = -1L;
    }

    public interface Issue {
        String CREATE_CONTACT_IDENTITY = "issue-edit";
        int MIN_LENGTH_FOR_SEARCH_BY_COMMENTS = 3;
    }

    public interface FileUpload {
        String FILE_ITEM_DESC = "file-item";
    }

    public interface Youtrack {
        String EQUIPMENT_PROJECT_NAME = "PG";
        String ADMIN_PROJECT_NAME = "PG";
    }
}
