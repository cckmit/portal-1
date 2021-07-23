package ru.protei.portal.core.model.dict;

/**
 * Категории привилегий
 */
public enum En_PrivilegeEntity {
    PROFILE(1),
    ISSUE(12),
    ISSUE_ASSIGNMENT(3),
    REGION(10),
    PRODUCT(8),
    PROJECT(9),
    EQUIPMENT(13),
    COMPANY(6),
    CONTACT(7),
    ACCOUNT(15),
    ROLE(14),
    OFFICIAL(21),
    DASHBOARD(2),
    DOCUMENT(16),
    DOCUMENT_TYPE(17),
    CASE_STATES(18),
    SITE_FOLDER(19),
    EMPLOYEE(4),
    EMPLOYEE_REGISTRATION(11),
    CONTRACT(20),
    SUBNET(22),
    RESERVED_IP(23),
    ROOM_RESERVATION(5),
    PLAN(24),
    ABSENCE(25),
    EDUCATION(26),
    DUTY_LOG(27),
    DELIVERY(28),
    YT(29),
    ;

    private final Integer order;

    En_PrivilegeEntity(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }
}
