package ru.protei.portal.ui.employee.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;

public interface AbstractEmployeeEditView extends IsWidget {

    void setActivity(AbstractEmployeeEditActivity activity);

    HasEnabled firstNameEnabled();

    HasEnabled lastNameEnabled();

    HasEnabled secondNameEnabled();

    HasEnabled birthDayEnabled();

    HasEnabled genderEnabled();

    HasEnabled personalEmailEnabled();

    HasEnabled workEmailEnabled();

    HasEnabled mobilePhoneEnabled();

    HasEnabled workPhoneEnabled();

    HasEnabled ipAddressEnabled();

    HasValue<String> firstName();

    HasValue<String> lastName();

    HasText secondName();

    HasValue<Date> birthDay ();

    HasText workPhone ();

    HasText mobilePhone();

    HasText workEmail();

    HasText personalEmail();

    HasText ipAddress();

    HasValue<EntityOption> workerPosition();

    HasValue<EntityOption> companyDepartment();

    HasEnabled companyDepartmentEnabled();

    HasEnabled workerPositionEnabled();

    HasValue<EntityOption> company();

    HasValue<En_Gender> gender ();

    HasValidable companyValidator();

    HasValidable firstNameValidator();

    HasValidable lastNameValidator();

    void companyDepartmentSelectorReload();

    void workerPositionSelectorReload();

    HasValidable companyDepartmentValidator();

    HasValidable workerPositionValidator();

    HasValidable genderValidator();

    HasVisibility saveVisibility();

    HasEnabled companyEnabled ();

    HasVisibility fireBtnVisibility();

    HasVisibility firedMsgVisibility();

    HasValidable workEmailValidator();

    HasValidable personalEmailValidator();

    HasValidable ipAddressValidator();

    HasVisibility firstNameErrorLabelVisibility();

    HasVisibility secondNameErrorLabelVisibility();

    HasVisibility lastNameErrorLabelVisibility();

    HasText firstNameErrorLabel();

    HasText secondNameErrorLabel();

    HasText lastNameErrorLabel();

    String firstNameLabel();

    String secondNameLabel();

    String lastNameLabel();

    String personalEmailLabel();

    String ipAddressLabel();

    String workEmailLabel();

    HasEnabled saveEnabled();

    void updateCompanyDepartments(Long companyId);

    void setAddButtonCompanyDepartmentVisible(boolean isVisible);

    void updateWorkerPositions(Long companyId);

    void setAddButtonWorkerPositionVisible(boolean isVisible);

}
