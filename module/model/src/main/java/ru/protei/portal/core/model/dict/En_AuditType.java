package ru.protei.portal.core.model.dict;

/**
 * Тип операции в системе
 */
public enum En_AuditType {
    ISSUE_MODIFY( 1 ),
    ISSUE_CREATE( 2 ),
    ISSUE_REPORT( 3 ),
    ISSUE_EXPORT( 4 ),

    REGION_MODIFY( 5 ),
    REGION_REPORT( 6 ),
    REGION_EXPORT( 7 ),

    PROJECT_MODIFY( 8 ),
    PROJECT_CREATE( 9 ),

    COMPANY_MODIFY( 10 ),
    COMPANY_CREATE( 11 ),

    PRODUCT_MODIFY( 12 ),
    PRODUCT_CREATE( 13 ),

    CONTACT_MODIFY( 14 ),
    CONTACT_CREATE( 15 ),

    ACCOUNT_MODIFY( 16 ),
    ACCOUNT_CREATE( 17 ),
    ACCOUNT_REMOVE( 18 ),

    EQUIPMENT_MODIFY( 19 ),
    EQUIPMENT_CREATE( 20 ),
    EQUIPMENT_REMOVE( 21 ),

    ROLE_MODIFY( 22 ),
    ROLE_CREATE( 23 ),

    ISSUE_COMMENT_CREATE( 24 ),
    ISSUE_COMMENT_MODIFY( 25 ),
    ISSUE_COMMENT_REMOVE( 26 ),

    ATTACHMENT_REMOVE( 27 ),

    EQUIPMENT_COPY( 28 ),

    OFFICIAL_MODIFY( 29 ),
    OFFICIAL_CREATE( 30);

    En_AuditType( int id ) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private final int id;
}
