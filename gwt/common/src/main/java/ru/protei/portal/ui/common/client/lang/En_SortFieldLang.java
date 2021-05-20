package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;


/**
 * Поля сортировки
 */
public class En_SortFieldLang {
    public String getName( En_SortField value) {
        switch (value)
        {
            case creation_date:
            case contract_creation_date:
            case project_creation_date:
                return lang.created();
            case name:
                return lang.name();
            case prod_name:
                return lang.name();
            case comp_name:
                return lang.name();
            case project_name:
                return lang.name();
            case project_number:
                return lang.projectNumber();
            case last_update:
                return lang.updated();
            case person_full_name:
                return lang.contactFullName();
            case person_position:
                return lang.contactPosition();
            case issue_number:
                return lang.issueNumber();
            case region_name:
                return lang.officialRegion();
            case role_name:
                return lang.roleName();
            case ulogin:
                return lang.accountLogin();
            case project:
            case document_project:
                return lang.documentProject();
            case equipment_project:
                return lang.equipmentProject();
            case equipment_name_sldwrks:
                return lang.equipmentNameBySldWrks();
            case equipment_decimal_number:
                return lang.equipmentDecimalNumber();
            case birthday:
                return lang.birthday();
            case employee_ip:
                return lang.employeeIPAddress();
            case address:
                return lang.reservedIpSubnetAddress();
            case ip_address:
                return lang.reservedIpIpAddress();
            case active_date:
                return lang.reservedIpNonActiveRange();
            case start_date:
                return lang.planStartDate();
            case finish_date:
                return lang.planFinishDate();
            case author_id:
                return lang.issueCommentAuthor();
            case absence_date_from:
                return lang.absenceFromTime();
            case absence_date_till:
                return lang.absenceTillTime();
            case absence_person:
                return lang.contactFullName();
            case absence_reason:
                return lang.absenceReason();
            case contract_signing_date:
                return lang.signingDate();
            case duty_log_date_from:
                return lang.dutyLogSortDateFrom();
            case duty_log_employee:
                return lang.dutyLogEmployee();
            case duty_log_type:
                return lang.dutyLogType();
            case by_plan:
                return lang.planSort();
            case server_ip:
                return lang.siteFolderIP();
            case delivery_case_name:
                return lang.deliveryName();
            case delivery_departure_date:
                return lang.deliveryDepartureDate();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
