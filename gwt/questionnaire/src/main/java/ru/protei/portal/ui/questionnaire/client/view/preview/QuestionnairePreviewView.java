package ru.protei.portal.ui.questionnaire.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.questionnaire.client.activity.preview.AbstractQuestionnairePreviewActivity;
import ru.protei.portal.ui.questionnaire.client.activity.preview.AbstractQuestionnairePreviewView;

public class QuestionnairePreviewView extends Composite implements AbstractQuestionnairePreviewView {
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
    public void setActivity( AbstractQuestionnairePreviewActivity activity ) {
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


    @Inject
    @UiField
    Lang lang;

    @Inject
    FixedPositioner positioner;

    private AbstractQuestionnairePreviewActivity activity;

    private static QuestionnairePreviewViewUiBinder ourUiBinder = GWT.create( QuestionnairePreviewViewUiBinder.class );
    interface QuestionnairePreviewViewUiBinder extends UiBinder< HTMLPanel, QuestionnairePreviewView> {}
}