package ru.protei.portal.ui.common.client.widget.departmentselector.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;

public class DepartmentSelectorItem extends Composite implements HasValue<CompanyDepartment>, HasAddHandlers, HasEditHandlers, HasClickHandlers {

    public DepartmentSelectorItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initDebugIds();
    }

    @Override
    public void setValue(CompanyDepartment value) {
        setValue(value, false);
    }

    @Override
    public void setValue(CompanyDepartment value, boolean fireEvents) {
        companyDepartment = value;

        text.setText(companyDepartment.getName());
        panel.setTitle(companyDepartment.getName());

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public CompanyDepartment getValue() {
        return companyDepartment;
    }

    public void setEditable(boolean isEditable) {
        if (isEditable) {
            editIcon.addStyleName("fas fa-pen");
        } else {
            editIcon.addStyleName("fas fa-external-link-alt");
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CompanyDepartment> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return addHandler(handler, AddEvent.getType());
    }

    @Override
    public HandlerRegistration addEditHandler(EditHandler handler) {
        return addHandler(handler, EditEvent.getType());
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addHandler(handler, ClickEvent.getType());
    }

    public HasVisibility editIconVisibility() {
        return editIcon;
    }

    @UiHandler({"text"})
    public void rootClick(ClickEvent event) {
        AddEvent.fire(this);
    }

    @UiHandler("editIcon")
    public void editClick(ClickEvent event) {
        ClickEvent.fireNativeEvent(event.getNativeEvent(), this);
    }

    private void initDebugIds() {
        /*panel.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TAG_SELECTOR_POPUP.ITEM);
        editIcon.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TAG_SELECTOR_POPUP.EDIT_BUTTON);
        text.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TAG_SELECTOR_POPUP.NAME);
        companyName.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TAG_SELECTOR_POPUP.COMPANY_NAME);*/
    }

    @UiField
    HTMLPanel panel;
    @UiField
    InlineLabel text;

    @UiField
    InlineLabel editIcon;

    @Inject
    PolicyService policyService;

    private CompanyDepartment companyDepartment = null;

    interface DepartmentViewUiBinder extends UiBinder<HTMLPanel, DepartmentSelectorItem> {}
    private static DepartmentViewUiBinder ourUiBinder = GWT.create(DepartmentViewUiBinder.class);
}
