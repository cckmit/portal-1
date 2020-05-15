package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PrivilegeEntity;


public class En_PrivilegeEntityLang {

    public String getName( En_PrivilegeEntity value ) {
        switch (value) {
            case PROFILE:
                return lang.privilegeCategoryCommon();
            case ISSUE:
                return lang.privilegeCategoryIssue();
            case ISSUE_ASSIGNMENT:
                return lang.privilegeCategoryIssueAssignment();
            case REGION:
                return lang.privilegeCategoryRegion();
            case PROJECT:
                return lang.privilegeCategoryProject();
            case COMPANY:
                return lang.privilegeCategoryCompany();
            case PRODUCT:
                return lang.privilegeCategoryProduct();
            case CONTACT:
                return lang.privilegeCategoryContact();
            case ACCOUNT:
                return lang.privilegeCategoryAccount();
            case EQUIPMENT:
                return lang.privilegeCategoryEquipment();
            case ROLE:
                return lang.privilegeCategoryRole();
            case OFFICIAL:
                return lang.privilegeCategoryOfficial();
            case DASHBOARD:
                return lang.privilegeDashboard();
            case DOCUMENT:
                return lang.privilegeDocument();
            case DOCUMENT_TYPE:
                return lang.privilegeDocumentType();
            case CASE_STATES:
                return lang.privilegeCaseStates();
            case SITE_FOLDER:
                return lang.privilegeSiteFolder();
            case EMPLOYEE:
                return lang.privilegeCategoryEmployee();
            case EMPLOYEE_REGISTRATION:
                return lang.privilegeCategoryEmployeeRegistration();
            case CONTRACT:
                return lang.privilegeContract();
            case SUBNET:
                return lang.subnet();
            case RESERVED_IP:
                return lang.ipReservation();
            case ROOM_RESERVATION:
                return lang.roomReservation();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
