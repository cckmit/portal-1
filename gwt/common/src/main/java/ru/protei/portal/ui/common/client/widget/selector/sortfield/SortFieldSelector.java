package ru.protei.portal.ui.common.client.widget.selector.sortfield;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.lang.En_SortFieldLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор списка сортировки
 */
public class SortFieldSelector extends ButtonSelector< En_SortField > {

    public void setType( ModuleType type ) {
        this.type = type;
        setDisplayOptionCreator( value -> new DisplayOption( sortFieldLang.getName( value ) ) );

        fillOptions();
    }

    public void fillOptions() {
        clearOptions();

        switch ( type ) {
            case COMPANY:
                addOption( En_SortField.comp_name );
                addOption( En_SortField.creation_date );
                break;
            case PRODUCT:
                addOption( En_SortField.prod_name );
                addOption( En_SortField.creation_date );
                break;
            case CONTACT:
                addOption( En_SortField.person_full_name );
                addOption( En_SortField.person_position );
                addOption( En_SortField.comp_name );
                break;
            case ISSUE:
                addOption( En_SortField.issue_number );
                addOption( En_SortField.last_update );
                break;
            case EQUIPMENT:
                addOption( En_SortField.name );
                addOption( En_SortField.equipment_name_sldwrks );
                addOption( En_SortField.equipment_project );
                addOption( En_SortField.equipment_decimal_number );
                break;
            case ACCOUNT:
                addOption( En_SortField.ulogin );
                addOption( En_SortField.person_full_name );
                break;
            case ROLE:
                addOption( En_SortField.role_name );
                break;
            case OFFICIAL:
                addOption( En_SortField.creation_date );
                addOption( En_SortField.region_name );
                addOption( En_SortField.prod_name );
                break;
            case DOCUMENT:
                addOption( En_SortField.name );
                addOption( En_SortField.document_project );
                addOption( En_SortField.creation_date );
                break;
            case DOCUMENT_TYPE:
                addOption( En_SortField.name );
                break;
            case SITE_FOLDER:
                addOption( En_SortField.name );
                break;
            case SITE_FOLDER_SERVER:
                addOption(En_SortField.name);
                addOption(En_SortField.server_ip);
                break;
            case EMPLOYEE:
                addOption( En_SortField.person_full_name );
                addOption( En_SortField.birthday );
                addOption( En_SortField.employee_ip);
                break;
            case EMPLOYEE_REGISTRATION:
                addOption(En_SortField.creation_date);
                break;
            case CONTRACT:
                addOption(En_SortField.contract_creation_date);
                addOption(En_SortField.contract_signing_date);
                break;
            case PROJECT:
                addOption( En_SortField.project_name );
                addOption( En_SortField.project_number );
                addOption( En_SortField.region_name );
                break;
            case REGION:
                addOption( En_SortField.name );
                break;
            case SUBNET:
                addOption( En_SortField.address );
                break;
            case RESERVED_IP:
                addOption( En_SortField.ip_address );
                addOption( En_SortField.active_date );
                break;
            case PLAN:
                addOption( En_SortField.name );
                addOption( En_SortField.creation_date );
                addOption( En_SortField.start_date );
                addOption( En_SortField.finish_date );
                break;
            case ABSENCE:
                addOption(En_SortField.absence_person);
                addOption(En_SortField.absence_date_from);
                addOption(En_SortField.absence_date_till);
                addOption(En_SortField.absence_reason);
                break;
            case DUTY_LOG:
                addOption(En_SortField.duty_log_date_from);
                addOption(En_SortField.duty_log_employee);
                addOption(En_SortField.duty_log_type);
                break;
            case DELIVERY:
                addOption(En_SortField.delivery_creation_date);
                addOption(En_SortField.delivery_departure_date);
                addOption(En_SortField.delivery_case_name);
                break;
            case CARD:
                addOption(En_SortField.card_serial_number);
                addOption(En_SortField.card_test_date);
        }
    }

    @Inject
    private En_SortFieldLang sortFieldLang;

    private ModuleType type;
}
