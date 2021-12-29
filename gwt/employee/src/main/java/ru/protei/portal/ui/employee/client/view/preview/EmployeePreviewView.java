package ru.protei.portal.ui.employee.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.employee.client.activity.preview.AbstractEmployeePreviewActivity;
import ru.protei.portal.ui.employee.client.activity.preview.AbstractEmployeePreviewView;

/**
 * Представление превью сотрудника
 */
public class EmployeePreviewView extends Composite implements AbstractEmployeePreviewView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractEmployeePreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setID(String value) {
        this.id.setInnerText(value);
    }

    @Override
    public void setIP(String ip) {
        this.ip.setInnerText(ip);
    }

    @Override
    public void setLogins(String logins) {
        this.login.setInnerText(logins);
    }

    @Override
    public void setRestVacationDays(String restVacationDays) {
        this.restVacationDays.setInnerText(restVacationDays);
    }

    @Override
    public void setPhotoUrl(String url) {
        photo.setUrl(url);
    }

    @Override
    public void setName(String name) {
        this.employeeName.setText(name);
    }

    @Override
    public void setBirthday(String birthday) {
        this.birthday.setInnerText(birthday);
    }

    @Override
    public void setPhones(String phones) {
        this.phones.setInnerText(phones);
    }

    @Override
    public void setEmail(String email) {
        this.email.setInnerHTML(email);
    }

    @Override
    public HasVisibility birthdayContainerVisibility() {
        return birthdayContainer;
    }

    @Override
    public HasVisibility phonesContainerVisibility() {
        return phonesContainer;
    }

    @Override
    public HasVisibility emailContainerVisibility() {
        return emailContainer;
    }

    @Override
    public HasWidgets positionsContainer() {
        return positionsContainer;
    }

    @Override
    public HasWidgets absencesContainer() {
        return absencesContainer;
    }

    public void showAbsencesPanel(boolean isShow) {
        if (isShow) {
            absencesPanel.removeClassName("hide");
        } else {
            absencesPanel.addClassName("hide");
        }
    }
    @Override
    public void showFullScreen(boolean isFullScreen) {
        backButtonPanel.setVisible(isFullScreen);
        rootWrapper.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
    }

    @Override
    public Element getRestVacationDaysLoading() {
        return restVacationDaysLoading;
    }

    @UiHandler("backButton")
    public void onBackButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onBackButtonClicked();
        }
    }

    @UiHandler("employeeName")
    public void onFullScreenClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onFullScreenClicked();
        }
    }

    @UiHandler("createAbsenceButton")
    public void onCreateAbsenceButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCreateAbsenceButtonClicked();
        }
    }

    @UiField
    HTMLPanel rootWrapper;

    @UiField
    SpanElement id;

    @UiField
    HTMLPanel positionsContainer;

    @UiField
    SpanElement ip;

    @UiField
    SpanElement login;

    @UiField
    Element restVacationDaysLoading;

    @UiField
    SpanElement restVacationDays;

    @UiField
    Image photo;

    @UiField
    Anchor employeeName;

    @UiField
    SpanElement birthday;

    @UiField
    SpanElement phones;

    @UiField
    SpanElement email;

    @UiField
    HTMLPanel birthdayContainer;

    @UiField
    HTMLPanel phonesContainer;

    @UiField
    HTMLPanel emailContainer;

    @UiField
    HTMLPanel absencesContainer;

    @UiField
    HTMLPanel backButtonPanel;

    @UiField
    Button backButton;

    @UiField
    DivElement absencesPanel;

    @UiField
    Button createAbsenceButton;

    AbstractEmployeePreviewActivity activity;

    private static EmployeePreviewViewUiBinder ourUiBinder = GWT.create( EmployeePreviewViewUiBinder.class );
    interface EmployeePreviewViewUiBinder extends UiBinder< HTMLPanel, EmployeePreviewView > {}
}
