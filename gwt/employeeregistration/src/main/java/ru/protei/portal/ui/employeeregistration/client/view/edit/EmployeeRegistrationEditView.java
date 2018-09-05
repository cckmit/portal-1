package ru.protei.portal.ui.employeeregistration.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_EmployeeEquipment;
import ru.protei.portal.core.model.dict.En_EmploymentType;
import ru.protei.portal.core.model.dict.En_InternalResource;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.optionlist.item.OptionItem;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.employeeregistration.client.activity.edit.AbstractEmployeeRegistrationEditActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.edit.AbstractEmployeeRegistrationEditView;
import ru.protei.portal.ui.employeeregistration.client.widget.optionlist.EmployeeEquipmentOptionList;
import ru.protei.portal.ui.employeeregistration.client.widget.optionlist.InternalResourceOptionList;
import ru.protei.portal.ui.employeeregistration.client.widget.selector.EmploymentTypeSelector;

import java.util.Date;
import java.util.Set;

public class EmployeeRegistrationEditView extends Composite implements AbstractEmployeeRegistrationEditView {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
    
    @Override
    public void setActivity(AbstractEmployeeRegistrationEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> fullName() {
        return fullName;
    }

    @Override
    public HasValue<PersonShortView> headOfDepartment() {
        return headOfDepartment;
    }

    @Override
    public HasValue<Date> employmentDate() {
        return employmentDate;
    }

    @Override
    public HasValue<En_EmploymentType> employmentType() {
        return employmentType;
    }

    @Override
    public HasValue<Boolean> withRegistration() {
        return withRegistration;
    }

    @Override
    public HasValue<String> post() {
        return post;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @Override
    public HasValue<String> workplaceInfo() {
        return workplaceInfo;
    }

    @Override
    public HasValue<Set<En_EmployeeEquipment>> equipmentList() {
        return equipmentList;
    }

    @Override
    public HasValue<Set<En_InternalResource>> resourcesList() {
        return resourcesList;
    }

    @Override
    public HasValidable fullNameValidation() {
        return fullName;
    }

    @Override
    public HasValidable postValidation() {
        return post;
    }

    @Override
    public HasValidable headOfDepartmentValidation() {
        return headOfDepartment;
    }

    @Override
    public void setEmploymentDateValid(boolean isValid) {
        employmentDate.markInputValid(isValid);
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @UiField
    Button saveButton;

    @UiField
    ValidableTextBox fullName;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector headOfDepartment;

    @Inject
    @UiField(provided = true)
    SinglePicker employmentDate;

    @Inject
    @UiField(provided =  true)
    EmploymentTypeSelector employmentType;

    @Inject
    @UiField(provided = true)
    OptionItem withRegistration;

    @UiField
    ValidableTextBox post;

    @UiField
    AutoResizeTextArea comment;

    @UiField
    AutoResizeTextArea workplaceInfo;

    @Inject
    @UiField(provided = true)
    InternalResourceOptionList resourcesList;

    @Inject
    @UiField(provided = true)
    EmployeeEquipmentOptionList equipmentList;

    @Inject
    Lang lang;

    private AbstractEmployeeRegistrationEditActivity activity;

    private static EmployeeRegistrationViewUiBinder ourUiBinder = GWT.create(EmployeeRegistrationViewUiBinder.class);
    interface EmployeeRegistrationViewUiBinder extends UiBinder<HTMLPanel, EmployeeRegistrationEditView> {
    }
}
