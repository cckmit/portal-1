package ru.protei.portal.ui.document.client.widget.projectlist.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

public class ProjectItem extends Composite implements HasValue<Boolean> {
    public ProjectItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public Boolean getValue() {
        return radio.getValue();
    }

    @Override
    public void setValue(Boolean value) {
        radio.setValue(value);
    }

    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        radio.setValue(value, fireEvents);
    }

    public void setCreated(String created) {
        this.created.setText(created == null ? "" : created);
    }

    public void setInfo(String info) {
        this.info.setText(info == null ? "" : info);
    }

    public void setProducts(String products) {
        this.products.setText(products == null ? "" : products);
    }

    public void setCustomerType(String customerType) {
        this.customerType.setText(customerType == null ? "" : customerType);
    }

    public void setManagers(String managers) {
        this.managers.setText(managers == null ? "" : managers);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler( "radio" )
    public void onRadioButtonClicked(ValueChangeEvent<Boolean> event) {
        ValueChangeEvent.fire(this, event.getValue());

        radio.setValue(event.getValue(), false);
    }


    @UiField
    RadioButton radio;
    @UiField
    Label created;
    @UiField
    Label info;
    @UiField
    Label products;
    @UiField
    Label customerType;
    @UiField
    Label managers;

    private static ProjectItemUiBinder ourUiBinder = GWT.create(ProjectItemUiBinder.class);
    interface ProjectItemUiBinder extends UiBinder<HTMLPanel, ProjectItem> {}
}