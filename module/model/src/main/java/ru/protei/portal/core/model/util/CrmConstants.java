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
    }

    public interface FileUpload {
        String FILE_ITEM_DESC = "file-item";
    }
}
