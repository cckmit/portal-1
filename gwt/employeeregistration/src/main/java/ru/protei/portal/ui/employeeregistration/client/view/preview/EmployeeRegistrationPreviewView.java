package ru.protei.portal.ui.employeeregistration.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.CaseStateUtils;
import ru.protei.portal.ui.employeeregistration.client.activity.preview.AbstractEmployeeRegistrationPreviewActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.preview.AbstractEmployeeRegistrationPreviewView;


public class EmployeeRegistrationPreviewView extends Composite implements AbstractEmployeeRegistrationPreviewView {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractEmployeeRegistrationPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setFullName(String fullName) {
        this.fullName.setText(fullName);
    }

    @Override
    public void setHeadOfDepartment(String headOfDepartment) {
        this.headOfDepartment.setInnerText(headOfDepartment);
    }

    @Override
    public void setEmploymentDate(String date) {
        this.employmentDate.setInnerText(date);
    }

    @Override
    public void setEmploymentType(String employmentType) {
        this.employmentType.setInnerText(employmentType);
    }

    @Override
    public void setPosition(String position) {
        this.position.setInnerText(position);
    }

    @Override
    public void setComment(String comment) {
        this.comment.setInnerText(comment);
    }

    @Override
    public void setWorkplace(String workplace) {
        this.workplace.setInnerText(workplace);
    }

    @Override
    public void setEquipmentList(String equipmentList) {
        this.equipmentList.setInnerText(equipmentList);
    }

    @Override
    public void setResourceList(String resourcesList) {
        this.resourcesList.setInnerText(resourcesList);
    }

    @Override
    public void setPhoneOfficeTypeList( String phoneOfficeTypeList ) {
        this.phoneOfficeTypeList.setInnerText(phoneOfficeTypeList);
    }

    @Override
    public void setCurators( String curators ) {
        this.curators.setInnerText(curators);
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML(value);
    }

    @Override
    public void setState(String state, String color) {
        if (state == null) {
            this.caseState.setClassName("");
            this.caseState.setInnerText("");
        } else {
            this.caseState.getStyle().setBackgroundColor(color);
            this.caseState.setInnerText(state);
        }
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentContainer;
    }

    @Override
    public void setDepartment(String value) {
        department.setInnerText(value);
    }

    @Override
    public HasWidgets getLinksContainer() {
        return linksContainer;
    }

    @Override
    public void setProbationPeriodMonth( String probationPeriodMonth ) {
        probationPeriod.setInnerText(probationPeriodMonth);
    }

    @Override
    public void setOperatingSystem( String operatingSystem ) {
        this.operatingSystem.setInnerText( operatingSystem );
    }

    @Override
    public void setResourceComment( String resourceComment ) {
        this.resourceComment.setInnerText( resourceComment );
    }

    @Override
    public void setAdditionalSoft( String additionalSoft ) {
        this.additionalSoft.setInnerText( additionalSoft );
    }

    @Override
    public void setCompany(String company) {
        this.company.setInnerText(company);
    }

    @UiHandler( "fullName" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();

        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @Override
    public void showFullScreen(boolean isFullScreen) {
        backButtonPanel.setVisible(isFullScreen);
        previewWrapperContainer.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
    }

    @UiHandler("backButton")
    public void onBackButtonClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onBackButtonClicked();
        }
    }


    @UiField
    Anchor fullName;
    @UiField
    SpanElement headOfDepartment;
    @UiField
    SpanElement employmentDate;
    @UiField
    SpanElement employmentType;
    @UiField
    SpanElement position;
    @UiField
    SpanElement comment;
    @UiField
    SpanElement workplace;
    @UiField
    SpanElement equipmentList;
    @UiField
    SpanElement resourcesList;
    @UiField
    SpanElement phoneOfficeTypeList;
    @UiField
    SpanElement probationPeriod;
    @UiField
    SpanElement resourceComment;
    @UiField
    SpanElement operatingSystem;
    @UiField
    SpanElement additionalSoft;
    @UiField
    SpanElement caseState;
    @UiField
    HTMLPanel commentContainer;
    @UiField
    SpanElement curators;
    @UiField
    SpanElement company;
    @UiField
    HTMLPanel previewWrapperContainer;
    @UiField
    HTMLPanel backButtonPanel;
    @UiField
    Button backButton;


    @Inject
    @UiField
    Lang lang;
    @UiField
    Element createdBy;
    @UiField
    HTMLPanel linksContainer;
    @UiField
    SpanElement department;

    private AbstractEmployeeRegistrationPreviewActivity activity;

    private static PreviewViewUiBinder ourUiBinder = GWT.create( PreviewViewUiBinder.class );
    interface PreviewViewUiBinder extends UiBinder< HTMLPanel, EmployeeRegistrationPreviewView> {}
}
