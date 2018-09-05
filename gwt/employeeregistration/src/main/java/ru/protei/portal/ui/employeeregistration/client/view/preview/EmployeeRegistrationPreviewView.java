package ru.protei.portal.ui.employeeregistration.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.employeeregistration.client.activity.preview.AbstractEmployeeRegistrationPreviewActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.preview.AbstractEmployeeRegistrationPreviewView;

public class EmployeeRegistrationPreviewView extends Composite implements AbstractEmployeeRegistrationPreviewView {
    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch( this, FixedPositioner.NAVBAR_TOP_OFFSET );
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore( this );
    }

    @Override
    public void setActivity( AbstractEmployeeRegistrationPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setFullName(String fullName) {
        this.fullName.setInnerText(fullName);
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
    public void setPost(String post) {
        this.post.setInnerText(post);
    }

    @Override
    public void setComment(String comment) {
        this.comment.setInnerText(comment);
    }

    @Override
    public void setWorkplaceInfo(String workplaceInfo) {
        this.workplaceInfo.setInnerText(workplaceInfo);
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
    public void setWithRegistration(String withRegistration) {
        this.withRegistration.setInnerText(withRegistration);
    }

    @Override
    public void setCreated(String created) {
        this.created.setInnerText(created);
    }

    @Override
    public void setState(En_CaseState state) {
        if (state == null) {
            this.caseState.setClassName("");
            this.caseState.setInnerText("");
        } else {
            this.caseState.setClassName("small label label-" + state.getName().toLowerCase());
            this.caseState.setInnerText(caseStateLang.getStateName(state));
        }
    }

    @UiField
    SpanElement fullName;
    @UiField
    SpanElement headOfDepartment;
    @UiField
    SpanElement employmentDate;
    @UiField
    SpanElement employmentType;
    @UiField
    SpanElement post;
    @UiField
    SpanElement comment;
    @UiField
    SpanElement workplaceInfo;
    @UiField
    SpanElement equipmentList;
    @UiField
    SpanElement resourcesList;
    @UiField
    SpanElement withRegistration;
    @UiField
    SpanElement created;
    @UiField
    DivElement caseState;


    @Inject
    @UiField
    Lang lang;

    @Inject
    En_CaseStateLang caseStateLang;

    @Inject
    FixedPositioner positioner;

    private AbstractEmployeeRegistrationPreviewActivity activity;

    private static PreviewViewUiBinder ourUiBinder = GWT.create( PreviewViewUiBinder.class );
    interface PreviewViewUiBinder extends UiBinder< HTMLPanel, EmployeeRegistrationPreviewView> {}
}