package ru.protei.portal.ui.plan.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.plan.client.activity.edit.AbstractPlanEditActivity;
import ru.protei.portal.ui.plan.client.activity.edit.AbstractPlanEditView;

public class PlanEditView extends Composite implements AbstractPlanEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractPlanEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public void setHeader(String value) { this.header.setText( value ); }

    @Override
    public void setCreatedBy(String value) { this.createdBy.setInnerHTML( value ); }

    @Override
    public HasValidable nameValidator() {
        return name;
    }

    @Override
    public HasValue<DateInterval> planPeriod() { return planPeriod; }

    @Override
    public HasWidgets unassignedTableContainer() {
        return unassignedTableContainer;
    }

    @Override
    public HasWidgets assignedTableContainer() {
        return assignedTableContainer;
    }

    @Override
    public HasVisibility editButtonVisibility() {
        return editPlanButton;
    }

    @Override
    public HasVisibility saveButtonVisibility() {
        return saveButton;
    }

    @Override
    public HasVisibility cancelButtonVisibility() {
        return cancelButton;
    }

    @Override
    public HasVisibility backButtonVisibility() {
        return backButton;
    }

    @Override
    public HasEnabled nameEnabled() {
        return name;
    }

    @Override
    public HasEnabled periodEnabled(){
        return planPeriod;
    }

    @UiHandler("saveButton")
    public void saveButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void cancelButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }
    @UiHandler("backButton")
    public void backButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onBackClicked();
        }
    }


    @UiHandler("editPlanButton")
    public void editButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onEditClicked();
        }
    }

    @UiField
    Element createdBy;
    @UiField
    Label header;
    @UiField
    ValidableTextBox name;
    @Inject
    @UiField(provided = true)
    RangePicker planPeriod;
    @UiField
    HTMLPanel unassignedTableContainer;
    @UiField
    HTMLPanel assignedTableContainer;
    @UiField
    Button editPlanButton;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @UiField
    Button backButton;


    private AbstractPlanEditActivity activity;

    private static PlanEditViewUiBinder ourUiBinder = GWT.create(PlanEditViewUiBinder.class);
    interface PlanEditViewUiBinder extends UiBinder<HTMLPanel, PlanEditView> {}
}