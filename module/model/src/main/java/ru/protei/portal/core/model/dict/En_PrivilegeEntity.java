package ru.protei.portal.core.model.dict;


/**
 * Категории привилегий
 */
public enum En_PrivilegeEntity {
    PROFILE(1),
    ISSUE(11),
    ISSUE_ASSIGNMENT(3),
    REGION(9),
    PRODUCT(7),
    PROJECT(8),
    EQUIPMENT(12),
    COMPANY(5),
    CONTACT(6),
    ACCOUNT(14),
    ROLE(13),
    OFFICIAL(20),
    DASHBOARD(2),
    DOCUMENT(15),
    DOCUMENT_TYPE(16),
    CASE_STATES(17),
    SITE_FOLDER(18),
    EMPLOYEE(4),
    EMPLOYEE_REGISTRATION(10),
    CONTRACT(19);

    private final Integer order;

    En_PrivilegeEntity(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }
}
