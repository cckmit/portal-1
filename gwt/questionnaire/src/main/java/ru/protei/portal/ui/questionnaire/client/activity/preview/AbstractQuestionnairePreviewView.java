package ru.protei.portal.ui.questionnaire.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractQuestionnairePreviewView extends IsWidget {
    void setActivity(AbstractQuestionnairePreviewActivity activity);

    void setFullName(String fullName);

    void setHeadOfDepartment(String headOfDepartment);

    void setEmploymentDate(String date);

    void setEmploymentType(String employmentType);

    void setPost(String post);

    void setComment(String comment);

    void setWorkplaceInfo(String workplaceInfo);

    void setEquipmentList(String equipmentList);

    void setResourceList(String resourcesList);
}
