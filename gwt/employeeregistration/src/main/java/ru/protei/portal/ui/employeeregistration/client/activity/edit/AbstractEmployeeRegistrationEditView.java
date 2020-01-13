package ru.protei.portal.ui.employeeregistration.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_EmployeeEquipment;
import ru.protei.portal.core.model.dict.En_EmploymentType;
import ru.protei.portal.core.model.dict.En_InternalResource;
import ru.protei.portal.core.model.dict.En_PhoneOfficeType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;
import java.util.Set;

public interface AbstractEmployeeRegistrationEditView extends IsWidget {

    void setActivity(AbstractEmployeeRegistrationEditActivity activity);

    HasValue<String> fullName();

    HasValue<PersonShortView> headOfDepartment();

    HasValue<Date> employmentDate();

    HasValue<En_EmploymentType> employmentType();

    HasValue<Boolean> withRegistration();

    HasValue<String> position();

    HasValue<String> comment();

    HasValue<String> workplace();

    HasValue<Set<En_EmployeeEquipment>> equipmentList();

    HasValue<Set<En_InternalResource>> resourcesList();

    HasValue<Set<En_PhoneOfficeType>> phoneOfficeTypeList();

    HasValidable fullNameValidation();

    HasValidable positionValidation();

    HasValidable headOfDepartmentValidation();

    void setEmploymentDateValid(boolean isValid);

    HasEnabled saveEnabled();

    HasValue<Integer> probationPeriod();

    HasValue<String> resourceComment();

    HasValue<String> operatingSystem();

    HasValue<String> additionalSoft();

    HasValue<Set<PersonShortView>> curators();

    HasVisibility workplaceErrorLabelVisibility();

    void setWorkplaceErrorLabel(String errorMsg);

    HasVisibility positionErrorLabelVisibility();

    void setPositionErrorLabel(String errorMsg);

    HasVisibility additionalSoftErrorLabelVisibility();

    void setAdditionalSoftErrorLabel(String errorMsg);

    HasVisibility resourceCommentErrorLabelVisibility();

    void setResourceCommentErrorLabel(String errorMsg);

    HasVisibility operatingSystemErrorLabelVisibility();

    void setOperatingSystemErrorLabel(String errorMsg);
}
