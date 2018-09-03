package ru.protei.portal.ui.questionnaire.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_EmployeeEquipment;
import ru.protei.portal.core.model.dict.En_EmploymentType;
import ru.protei.portal.core.model.dict.En_InternalResource;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;
import java.util.Set;

public interface AbstractQuestionnaireEditView extends IsWidget {

    void setActivity(AbstractQuestionnaireEditActivity activity);

    HasValue<String> fullName();

    HasValue<PersonShortView> headOfDepartment();

    HasValue<Date> employmentDate();

    HasValue<En_EmploymentType> employmentType();

    HasValue<Boolean> withRegistration();

    HasValue<String> post();

    HasValue<String> comment();

    HasValue<String> workplaceInfo();

    HasValue<Set<En_EmployeeEquipment>> equipmentList();

    HasValue<Set<En_InternalResource>> resourcesList();

    HasValidable fullNameValidation();

    HasValidable postValidation();
}
