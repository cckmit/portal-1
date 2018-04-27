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

    public interface Person {
        Long CREATE_NEW_PERSON_ID = -1L;
    }
}
