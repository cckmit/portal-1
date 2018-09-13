package ru.protei.portal.ui.employeeregistration.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseLink;

import java.util.Set;

public interface AbstractEmployeeRegistrationPreviewView extends IsWidget {
    void setActivity(AbstractEmployeeRegistrationPreviewActivity activity);

    void setFullName(String fullName);

    void setHeadOfDepartment(String headOfDepartment);

    void setEmploymentDate(String date);

    void setEmploymentType(String employmentType);

    void setPosition(String post);

    void setComment(String comment);

    void setWorkplace(String workplace);

    void setEquipmentList(String equipmentList);

    void setResourceList(String resourcesList);

    void setWithRegistration(String withRegistration);

    void setCreated(String created);

    void setState(En_CaseState state);

    void setIssues(Set<CaseLink> issues);
}
