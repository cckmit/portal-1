package ru.protei.portal.ui.employee.client.activity.edit;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;

public interface AbstractEmployeeEditView extends IsWidget {

    void setActivity(AbstractEmployeeEditActivity activity);

    HasEnabled firstNameEnabled();

    HasEnabled lastNameEnabled();

    HasEnabled secondNameEnabled();

    HasEnabled birthDayEnabled();

    HasEnabled genderEnabled();

    HasEnabled workEmailEnabled();

    HasEnabled mobilePhoneEnabled();

    HasEnabled workPhoneEnabled();

    HasEnabled ipAddressEnabled();

    HasValue<String> firstName();

    HasValue<String> lastName();

    HasValue<Boolean> contractAgreement();

    HasText secondName();

    HasValue<Date> birthDay ();

    void setBirthDayTimeZone (TimeZone timeZone);

    HasText workPhone ();

    HasText mobilePhone();

    HasValue<String> workEmail();

    HasValue<String> ipAddress();

    HasValue<EntityOption> workerPosition();

    HasValue<EntityOption> companyDepartment();

    HasValue<EntityOption> company();

    HasValue<En_Gender> gender ();

    HasValidable firstNameValidator();

    HasValidable lastNameValidator();

    void companyDepartmentSelectorReload();

    void workerPositionSelectorReload();

    HasValidable genderValidator();

    HasVisibility fireBtnVisibility();

    HasVisibility firedMsgVisibility();

    HasValidable workEmailValidator();

    HasValidable ipAddressValidator();

    HasVisibility firstNameErrorLabelVisibility();

    HasVisibility secondNameErrorLabelVisibility();

    HasVisibility lastNameErrorLabelVisibility();

    String firstNameLabel();

    String secondNameLabel();

    String lastNameLabel();

    String ipAddressLabel();

    String workEmailLabel();

    HasEnabled saveEnabled();

    HasValue<Boolean> changeAccount();

    HasVisibility changeAccountVisibility();

    HasWidgets getPositionsContainer();

    void updateCompanyDepartments(Long companyId);

    void setAddButtonCompanyDepartmentVisible(boolean isVisible);

    void updateWorkerPositions(Long companyId);

    void setAddButtonWorkerPositionVisible(boolean isVisible);

    HandlerRegistration addChangeHandler(ChangeHandler changeHandler);

    void submitAvatar(String url);

    void setFileUploadEnabled(boolean isEnabled);

    HandlerRegistration addSubmitCompleteHandler(FormPanel.SubmitCompleteHandler submitCompleteHandler);

    void setAvatarUrl(String url);

    void setAvatarLabelText(String text);

    void refreshHomeCompanies(Boolean isSynchronize);
}
