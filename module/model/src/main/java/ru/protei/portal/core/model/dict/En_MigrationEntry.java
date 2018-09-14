package ru.protei.portal.core.model.dict;

import ru.protei.portal.core.model.helper.HelperFunc;

public enum En_MigrationEntry {

    COMPANY ("Tm_Company"),
    PERSON_CUSTOMER ("Tm_Person"),
    PERSON_EMPLOYEE ("Tm_Person_Protei"),
    PROJECT ("Tm_Project"),
    PRODUCT ("Tm_Product"),
    CLIENT_LOGIN ("Tm_ClientLogin"),
    COMPANY_EMAIL_SUBS ("Tm_CompanyEmailSubscription"),
    BUG ("BugTracking.Tm_Bug"),
    CRM_SUPPORT_SESSION("CRM.SUPPORT_SESSION"),
    CRM_MARKETING_SESSION("CRM.MARKETING_SESSION"),
    CRM_SUPPORT_SESSION_COMMENT("CRM.SUPPORT_COMMENT"),
    YOUTRACK_EMPLOYEE_REGISTRATION_ISSUE("YOUTRACK_EMPLOYEE_REGISTRATION_ISSUE");


    En_MigrationEntry (String code) {
        this.code = code;
    }

    private final String code;

    public String getCode() {
        return code;
    }

    public boolean matches (String code) {
        return this.code.equalsIgnoreCase(code);
    }


    public static En_MigrationEntry find (String code) {
        return HelperFunc.find(En_MigrationEntry.class, null, e -> e.matches(code));
    }
}
