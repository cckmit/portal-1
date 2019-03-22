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
    OFFICIAL_CREATE( 30),

    ROLE_REMOVE( 31 ),

    EMPLOYEE_MODIFY( 32 ),
    EMPLOYEE_CREATE( 33 ),

    DEPARTMENT_MODIFY( 34 ),
    DEPARTMENT_CREATE( 35 ),
    DEPARTMENT_REMOVE( 36 ),

    WORKER_MODIFY( 37 ),
    WORKER_CREATE( 38 ),
    WORKER_REMOVE( 39 ),

    POSITION_MODIFY( 40 ),
    POSITION_CREATE( 41 ),
    POSITION_REMOVE( 42 ),

    PHOTO_UPLOAD( 43 ),

    DOCUMENT_MODIFY(44),
    DOCUMENT_REMOVE(45),

    EMPLOYEE_REGISTRATION_CREATE(46),

    PROJECT_REMOVE(47),

    EMPLOYEE_REGISTRATION_MODIFY(48),

    CONTRACT_MODIFY(49),
    CONTRACT_CREATE(50),
    ;

    En_AuditType(int id ) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private final int id;
}
