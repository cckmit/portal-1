package ru.protei.portal.ui.employeeregistration.client.activity.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

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

    void setCurators( String curators );

    void setCreatedBy(String created);

    void setState(String state);

    void setPhoneOfficeTypeList( String phoneOfficeTypeList );

    HasWidgets getLinksContainer();

    void setProbationPeriodMonth(String probationPeriodMonth );

    void setOperatingSystem( String operatingSystem );

    void setResourceComment( String resourceComment );

    void setAdditionalSoft( String additionalSoft );

    void showFullScreen(boolean isFullScreen);

    HasWidgets getCommentsContainer();
}
