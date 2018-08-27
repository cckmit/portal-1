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
                return lang.created();
            case name:
                return lang.name();
            case prod_name:
                return lang.name();
            case comp_name:
                return lang.name();
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
            case equipment_project:
                return lang.equipmentProject();
            case equipment_name_sldwrks:
                return lang.equipmentNameBySldWrks();
            case birthday:
                return lang.birthday();

            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
